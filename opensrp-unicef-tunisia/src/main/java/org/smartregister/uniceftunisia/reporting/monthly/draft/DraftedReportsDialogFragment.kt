package org.smartregister.uniceftunisia.reporting.monthly.draft

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import org.smartregister.uniceftunisia.R
import org.smartregister.uniceftunisia.application.UnicefTunisiaApplication

class DraftedReportsDialogFragment(private val date: String, private val month: String, private val clickListener: View.OnClickListener) : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Light_NoTitleBar)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.dialog_fragment_send_monthly, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val provider = UnicefTunisiaApplication.getInstance().context().allSharedPreferences().fetchRegisteredANM()

        with(view) {
            findViewById<TextView>(R.id.tv_send_monthly_draft).apply {
                text = String.format(getString(R.string.send_report_warning), month, date, provider)
            }
            findViewById<Button>(R.id.button_cancel).apply {
                setOnClickListener { dismiss() }
            }

            findViewById<Button>(R.id.button_send).apply {
                setOnClickListener { v ->
                    dismiss()
                    clickListener.onClick(v)
                }
            }
        }
    }
}