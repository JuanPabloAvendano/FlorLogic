package co.florlogic.finca;

import static com.tngtech.archunit.core.domain.JavaCall.Predicates.target;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.core.domain.properties.HasOwner.Predicates.With.owner;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import org.junit.jupiter.api.Test;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaMethodCall;
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

    private static final DescribedPredicate<JavaMethodCall> ESTATICO_DEL_DOMINIO = DescribedPredicate.describe(
            "un método estático de una clase del dominio",
            llamada -> llamada.getTargetOwner().getPackageName().matches("co\\.florlogic\\.finca\\..*\\.dominio(\\..*)?")
                    && llamada.getTarget().resolveMember()
                            .map(metodo -> metodo.getModifiers().contains(JavaModifier.STATIC)).orElse(false));

    @Test
    void el_dominio_solo_depende_de_java_y_de_otro_dominio() {
        // Sin Spring, Jackson, JDBC ni el contrato: si alguien los importa en el dominio, esta prueba falla. Fuera:
        // package-info, que solo declara el módulo de Spring Modulith (@ApplicationModule), no es código del dominio.
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
        // Regla (1) de R. Desde 1b, solo ManejadorDeErrores podrá ver además las excepciones de servicios.output.ports
        // (COH-189).
        classes().that().resideInAPackage("co.florlogic.finca..infraestructura.input.controladores..")
                .and().doNotHaveSimpleName("package-info")
                .should().onlyDependOnClassesThat().resideInAnyPackage(
                        "co.florlogic.finca..infraestructura.input.controladores..",
                        "co.florlogic.finca..servicios.input.ports..",
                        "co.florlogic.finca..servicios.input.dto..",
                        "java..", "org.springframework..", "org.slf4j..", "tools.jackson..", "co.florlogic.contrato..")
                .because("el controlador solo adapta HTTP al puerto de entrada; no conoce el dominio ni el caso de uso")
                .check(CLASES);
    }

    @Test
    void los_adaptadores_de_servicios_no_construyen_dominio() {
        // Regla (2) de R: crear un valor del dominio (constructor o fábrica estática) es trabajo del ensamblador, del
        // caso de uso, del mapper o del propio dominio; el adaptador solo orquesta.
        noClasses().that().resideInAnyPackage(
                        "co.florlogic.finca..servicios.input.adapter..", "co.florlogic.finca..servicios.output.adapter..")
                .should().callConstructorWhere(target(owner(resideInAPackage("co.florlogic.finca..dominio.."))))
                .orShould().callMethodWhere(ESTATICO_DEL_DOMINIO)
                .because("adaptador ≠ ensamblador: solo el assembler traduce entre DTO y dominio (A-84)")
                .check(CLASES);
    }

}
