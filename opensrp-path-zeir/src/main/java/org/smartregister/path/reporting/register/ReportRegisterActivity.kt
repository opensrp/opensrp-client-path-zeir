package org.smartregister.path.reporting.register

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_report_register.*
import org.apache.commons.lang3.tuple.Triple
import org.smartregister.child.ChildLibrary
import org.smartregister.child.activity.BaseActivity
import org.smartregister.child.toolbar.LocationSwitcherToolbar
import org.smartregister.path.R
import org.smartregister.path.activity.ChildRegisterActivity
import org.smartregister.path.application.ZeirApplication
import org.smartregister.path.reporting.ReportGroupingModel
import org.smartregister.path.util.AppUtils
import org.smartregister.path.view.NavigationMenu
import org.smartregister.reporting.job.RecurringIndicatorGeneratingJob

class ReportRegisterActivity : BaseActivity(), ReportRegisterContract.View, AdapterView.OnItemClickListener {

    private lateinit var reportRegisterPresenter: ReportRegisterPresenter

    lateinit var navigationMenu: NavigationMenu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializePresenter()
        navigationMenu = NavigationMenu.getInstance(this)
        reportRegisterListView.apply {
            val context = this@ReportRegisterActivity
            adapter = ReportRegisterTypeAdapter(context, R.layout.report_type_list_item,
                    ReportGroupingModel(context).reportGroupings.map { it.displayName })
            onItemClickListener = context
            divider = ColorDrawable(ContextCompat.getColor(context, R.color.light_grey))
            dividerHeight = 1
        }

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

    public override fun getContentView() = R.layout.activity_report_register

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

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) =
            reportRegisterPresenter.startReport(ReportGroupingModel(this).reportGroupings[position].reportGroup)

    override fun initializePresenter() {
        reportRegisterPresenter = ReportRegisterPresenter(this)
    }
}