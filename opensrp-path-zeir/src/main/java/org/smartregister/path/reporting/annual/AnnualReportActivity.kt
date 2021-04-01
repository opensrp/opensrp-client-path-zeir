package org.smartregister.path.reporting.annual

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.vijay.jsonwizard.activities.MultiLanguageActivity
import kotlinx.android.synthetic.main.activity_annual_report.*
import org.smartregister.path.R

class AnnualReportActivity : MultiLanguageActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_annual_report)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.annualReportNavController) as NavHostFragment
        navController = navHostFragment.navController

        backButton.setOnClickListener { finish() }
    }
}