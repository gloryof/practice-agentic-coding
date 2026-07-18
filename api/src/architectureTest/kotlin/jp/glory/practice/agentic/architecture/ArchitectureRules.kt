package jp.glory.practice.agentic.architecture

import com.tngtech.archunit.core.domain.JavaClass
import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.lang.ArchCondition
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.ConditionEvents
import com.tngtech.archunit.lang.SimpleConditionEvent
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes

class ArchitectureRules(
    private val basePackage: String,
) {
    data class NamedRule(
        val id: String,
        val rule: ArchRule,
    ) {
        fun check(classes: JavaClasses) = rule.check(classes)
    }

    fun all(): List<NamedRule> =
        listOf(
            layerRule(
                id = "ARCH-CMD-DOMAIN",
                sourcePackage = "command.domain",
                allowedLayers = setOf("command.domain"),
            ),
            layerRule(
                id = "ARCH-CMD-USECASE",
                sourcePackage = "command.usecase",
                allowedLayers = setOf("command.domain", "command.usecase"),
            ),
            layerRule(
                id = "ARCH-CMD-WEB",
                sourcePackage = "command.web",
                allowedLayers = setOf("command.usecase", "command.web"),
            ),
            layerRule(
                id = "ARCH-CMD-INFRA",
                sourcePackage = "command.infra",
                allowedLayers = setOf("command.domain", "command.infra"),
            ),
            layerRule(
                id = "ARCH-QRY-USECASE",
                sourcePackage = "query.usecase",
                allowedLayers = setOf("query.usecase"),
            ),
            layerRule(
                id = "ARCH-QRY-WEB",
                sourcePackage = "query.web",
                allowedLayers = setOf("query.usecase", "query.web"),
            ),
            layerRule(
                id = "ARCH-QRY-INFRA",
                sourcePackage = "query.infra",
                allowedLayers = setOf("query.usecase", "query.infra"),
            ),
            commandContextRule(),
            domainPlacementRule(),
        )

    private fun layerRule(
        id: String,
        sourcePackage: String,
        allowedLayers: Set<String>,
    ): NamedRule {
        val condition =
            object : ArchCondition<JavaClass>("depend only on allowed application layers") {
                override fun check(
                    item: JavaClass,
                    events: ConditionEvents,
                ) {
                    val context = contextOf(item.packageName) ?: return
                    item.directDependenciesFromSelf
                        .filter { it.targetClass.packageName.isApplicationPackage() }
                        .filterNot { it.targetClass.packageName.isSharedPackage() }
                        .filterNot { dependency ->
                            allowedLayers.any { layer ->
                                dependency.targetClass.packageName.isInLayer(context, layer)
                            }
                        }.forEach { dependency ->
                            events.add(
                                SimpleConditionEvent.violated(
                                    item,
                                    "$id: ${item.name} -> ${dependency.targetClass.name}",
                                ),
                            )
                        }
                }
            }

        val rule =
            classes()
                .that()
                .resideInAPackage("$basePackage..$sourcePackage..")
                .should(condition)
                .allowEmptyShould(true)
                .`as`("$id: $sourcePackage の依存方向を守る")

        return NamedRule(id, rule)
    }

    private fun commandContextRule(): NamedRule {
        val id = "ARCH-CMD-CONTEXT"
        val condition =
            object : ArchCondition<JavaClass>("not depend on another command context") {
                override fun check(
                    item: JavaClass,
                    events: ConditionEvents,
                ) {
                    val originContext = contextOf(item.packageName) ?: return
                    item.directDependenciesFromSelf
                        .filter { it.targetClass.packageName.isCommandPackage() }
                        .filter { contextOf(it.targetClass.packageName) != originContext }
                        .forEach { dependency ->
                            events.add(
                                SimpleConditionEvent.violated(
                                    item,
                                    "$id: ${item.name} -> ${dependency.targetClass.name}",
                                ),
                            )
                        }
                }
            }
        val rule =
            classes()
                .that()
                .resideInAPackage("$basePackage..command..")
                .should(condition)
                .allowEmptyShould(true)
                .`as`("$id: commandコンテキスト間の直接依存を禁止する")

        return NamedRule(id, rule)
    }

    private fun domainPlacementRule(): NamedRule {
        val id = "ARCH-DOMAIN-PLACEMENT"
        val rule =
            classes()
                .that()
                .resideInAPackage("$basePackage..command.domain..")
                .should()
                .resideInAnyPackage(
                    "$basePackage..command.domain.model..",
                    "$basePackage..command.domain.event..",
                    "$basePackage..command.domain.constraint..",
                    "$basePackage..command.domain.service..",
                    "$basePackage..command.domain.repository..",
                ).allowEmptyShould(true)
                .`as`("$id: Domainクラスを定義済み分類へ配置する")

        return NamedRule(id, rule)
    }

    private fun contextOf(packageName: String): String? {
        if (!packageName.isApplicationPackage() || packageName.isSharedPackage()) return null
        return packageName.removePrefix("$basePackage.").substringBefore('.')
    }

    private fun String.isApplicationPackage(): Boolean = this == basePackage || startsWith("$basePackage.")

    private fun String.isSharedPackage(): Boolean = this == "$basePackage.shared" || startsWith("$basePackage.shared.")

    private fun String.isCommandPackage(): Boolean {
        val context = contextOf(this) ?: return false
        val prefix = "$basePackage.$context.command"
        return this == prefix || startsWith("$prefix.")
    }

    private fun String.isInLayer(
        context: String,
        layer: String,
    ): Boolean {
        val prefix = "$basePackage.$context.$layer"
        return this == prefix || startsWith("$prefix.")
    }
}
