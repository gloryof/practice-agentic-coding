package jp.glory.practice.agentic.architecture

import com.tngtech.archunit.core.importer.ClassFileImporter
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFailsWith

class ArchitectureRulesTest {
    @Nested
    inner class CompliantFixtures {
        @Test
        fun `given compliant fixtures when check architecture then all rules pass`() {
            val classes = ClassFileImporter().importPackages(COMPLIANT_PACKAGE)

            ArchitectureRules(COMPLIANT_PACKAGE).all().forEach { it.check(classes) }
        }
    }

    @Nested
    inner class ViolatingFixtures {
        @Test
        fun `given violating fixtures when check architecture then each rule identifies dependency`() {
            val classes = ClassFileImporter().importPackages(VIOLATING_PACKAGE)
            val expectations =
                mapOf(
                    "ARCH-CMD-DOMAIN" to ("CommandDomainViolation" to "CommandUsecaseTarget"),
                    "ARCH-CMD-USECASE" to ("CommandUsecaseViolation" to "CommandWebTarget"),
                    "ARCH-CMD-WEB" to ("CommandWebViolation" to "CommandInfraTarget"),
                    "ARCH-CMD-INFRA" to ("CommandInfraViolation" to "CommandUsecaseTarget"),
                    "ARCH-QRY-USECASE" to ("QueryUsecaseViolation" to "QueryInfraTarget"),
                    "ARCH-QRY-WEB" to ("QueryWebViolation" to "QueryInfraTarget"),
                    "ARCH-QRY-INFRA" to ("QueryInfraViolation" to "QueryWebTarget"),
                    "ARCH-CMD-CONTEXT" to ("CommandUsecaseViolation" to "OtherContextDomainTarget"),
                    "ARCH-DOMAIN-PLACEMENT" to ("MisplacedDomainClass" to "MisplacedDomainClass"),
                )

            ArchitectureRules(VIOLATING_PACKAGE).all().forEach { namedRule ->
                val message =
                    assertFailsWith<AssertionError> {
                        namedRule.check(classes)
                    }.message.orEmpty()
                val expected = checkNotNull(expectations[namedRule.id])
                assertContains(message, namedRule.id)
                assertContains(message, expected.first)
                assertContains(message, expected.second)
            }
        }
    }

    private companion object {
        const val COMPLIANT_PACKAGE = "jp.glory.practice.agentic.architecturefixture.compliant"
        const val VIOLATING_PACKAGE = "jp.glory.practice.agentic.architecturefixture.violating"
    }
}
