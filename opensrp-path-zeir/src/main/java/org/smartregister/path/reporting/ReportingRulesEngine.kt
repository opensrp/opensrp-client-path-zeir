package org.smartregister.path.reporting

import android.content.Context
import org.jeasy.rules.api.Facts
import org.jeasy.rules.api.Rules
import org.jeasy.rules.core.DefaultRulesEngine
import org.jeasy.rules.mvel.MVELRuleFactory
import org.jeasy.rules.support.YamlRuleDefinitionReader
import org.smartregister.path.reporting.common.toWholeNumber
import org.smartregister.path.reporting.monthly.domain.Tally
import org.smartregister.util.Utils
import java.io.BufferedReader
import java.io.InputStreamReader

class ReportingRulesEngine<T : Tally>(tallies: MutableMap<String, T>, rulesFilePath: String = "configs/reporting/reporting-rules.yml", context: Context) {

    private val facts = Facts().apply { asMap().putAll(tallies.map { Pair(it.key, it.value.value) }) }

    private val reportingRules: Rules = MVELRuleFactory(YamlRuleDefinitionReader())
            .createRules(BufferedReader(InputStreamReader(Utils.getAssetFileInputStream(context, rulesFilePath))))

    private val rulesEngine: DefaultRulesEngine = DefaultRulesEngine()

    /**
     * Filter and fire dependent calculation rules for [monthlyTally]. Invoke the callback method [fieldValueHandler]
     * once calculation is done to update the value of the calculation field. This also updates the [viewModelData]
     */
    fun fireRules(monthlyTally: T, viewModelData: MutableMap<String, T>,
                  fieldValueHandler: (String, String) -> Unit) {

        facts.asMap()[monthlyTally.indicator] = monthlyTally.value

        viewModelData.run {
            val dependentCalculations = this[monthlyTally.indicator]?.dependentCalculations
                    ?: emptyList()

            val filteredRules = reportingRules.filter { dependentCalculations.contains(it.name) }
                    .toSet()

            rulesEngine.fire(Rules(filteredRules), facts)

            dependentCalculations.forEach { calculationField ->
                val calculatedValue: String = when {
                    facts.asMap()[calculationField] is String -> facts.asMap()[calculationField] as String
                    facts.asMap()[calculationField] is Boolean -> "0"
                    else -> facts.get<Number>(calculationField)?.toDouble()?.toWholeNumber()?.toString() ?: "0"
                }
                this[calculationField]?.value = calculatedValue
                fieldValueHandler(calculationField, calculatedValue)
            }
        }
    }
}