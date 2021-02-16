package org.smartregister.pathzeir.reporting.stock

import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_stock.*
import org.apache.commons.lang3.tuple.Triple
import org.smartregister.child.ChildLibrary
import org.smartregister.child.activity.BaseActivity
import org.smartregister.child.toolbar.LocationSwitcherToolbar
import org.smartregister.pathzeir.R
import org.smartregister.pathzeir.activity.ChildRegisterActivity
import org.smartregister.pathzeir.application.ZeirApplication
import org.smartregister.pathzeir.util.AppUtils
import org.smartregister.pathzeir.view.NavigationMenu
import org.smartregister.reporting.job.RecurringIndicatorGeneratingJob
import org.smartregister.stock.StockLibrary
import org.smartregister.stock.activity.StockControlActivity
import org.smartregister.stock.adapter.StockGridAdapter

class ZeirStockActivity : BaseActivity()  {

    lateinit var navigationMenu: NavigationMenu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navigationMenu = NavigationMenu.getInstance(this)

        reportSyncBtn.apply {
            setOnClickListener {
                val allSharedPreferences = ZeirApplication.getInstance().context().allSharedPreferences()
                val reportJobExecutionTime = allSharedPreferences.getPreference("report_job_execution_time")
                if (reportJobExecutionTime.isBlank() || AppUtils.timeBetweenLastExecutionAndNow(30, reportJobExecutionTime)) {
                    allSharedPreferences.savePreference("report_job_execution_time", System.currentTimeMillis().toString())
                    RecurringIndicatorGeneratingJob.scheduleJobImmediately(RecurringIndicatorGeneratingJob.TAG)
                    Toast.makeText(context, "Reporting Job Has Started, It will take some time", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Reporting Job Has Already Been Started, Try again in 30 mins", Toast.LENGTH_LONG).show()
                }
            }
        }
        backButton.apply { setOnClickListener { navigationMenu.openDrawer() } }
    }

    private fun refreshAdapter() {
        val allStockTypes = StockLibrary.getInstance().stockTypeRepository.getAllStockTypes(null)
        val stockTypes = allStockTypes.toTypedArray()
        val adapter = StockGridAdapter(this, stockTypes, StockControlActivity::class.java)
        stockgrid.setAdapter(adapter)
        adapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        refreshAdapter()
    }

    public override fun getContentView() = R.layout.activity_stock

    public override fun getDrawerLayoutId() = navigationMenu.drawer.id

    override fun getToolbarId() = LocationSwitcherToolbar.TOOLBAR_ID

    @Suppress("UNCHECKED_CAST")
    public override fun onBackActivity(): Class<ChildRegisterActivity> {
        navigationMenu.drawer
        return ChildLibrary.getInstance().metadata().childRegisterActivity as Class<ChildRegisterActivity>
    }

    override fun onUniqueIdFetched(triple: Triple<String, Map<String, String>, String>, s: String) = Unit

    override fun onNoUniqueId() = Unit

    override fun onRegistrationSaved(b: Boolean) = Unit

    override fun onSyncStart() {
        super.onSyncStart()
        reportSyncBtn.visibility = View.GONE
        AppUtils.updateSyncStatus(false)
    }
}