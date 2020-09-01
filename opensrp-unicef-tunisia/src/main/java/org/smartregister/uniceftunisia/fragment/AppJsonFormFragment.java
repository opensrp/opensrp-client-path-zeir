package org.smartregister.uniceftunisia.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.presenters.JsonFormFragmentPresenter;
import com.vijay.jsonwizard.utils.FormUtils;
import com.vijay.jsonwizard.viewstates.JsonFormFragmentViewState;
import com.vijay.jsonwizard.widgets.DatePickerFactory;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.smartregister.Context;
import org.smartregister.child.interactor.ChildFormInteractor;
import org.smartregister.child.provider.MotherLookUpSmartClientsProvider;
import org.smartregister.child.util.MotherLookUpUtils;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.uniceftunisia.R;
import org.smartregister.uniceftunisia.activity.AppStockJsonFormActivity;
import org.smartregister.uniceftunisia.application.UnicefTunisiaApplication;
import org.smartregister.uniceftunisia.util.AppConstants;
import org.smartregister.util.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

import static org.smartregister.util.Utils.getValue;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-07-11
 */

public class AppJsonFormFragment extends JsonFormFragment {
    private Snackbar snackbar = null;
    private AlertDialog alertDialog = null;

    public static AppJsonFormFragment getFormFragment(String stepName) {
        AppJsonFormFragment jsonFormFragment = new AppJsonFormFragment();
        Bundle bundle = new Bundle();
        bundle.putString(AppConstants.KEY.STEPNAME, stepName);
        jsonFormFragment.setArguments(bundle);
        return jsonFormFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected JsonFormFragmentViewState createViewState() {
        return new JsonFormFragmentViewState();
    }

    @Override
    protected JsonFormFragmentPresenter createPresenter() {
        return new JsonFormFragmentPresenter(this, ChildFormInteractor.getChildInteractorInstance());
    }

    public Context context() {
        return UnicefTunisiaApplication.getInstance().context();
    }

    private void showMotherLookUp(final HashMap<CommonPersonObject, List<CommonPersonObject>> map) {
        if (!map.isEmpty()) {
            tapToView(map);
        } else {
            if (snackbar != null) {
                snackbar.dismiss();
            }
        }
    }

    private void updateResults(final HashMap<CommonPersonObject, List<CommonPersonObject>> map) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.mother_lookup_results, null);

        ListView listView = view.findViewById(R.id.list_view);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.PathDialog);
        builder.setView(view).setNegativeButton(R.string.dismiss, null);
        builder.setCancelable(true);

        alertDialog = builder.create();

        final List<CommonPersonObject> mothers = new ArrayList<>();
        for (Map.Entry<CommonPersonObject, List<CommonPersonObject>> entry : map.entrySet()) {
            mothers.add(entry.getKey());
        }

        final MotherLookUpSmartClientsProvider motherLookUpSmartClientsProvider = new MotherLookUpSmartClientsProvider(getActivity());
        BaseAdapter baseAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return mothers.size();
            }

            @Override
            public Object getItem(int position) {
                return mothers.get(position);
            }

            @Override
            public long getItemId(int position) {
                return Long.parseLong(mothers.get(position).getCaseId().replaceAll("\\D+", ""));
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v;
                if (convertView == null) {
                    v = motherLookUpSmartClientsProvider.inflateLayoutForCursorAdapter();
                } else {
                    v = convertView;
                }

                CommonPersonObject commonPersonObject = mothers.get(position);
                List<CommonPersonObject> children = map.get(commonPersonObject);

                motherLookUpSmartClientsProvider.getView(commonPersonObject, children, v);

                v.setOnClickListener(lookUpRecordOnClickLister);
                v.setTag(Utils.convert(commonPersonObject));

                return v;
            }
        };

        listView.setAdapter(baseAdapter);
        alertDialog.show();

    }

    private void clearMotherLookUp() {
        Map<String, List<View>> lookupMap = getLookUpMap();
        if (lookupMap.containsKey(AppConstants.KEY.MOTHER)) {
            List<View> lookUpViews = lookupMap.get(AppConstants.KEY.MOTHER);
            if (lookUpViews != null && !lookUpViews.isEmpty()) {
                for (View view : lookUpViews) {
                    if (view instanceof MaterialEditText) {
                        MaterialEditText materialEditText = (MaterialEditText) view;
                        materialEditText.setEnabled(true);
                        enableEditText(materialEditText);
                        materialEditText.setTag(com.vijay.jsonwizard.R.id.after_look_up, false);
                        materialEditText.setText("");
                    }
                }

                Map<String, String> metadataMap = new HashMap<>();
                metadataMap.put(AppConstants.KEY.ENTITY_ID, "");
                metadataMap.put(AppConstants.KEY.VALUE, "");

                writeMetaDataValue(FormUtils.LOOK_UP_JAVAROSA_PROPERTY, metadataMap);

            }
        }
    }

    private void tapToView(final HashMap<CommonPersonObject, List<CommonPersonObject>> map) {
        snackbar = Snackbar
                .make(getMainView(), map.size() + getString(R.string.mother_guardian_matches), Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.tap_to_view, v -> updateResults(map));
        show(snackbar, 30000);

    }

    private void clearView() {
        snackbar = Snackbar
                .make(getMainView(), "Undo Lookup.", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Clear", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
                clearMotherLookUp();
            }
        });
        show(snackbar, 30000);
    }

    private void show(final Snackbar snackbar, int duration) {
        if (snackbar == null) {
            return;
        }

        float drawablePadding = getResources().getDimension(R.dimen.register_drawable_padding);
        int paddingInt = Float.valueOf(drawablePadding).intValue();

        float textSize = getActivity().getResources().getDimension(R.dimen.snack_bar_text_size);

        View snackbarView = snackbar.getView();
        snackbarView.setMinimumHeight(Float.valueOf(textSize).intValue());
        snackbarView.setBackgroundResource(R.color.snackbar_background_yellow);

        final Button actionView = snackbarView.findViewById(android.support.design.R.id.snackbar_action);
        actionView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        actionView.setGravity(Gravity.CENTER);
        actionView.setTextColor(getResources().getColor(R.color.text_black));

        TextView textView = snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        textView.setGravity(Gravity.CENTER);
        textView.setOnClickListener(v -> actionView.performClick());
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_error, 0, 0, 0);
        textView.setCompoundDrawablePadding(paddingInt);
        textView.setPadding(paddingInt, 0, 0, 0);
        textView.setTextColor(getResources().getColor(R.color.text_black));

        snackbarView.setOnClickListener(v -> actionView.performClick());

        snackbar.show();

        Handler handler = new Handler();
        handler.postDelayed(() -> snackbar.dismiss(), duration);

    }

    private void disableEditText(MaterialEditText editText) {
        editText.setInputType(InputType.TYPE_NULL);
    }

    private void enableEditText(MaterialEditText editText) {
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
    }

    private void lookupDialogDismissed(CommonPersonObjectClient pc) {
        if (pc != null) {

            Map<String, List<View>> lookupMap = getLookUpMap();
            if (lookupMap.containsKey(AppConstants.KEY.MOTHER)) {
                List<View> lookUpViews = lookupMap.get(AppConstants.KEY.MOTHER);
                if (lookUpViews != null && !lookUpViews.isEmpty()) {

                    for (View view : lookUpViews) {

                        String key = (String) view.getTag(com.vijay.jsonwizard.R.id.key);
                        String text = "";

                        if (StringUtils.containsIgnoreCase(key, MotherLookUpUtils.firstName)) {
                            text = getValue(pc.getColumnmaps(), MotherLookUpUtils.firstName, true);
                        }

                        if (StringUtils.containsIgnoreCase(key, MotherLookUpUtils.lastName)) {
                            text = getValue(pc.getColumnmaps(), MotherLookUpUtils.lastName, true);
                        }

                        if (StringUtils.containsIgnoreCase(key, MotherLookUpUtils.birthDate)) {
                            String dobString = getValue(pc.getColumnmaps(), MotherLookUpUtils.dob, false);
                            Date motherDob = Utils.dobStringToDate(dobString);
                            if (motherDob != null) {
                                try {
                                    text = DatePickerFactory.DATE_FORMAT.format(motherDob);
                                } catch (Exception e) {
                                    Timber.e(e);
                                }
                            }
                        }

                        if (view instanceof MaterialEditText) {
                            MaterialEditText materialEditText = (MaterialEditText) view;
                            materialEditText.setEnabled(false);
                            materialEditText.setTag(com.vijay.jsonwizard.R.id.after_look_up, true);
                            materialEditText.setText(text);
                            materialEditText.setInputType(InputType.TYPE_NULL);
                            disableEditText(materialEditText);
                        }
                    }

                    Map<String, String> metadataMap = new HashMap<>();
                    metadataMap.put(AppConstants.KEY.ENTITY_ID, AppConstants.KEY.MOTHER);
                    metadataMap.put(AppConstants.KEY.VALUE, getValue(pc.getColumnmaps(), MotherLookUpUtils.baseEntityId, false));

                    writeMetaDataValue(FormUtils.LOOK_UP_JAVAROSA_PROPERTY, metadataMap);

                    clearView();
                }
            }
        }
    }

    private final View.OnClickListener lookUpRecordOnClickLister = view -> {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
            CommonPersonObjectClient client = null;
            if (view.getTag() != null && view.getTag() instanceof CommonPersonObjectClient) {
                client = (CommonPersonObjectClient) view.getTag();
            }

            if (client != null) {
                lookupDialogDismissed(client);
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean fillFormCheck = true;

        if (item.getItemId() == com.vijay.jsonwizard.R.id.action_save) {
            JSONObject object = getStep("step1");
            try {
                if (object.getString(AppConstants.KEY.TITLE).contains("Record out of catchment area service")) {
                    fillFormCheck = ((AppStockJsonFormActivity) getActivity()).checkIfAtLeastOneServiceGiven();
                }
            } catch (Exception e) {
                Timber.e(e);
            }
        }

        if (fillFormCheck) {
            return super.onOptionsItemSelected(item);
        } else {
            String errorMessage = getString(R.string.fill_form_error_msg);

            final Snackbar snackbar = Snackbar
                    .make(getMainView(), errorMessage, Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.close, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackbar.dismiss();
                }
            });

            // Changing message text color
            snackbar.setActionTextColor(Color.WHITE);
            View sbView = snackbar.getView();
            TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);

            snackbar.show();
            return true;
        }
    }
}