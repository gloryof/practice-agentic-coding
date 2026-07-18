package jp.glory.practice.agentic.architecturefixture.violating.alpha.command.usecase

import jp.glory.practice.agentic.architecturefixture.violating.alpha.command.web.CommandWebTarget
import jp.glory.practice.agentic.architecturefixture.violating.beta.command.domain.model.OtherContextDomainTarget

class CommandUsecaseTarget

class CommandUsecaseViolation(
    val webTarget: CommandWebTarget,
    val contextTarget: OtherContextDomainTarget,
)
