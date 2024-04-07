package com.ndgndg91.executionaggregator.config

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.sql.Timestamp

@JsonIgnoreProperties(ignoreUnknown = true)
data class ExecutionHistory(
    val id: Long? = null,
    @JsonProperty("user_id") val userId: Long,
    val symbol: String,
    val amount: Long,
    val timestamp: Timestamp = Timestamp(System.currentTimeMillis())
)
