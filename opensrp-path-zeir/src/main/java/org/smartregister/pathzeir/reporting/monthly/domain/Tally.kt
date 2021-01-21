package org.smartregister.pathzeir.reporting.monthly.domain

import java.io.Serializable

abstract class Tally : Serializable {
    abstract val indicator: String

    abstract var id: Long

    abstract var value: String

    abstract val grouping: String

    abstract var enteredManually: Boolean

    abstract var providerId: String?

    abstract var dependentCalculations: List<String>
}