package co.florlogic.finca;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import org.junit.jupiter.api.Test;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;

/**
 * TEC-05 (ArchUnit): la regla de dependencias dentro de cada hexágono, que {@link ModulosTest} no ve porque solo mira
 * los límites entre módulos (ESPEC-dominio-ingesta VER-2, 29-oct). Las flechas apuntan hacia adentro:
 * {@code infraestructura → servicios → dominio}, y el dominio no conoce a nadie (Martin, <i>Clean Architecture</i>,
 * «The Dependency Rule»; {@code .claude/CLAUDE.md}: «dominio es Java puro: sin Spring, sin SQL, sin contrato»).
 */
class CapasTest {

    private static final JavaClasses CLASES = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("co.florlogic.finca");

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
}
