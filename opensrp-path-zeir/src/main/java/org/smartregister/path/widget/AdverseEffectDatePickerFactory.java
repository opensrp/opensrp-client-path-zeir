package org.smartregister.path.widget;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.customviews.DatePickerDialog;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.child.widgets.ChildDatePickerFactory;
import org.smartregister.path.fragment.AppChildFormFragment;

import java.lang.ref.WeakReference;
import java.util.List;

import timber.log.Timber;

public class AdverseEffectDatePickerFactory extends ChildDatePickerFactory implements AppChildFormFragment.OnReactionVaccineSelected {

    private AppChildFormFragment formFragment;
    private TextView txtDuration;
    private MaterialEditText edtText;
    private JSONObject jsonObject;

    @Override
    protected List<View> attachJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, boolean popup, CommonListener listener) {
        this.formFragment = new WeakReference<>((AppChildFormFragment) formFragment).get();
        this.formFragment.setOnReactionVaccineSelected(this);
        return super.attachJson(stepName, context, formFragment, jsonObject, popup, listener);
    }

    @Override
    protected DatePickerDialog createDateDialog(Context context, TextView duration, MaterialEditText editText, JSONObject jsonObject) throws JSONException {
        DatePickerDialog datePickerDialog = super.createDateDialog(context, duration, editText, jsonObject);
        this.txtDuration = new WeakReference<>(duration).get();
        this.edtText = new WeakReference<>(editText).get();
        this.jsonObject = jsonObject;
        return datePickerDialog;
    }

    @Override
    public void updateDatePicker(String date) {
        try {
            jsonObject.put(JsonFormConstants.MIN_DATE, date);
            attachLayout(JsonFormConstants.STEP1, formFragment.getContext(), formFragment, jsonObject, edtText, txtDuration);
        } catch (JSONException e) {
            Timber.e(e);
        }
    }
}
