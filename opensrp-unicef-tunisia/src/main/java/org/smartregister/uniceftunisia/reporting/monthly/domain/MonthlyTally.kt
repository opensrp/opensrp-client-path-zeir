package org.smartregister.uniceftunisia.reporting.monthly.domain

import org.codehaus.jackson.annotate.JsonProperty
import java.io.Serializable
import java.util.*

data class MonthlyTally(
        val indicator: String,

        @JsonProperty
        var id: Long = 0,

        @JsonProperty
        var value: String? = null,

        @JsonProperty
        var dateSent: Date? = null,

        @JsonProperty
        var month: Date = Date(),

        @JsonProperty
        var isEdited: Boolean = false,

        @JsonProperty
        var providerId: String? = null,

        @JsonProperty
        var updatedAt: Date? = null,

        @JsonProperty
        val grouping: String,

        @JsonProperty
        var createdAt: Date? = null
) : Serializable