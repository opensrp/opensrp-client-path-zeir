package org.smartregister.uniceftunisia.reporting.monthly.domain

import org.codehaus.jackson.annotate.JsonProperty
import java.io.Serializable
import java.util.*

open class MonthlyTally(
        val indicator: String,

        @JsonProperty
        var id: Long = 0,

        @JsonProperty
        var value: String = "0",

        @JsonProperty
        var dateSent: Date? = null,

        @JsonProperty
        var month: Date = Date(),

        @JsonProperty
        var providerId: String? = null,

        @JsonProperty
        var updatedAt: Date? = null,

        @JsonProperty
        val grouping: String,

        @JsonProperty
        var createdAt: Date? = null,

        @JsonProperty
        var enteredManually: Boolean = false,

        @JsonProperty
        var dependentCalculations: List<String> = emptyList()
) : Serializable