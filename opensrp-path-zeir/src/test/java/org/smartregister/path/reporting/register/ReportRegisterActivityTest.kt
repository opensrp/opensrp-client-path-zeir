package org.smartregister.path.reporting.register

import android.view.View
import android.widget.ImageView
import android.widget.ListView
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.smartregister.path.R
import org.smartregister.path.TestZeirApplication
import org.smartregister.path.activity.ChildRegisterActivity
import org.smartregister.path.application.ZeirApplication

@RunWith(RobolectricTestRunner::class)
@Config(application = TestZeirApplication::class)
@Ignore("Fix memory leak error: #250")
class ReportRegisterActivityTest {

    private val controller = Robolectric.buildActivity(ReportRegisterActivity::class.java)
    private lateinit var reportRegisterActivity: ReportRegisterActivity

    @Before
    fun `Before every test`() {
        reportRegisterActivity = controller.create().resume().get()
    }

    @Test
    fun `Should launch activity and populate list view`() {
        val reportRegisterListView = reportRegisterActivity.findViewById<ListView>(R.id.reportRegisterListView)
        assertNotNull(reportRegisterActivity.navigationMenu)
        assertEquals(2, reportRegisterListView.adapter.count)
    }

    @Test
    fun `Should use the correct layout`() {
        assertEquals(reportRegisterActivity.contentView, R.layout.activity_report_register)
    }

    @Test
    fun `Should return correct drawer id`() {
        assertEquals(reportRegisterActivity.drawerLayoutId, reportRegisterActivity.navigationMenu.drawer.id)
    }

    @Test
    fun `Should return ChildRegisterActivity class`() {
        assertEquals(reportRegisterActivity.onBackActivity().name, ChildRegisterActivity::class.java.name)
    }

    @Test
    fun `Should perform sync and update status`() {
        reportRegisterActivity.onSyncStart()
        val reportSyncImageView = reportRegisterActivity.findViewById<ImageView>(R.id.reportSyncBtn)
        assertEquals(reportSyncImageView.visibility, View.GONE)
        val syncCompletePreference = ZeirApplication.getInstance().context().allSharedPreferences().getPreference("syncComplete")
        assertEquals("false", syncCompletePreference)
    }

    @After
    fun `After every test`() {
        controller.pause().stop().destroy()
    }
}