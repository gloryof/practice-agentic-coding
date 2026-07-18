package jp.glory.practice.agentic.architecture

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.core.importer.Location
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

class ProductionArchitectureTest {
    @TestFactory
    fun `given production classes when check architecture then all rules pass`(): List<DynamicTest> {
        val classes =
            ClassFileImporter()
                .withImportOption(ExcludeArchitectureTests())
                .importPackages(BASE_PACKAGE)

        return ArchitectureRules(BASE_PACKAGE).all().map { namedRule ->
            DynamicTest.dynamicTest(namedRule.id) {
                namedRule.check(classes)
            }
        }
    }

    private class ExcludeArchitectureTests : ImportOption {
        override fun includes(location: Location): Boolean = !location.toString().contains("/architectureTest/")
    }

    private companion object {
        const val BASE_PACKAGE = "jp.glory.practice.agentic"
    }
}
