package org.smartregister.path.reporting.annual.coverage.domain

data class CoverageTarget(
        val targetType: CoverageTargetType,
        val year: Int,
        var target: Int,
)