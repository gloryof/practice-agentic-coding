package jp.glory.practice.agentic.shared.testinfra

import com.github.f4b6a3.uuid.UuidCreator

object UuidGenerator {
    fun v7(): String = UuidCreator.getTimeOrderedEpoch().toString()
}
