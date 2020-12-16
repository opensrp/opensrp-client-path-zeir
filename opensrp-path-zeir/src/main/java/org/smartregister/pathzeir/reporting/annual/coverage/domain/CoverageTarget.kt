package org.smartregister.pathzeir.reporting.annual.coverage.domain

data class CoverageTarget(
        val targetType: CoverageTargetType,
        val year: Int,
        var target: Int,
)