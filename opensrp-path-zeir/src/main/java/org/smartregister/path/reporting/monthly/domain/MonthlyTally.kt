package org.smartregister.path.reporting.monthly.domain

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable
import java.util.Date

data class MonthlyTally(
        override val indicator: String,

        @JsonProperty
        override var id: Long =0,

        @JsonProperty
        override var value: String = "0",

        @JsonProperty
        override val grouping: String,

        @JsonProperty
        override var enteredManually: Boolean = false,

        @JsonProperty
        override var providerId: String? = null,

        @JsonProperty
        var dateSent: Date? = null,

        @JsonProperty
        var month: Date = Date(),

        @JsonProperty
        var updatedAt: Date? = null,

        @JsonProperty
        var createdAt: Date? = null,

        @JsonProperty
        override var dependentCalculations: List<String> = emptyList()
) : Tally(), Serializable