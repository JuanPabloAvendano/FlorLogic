package co.florlogic.finca;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import org.junit.jupiter.api.Test;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.AccessTarget;
import com.tngtech.archunit.core.domain.JavaAccess;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaConstructor;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;

/**
 * TEC-05 (ArchUnit): la regla de dependencias dentro de cada hexágono, que {@link ModulosTest} no ve porque solo mira
 * los límites entre módulos (ESPEC-dominio-ingesta VER-2, 29-oct). Las flechas apuntan hacia adentro:
 * {@code infraestructura → servicios → dominio}, y el dominio no conoce a nadie (Martin, <i>Clean Architecture</i>,
 * «The Dependency Rule»; {@code .claude/CLAUDE.md}: «dominio es Java puro: sin Spring, sin SQL, sin contrato»).
 * Desde el Segmento R vigila también los nombres de ucotestapi (C-115): el controlador solo ve el puerto de entrada y
 * sus DTO, y los adaptadores orquestan sin construir dominio, que eso es del ensamblador (A-83, A-84).
 */
class CapasTest {

    private static final JavaClasses CLASES = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("co.florlogic.finca");

    // Llamada o referencia (Clase::de, Clase::new) a un constructor o a un método estático de cualquier dominio, también
    // el dominio raíz co.florlogic.finca.dominio (OBS-R-11).
    private static final DescribedPredicate<JavaAccess<?>> CREA_DOMINIO = DescribedPredicate.describe(
            "un constructor o un método estático del dominio, llamado o referenciado",
            acceso -> acceso.getTarget() instanceof AccessTarget.CodeUnitAccessTarget objetivo
                    && acceso.getTargetOwner().getPackageName().matches("co\\.florlogic\\.finca\\.(.*\\.)?dominio(\\..*)?")
                    && (objetivo.getName().equals(JavaConstructor.CONSTRUCTOR_NAME)
                            || objetivo.resolveMember().map(m -> m.getModifiers().contains(JavaModifier.STATIC)).orElse(false)));

    @Test
    void el_dominio_solo_depende_de_java_y_de_otro_dominio() {
        // Sin Spring, Jackson, JDBC ni el contrato: si alguien los importa en el dominio, esta prueba falla. Fuera:
        // package-info, que solo documenta el paquete (y en la raíz de un módulo lo declara con @ApplicationModule),
        // no es código del dominio.
        classes().that().resideInAPackage("co.florlogic.finca..dominio..").and().doNotHaveSimpleName("package-info")
                .should().onlyDependOnClassesThat().resideInAnyPackage("java..", "co.florlogic.finca..dominio..")
                .because("el dominio es Java puro y no conoce servicios, infraestructura ni el contrato")
                .check(CLASES);
    }

    @Test
    void los_servicios_no_dependen_de_la_infraestructura() {
        // Los servicios hablan con afuera por sus puertos (interfaces); el adaptador de infraestructura los implementa.
        noClasses().that().resideInAPackage("co.florlogic.finca..servicios..")
                .should().dependOnClassesThat().resideInAPackage("co.florlogic.finca..infraestructura..")
                .because("las dependencias apuntan hacia adentro: infraestructura → servicios → dominio")
                .check(CLASES);
    }

    @Test
    void el_controlador_solo_conoce_el_puerto_de_entrada_y_sus_dto() {
        // Regla (1) de R, angostada por A-103: el controlador habla HTTP y deja la traducción a controladores.ensamblador. Desde
        // 1b, solo ManejadorDeErrores podrá ver además las excepciones de servicios.output.ports (COH-189).
        // OBS-R-10: cubre también los subpaquetes nuevos de controladores, y de Spring solo lo web.
        classes().that().resideInAPackage("co.florlogic.finca..infraestructura.input.controladores..")
                .and().resideOutsideOfPackage("co.florlogic.finca..infraestructura.input.controladores.ensamblador..")
                .and().doNotHaveSimpleName("package-info").and().doNotHaveSimpleName("ManejadorDeErrores")
                // Los oyentes de eventos son adaptadores de entrada por evento, no HTTP (CT-12); Modulith vigila a quién ven.
                .and().haveSimpleNameNotEndingWith("Oyente")
                .should().onlyDependOnClassesThat().resideInAnyPackage(
                        "co.florlogic.finca..infraestructura.input.controladores..",
                        "co.florlogic.finca..servicios.input.ports..",
                        "co.florlogic.finca..servicios.input.dto..",
                        "java..", "org.springframework.web..", "org.springframework.http..", "org.slf4j..")
                .because("el controlador solo adapta HTTP al puerto de entrada; no conoce el dominio, el caso de uso ni el contrato")
                .check(CLASES);
    }

    @Test
    void el_manejador_de_errores_ademas_solo_ve_las_excepciones_de_los_puertos_de_salida() {
        // COH-189: la única excepción a la regla (1), para traducir ChoqueConcurrente en 409 (A-98).
        classes().that().haveSimpleName("ManejadorDeErrores")
                .should().onlyDependOnClassesThat().resideInAnyPackage(
                        "co.florlogic.finca..infraestructura.input.controladores..",
                        "co.florlogic.finca..servicios.output.ports..",
                        "java..", "org.springframework.web..", "org.springframework.http..", "org.slf4j..")
                .because("solo el manejador traduce las excepciones con nombre de los puertos a respuestas HTTP")
                .check(CLASES);
    }

    @Test
    void solo_el_ensamblador_del_controlador_conoce_el_contrato_y_jackson() {
        // A-103: el ensamblador del controlador traduce entre el contrato y los DTO del puerto; no ve HTTP ni el dominio.
        classes().that().resideInAPackage("co.florlogic.finca..infraestructura.input.controladores.ensamblador..")
                .and().doNotHaveSimpleName("package-info")
                .should().onlyDependOnClassesThat().resideInAnyPackage(
                        "co.florlogic.finca..infraestructura.input.controladores.ensamblador..",
                        "co.florlogic.finca..servicios.input.dto..",
                        "java..", "org.slf4j..", "tools.jackson..", "co.florlogic.contrato..")
                .because("el ensamblador del controlador solo traduce el contrato a los DTO del puerto de entrada (Clean Architecture p. 198)")
                .check(CLASES);
    }

    @Test
    void fuera_del_ensamblador_del_controlador_nadie_conoce_el_contrato() {
        // OBS-R-9, A-103: el contrato es el lenguaje de la comunicación y solo lo leen sus adaptadores; también el que
        // calcula la huella con la forma canónica del contrato (A-106, COH-201).
        noClasses().that().resideInAPackage("co.florlogic.finca..")
                .and().resideOutsideOfPackage("co.florlogic.finca..infraestructura.input.controladores.ensamblador..")
                .and().resideOutsideOfPackage("co.florlogic.finca.ingesta.infraestructura.output.serviciosexternos..")
                .should().dependOnClassesThat().resideInAnyPackage("co.florlogic.contrato..")
                .because("el contrato solo lo leen los adaptadores (A-103)")
                .check(CLASES);
    }

    @Test
    void jackson_solo_en_infraestructura() {
        // Leer JSON es un detalle de infraestructura (Clean Architecture p. 198): ni el dominio ni los servicios lo ven.
        noClasses().that().resideInAPackage("co.florlogic.finca..")
                .and().resideOutsideOfPackage("co.florlogic.finca..infraestructura..")
                .should().dependOnClassesThat().resideInAnyPackage("tools.jackson..")
                .because("leer JSON es un detalle de infraestructura")
                .check(CLASES);
    }

    @Test
    void los_adaptadores_de_servicios_no_construyen_dominio() {
        // Regla (2) de R: crear un valor del dominio (constructor o fábrica estática) es trabajo del ensamblador, del
        // caso de uso, del mapper o del propio dominio; el adaptador solo orquesta.
        noClasses().that().resideInAnyPackage(
                        "co.florlogic.finca..servicios.input.adapter..", "co.florlogic.finca..servicios.output.adapter..")
                .should().accessTargetWhere(CREA_DOMINIO)
                .because("adaptador ≠ ensamblador: solo el ensamblador traduce entre DTO y dominio (A-84)")
                .check(CLASES);
    }

}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - ClassFileImporter().withImportOption(DO_NOT_INCLUDE_TESTS).importPackages("co.florlogic.finca") (ArchUnit):
 *   lee los .class ya compilados del backend, sin las clases de prueba, y los guarda en CLASES (JavaClasses) para
 *   que todas las reglas los revisen; se importa una sola vez por ser static final.
 * - classes() / noClasses() (ArchRuleDefinition): empiezan una regla «las clases que…» o «ninguna clase que…».
 * - .that().resideInAPackage("co.florlogic.finca..dominio.."): en ArchUnit los dos puntos seguidos (..) significan
 *   «cualquier cantidad de paquetes en medio»; así la regla cubre el dominio de todos los módulos a la vez.
 * - .and().doNotHaveSimpleName("package-info") / haveSimpleNameNotEndingWith("Oyente"): sacan de la regla las
 *   clases con ese nombre corto (el package-info, ManejadorDeErrores, los oyentes de eventos).
 * - .should().onlyDependOnClassesThat().resideInAnyPackage(…): lista blanca; cualquier dependencia fuera de esos
 *   paquetes rompe la regla. "java.." es todo el JDK; "tools.jackson.." es Jackson 3; "org.slf4j.." el registro.
 * - .should().dependOnClassesThat(): lista negra, con noClasses(); basta una dependencia para fallar.
 * - .because(…): el texto que sale en el mensaje de error cuando la regla falla.
 * - .check(CLASES): evalúa la regla sobre las clases importadas y lanza AssertionError con cada violación.
 * - DescribedPredicate.describe(texto, condición): una condición propia con descripción legible; CREA_DOMINIO es
 *   «este acceso apunta a un constructor o a un método estático de algún paquete dominio».
 * - JavaAccess<?> / AccessTarget.CodeUnitAccessTarget: un acceso es cualquier llamada o referencia de una clase a
 *   otra; si el destino es código (método o constructor), el patrón instanceof lo nombra objetivo.
 * - getTargetOwner().getPackageName().matches(regex): la expresión regular acepta co.florlogic.finca.dominio y
 *   cualquier paquete …dominio… de un módulo.
 * - JavaConstructor.CONSTRUCTOR_NAME: el nombre interno de todo constructor en el bytecode (<init>).
 * - resolveMember().map(m -> …contains(JavaModifier.STATIC)).orElse(false): resolveMember devuelve un Optional
 *   (puede no encontrar el miembro); si lo encuentra, mira si es static; si no, cuenta como «no estático».
 * - accessTargetWhere(CREA_DOMINIO): falla si alguna clase de la regla hace un acceso que cumple esa condición.
 * - @Test: método que JUnit 5 ejecuta como prueba; aquí cada regla de capas es una prueba aparte.
 */
