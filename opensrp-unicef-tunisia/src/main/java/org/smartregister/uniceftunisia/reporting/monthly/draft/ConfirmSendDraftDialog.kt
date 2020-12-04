package org.smartregister.uniceftunisia.reporting.monthly.draft

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialog_fragment_send_monthly.*
import org.smartregister.uniceftunisia.R
import org.smartregister.uniceftunisia.application.UnicefTunisiaApplication
import org.smartregister.uniceftunisia.reporting.common.ReportingUtils.dateFormatter
import java.util.*

class ConfirmSendDraftDialog : DialogFragment() {

    lateinit var onClickListener: View.OnClickListener

    object Constants {
        const val MONTH = "month"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Light_NoTitleBar)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.dialog_fragment_send_monthly, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog?.window?.setLayout(275, 350)
        val provider = UnicefTunisiaApplication.getInstance().context().allSharedPreferences().fetchRegisteredANM()

        sendMonthlyDraftTextView.apply {
            text = String.format(getString(R.string.send_report_warning), arguments?.getString(Constants.MONTH),
                    dateFormatter("dd/MM/yyyy").format(Date()), provider)
        }

        cancelSendReportsButton.setOnClickListener { dismiss() }

        sendDraftReportsButton.apply {
            setOnClickListener { v ->
                dismiss()
                onClickListener.onClick(v)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.apply {
            with(window!!.attributes) {
                dimAmount = 0.50f
                flags = flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
                window?.attributes = this
            }
            window?.setLayout(600, WindowManager.LayoutParams.WRAP_CONTENT)
        }
    }
}