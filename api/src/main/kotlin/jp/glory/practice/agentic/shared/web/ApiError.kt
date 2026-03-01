package jp.glory.practice.agentic.shared.web

import com.fasterxml.jackson.annotation.JsonProperty

data class ApiError(
    val code: String,
    val message: String,
    val details: List<ApiErrorDetail>,
    @JsonProperty("trace_id") val traceId: String,
)

data class ApiErrorDetail(
    val field: String,
    val reason: String,
)
