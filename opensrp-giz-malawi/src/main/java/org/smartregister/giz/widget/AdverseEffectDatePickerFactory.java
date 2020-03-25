package org.smartregister.giz.widget;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.customviews.DatePickerDialog;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.widgets.DatePickerFactory;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.giz.fragment.GizChildFormFragment;

import java.lang.ref.WeakReference;
import java.util.List;

import timber.log.Timber;


public class AdverseEffectDatePickerFactory extends DatePickerFactory implements GizChildFormFragment.OnReactionVaccineSelected {

    private WeakReference<GizChildFormFragment> formFragment;
    private WeakReference<TextView> txtDuration;
    private WeakReference<MaterialEditText> edtText;
    private JSONObject jsonObject;

    @Override
    protected List<View> attachJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, boolean popup, CommonListener listener) {
        this.formFragment = new WeakReference<>((GizChildFormFragment) formFragment);
        this.formFragment.get().setOnReactionVaccineSelected(this);
        return super.attachJson(stepName, context, formFragment, jsonObject, popup, listener);
    }

    @Override
    protected DatePickerDialog createDateDialog(Context context, TextView duration, MaterialEditText editText, JSONObject jsonObject) throws JSONException {
        DatePickerDialog datePickerDialog = super.createDateDialog(context, duration, editText, jsonObject);
        txtDuration = new WeakReference<>(duration);
        this.edtText = new WeakReference<>(editText);
        this.jsonObject = jsonObject;
        return datePickerDialog;
    }

    @Override
    public void updateDatePicker(String date) {
        try {
            jsonObject.put(JsonFormConstants.MIN_DATE, date);
            attachLayout(JsonFormConstants.STEP1, formFragment.get().getContext(), formFragment.get(), jsonObject, edtText.get(), txtDuration.get());
        } catch (JSONException e) {
            Timber.e(e);
        }
    }
}
