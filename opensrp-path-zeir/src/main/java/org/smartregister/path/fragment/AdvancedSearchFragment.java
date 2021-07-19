package org.smartregister.path.fragment;

import static org.smartregister.child.util.Constants.CHILD_STATUS.ACTIVE;
import static org.smartregister.child.util.Constants.CHILD_STATUS.INACTIVE;
import static org.smartregister.child.util.Constants.CHILD_STATUS.LOST_TO_FOLLOW_UP;

import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.customviews.RadioButton;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.child.fragment.BaseAdvancedSearchFragment;
import org.smartregister.child.presenter.BaseChildAdvancedSearchPresenter;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.DBConstants;
import org.smartregister.path.R;
import org.smartregister.path.activity.ChildRegisterActivity;
import org.smartregister.path.presenter.AdvancedSearchPresenter;
import org.smartregister.path.util.AppConstants;
import org.smartregister.path.util.DBQueryHelper;
import org.smartregister.view.activity.BaseRegisterActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ndegwamartin on 08/03/2019.
 */
public class AdvancedSearchFragment extends BaseAdvancedSearchFragment {

    private AdvancedSearchPresenter presenter;
    private MaterialEditText firstName;
    private MaterialEditText lastName;
    private MaterialEditText motherGuardianFirstName;
    private MaterialEditText motherGuardianLastName;
    private MaterialEditText motherGuardianPhoneNumber;
    private MaterialEditText childRegistrationNumber;
    private MaterialEditText childUniqueGovtId;
    private MaterialEditText cardId;
    private RadioButton myCatchmentRadioButton;
    private RadioButton outAndInMyCatchmentRadio;

    @Override
    protected BaseChildAdvancedSearchPresenter getPresenter() {
        if (presenter == null && getActivity() != null) {
            String viewConfigurationIdentifier = ((BaseRegisterActivity) getActivity()).getViewIdentifiers().get(0);
            presenter = new AdvancedSearchPresenter(this, viewConfigurationIdentifier);
        }
        return presenter;
    }

    @Override
    public boolean onBackPressed() {
        if (getActivity() != null)
            ((BaseRegisterActivity) getActivity()).setSelectedBottomBarMenuItem(R.id.action_home);
        return super.onBackPressed();
    }

    @Override
    protected void populateFormViews(View view) {
        super.populateFormViews(view);
        view.findViewById(R.id.child_birth_reg_number_layout).setVisibility(View.VISIBLE);
        view.findViewById(R.id.child_unique_reg_layout).setVisibility(View.VISIBLE);
        childRegistrationNumber = view.findViewById(R.id.child_birth_reg_number);
        childUniqueGovtId = view.findViewById(R.id.child_reg);
        enableCardSupport(view);
        qrCodeButton.setVisibility(View.GONE);
    }

    private void enableCardSupport(View view) {
        //TODO enable this when working on card support
        View cardLayoutSupport = view.findViewById(R.id.card_support_layout);
        cardLayoutSupport.setVisibility(View.VISIBLE);
        cardLayoutSupport.setEnabled(false);

        cardId = view.findViewById(R.id.card_id);
        cardId.addTextChangedListener(advancedSearchTextwatcher);
        cardId.setEnabled(true);
        cardId.setInputType(InputType.TYPE_CLASS_NUMBER);

        Button scanCardQRCodeView = view.findViewById(R.id.scanCardButton);
        scanCardQRCodeView.setText(R.string.scan_qr_code);
        scanCardQRCodeView.setVisibility(View.VISIBLE);
        scanCardQRCodeView.setEnabled(true);
        scanCardQRCodeView.setOnClickListener(button -> {
            if (getActivity() != null) {
                ChildRegisterActivity childRegisterActivity = (ChildRegisterActivity) getActivity();
                childRegisterActivity.setAdvancedSearch(true);
                childRegisterActivity.startQrCodeScanner();
            }
        });
    }

    @Override
    public void populateSearchableFields(View view) {

        myCatchmentRadioButton = view.findViewById(R.id.my_catchment);
        outAndInMyCatchmentRadio = view.findViewById(R.id.out_and_inside);

        TextView dobRange = view.findViewById(R.id.date_of_birth_range_label);
        dobRange.setText(R.string.dob_range);

        firstName = view.findViewById(R.id.first_name);
        firstName.addTextChangedListener(advancedSearchTextwatcher);
        firstName.setHint(R.string.child_first_name);
        firstName.setFloatingLabelText(getString(R.string.child_first_name));

        lastName = view.findViewById(R.id.last_name);
        lastName.addTextChangedListener(advancedSearchTextwatcher);
        lastName.setHint(R.string.child_last_name);
        lastName.setFloatingLabelText(getString(R.string.child_last_name));

        view.findViewById(R.id.opensrp_id).setVisibility(View.GONE);

        motherGuardianFirstName = view.findViewById(R.id.mother_guardian_first_name);
        motherGuardianFirstName.addTextChangedListener(advancedSearchTextwatcher);
        motherGuardianFirstName.setHint(R.string.mother_caregiver_first_name);
        motherGuardianFirstName.setFloatingLabelText(getString(R.string.mother_caregiver_first_name));

        motherGuardianLastName = view.findViewById(R.id.mother_guardian_last_name);
        motherGuardianLastName.addTextChangedListener(advancedSearchTextwatcher);
        motherGuardianLastName.setHint(R.string.mother_caregiver_last_name);
        motherGuardianLastName.setFloatingLabelText(getString(R.string.mother_caregiver_last_name));

        motherGuardianPhoneNumber = view.findViewById(R.id.mother_guardian_phone_number);
        motherGuardianPhoneNumber.addTextChangedListener(advancedSearchTextwatcher);
        motherGuardianPhoneNumber.setHint(R.string.mother_caregiver_phone);
        motherGuardianPhoneNumber.setFloatingLabelText(getString(R.string.mother_caregiver_phone));
        motherGuardianPhoneNumber.setInputType(InputType.TYPE_CLASS_NUMBER);

        //Defaults
        startDate.addTextChangedListener(advancedSearchTextwatcher);
        endDate.addTextChangedListener(advancedSearchTextwatcher);
        childUniqueGovtId.addTextChangedListener(advancedSearchTextwatcher);
        childUniqueGovtId.setInputType(InputType.TYPE_CLASS_NUMBER);
        childRegistrationNumber.addTextChangedListener(advancedSearchTextwatcher);

        advancedFormSearchableFields.put(AppConstants.KeyConstants.ZEIR_ID, cardId);
        advancedFormSearchableFields.put(AppConstants.KeyConstants.CHILD_BIRTH_CERTIFICATE,  childUniqueGovtId);
        advancedFormSearchableFields.put(AppConstants.KeyConstants.CHILD_REGISTER_CARD_NUMBER, childRegistrationNumber);
        advancedFormSearchableFields.put(DBConstants.KEY.FIRST_NAME, firstName);
        advancedFormSearchableFields.put(DBConstants.KEY.LAST_NAME, lastName);
        advancedFormSearchableFields.put(DBConstants.KEY.MOTHER_FIRST_NAME, motherGuardianFirstName);
        advancedFormSearchableFields.put(DBConstants.KEY.MOTHER_LAST_NAME, motherGuardianLastName);
        advancedFormSearchableFields.put(Constants.KEY.MOTHER_GUARDIAN_NUMBER, motherGuardianPhoneNumber);
        advancedFormSearchableFields.put(START_DATE, startDate);
        advancedFormSearchableFields.put(END_DATE, endDate);
    }

    @Override
    protected String getDefaultSortQuery() {
        return presenter.getDefaultSortQuery();
    }

    @Override
    protected String filterSelectionCondition(boolean urgentOnly) {
        return DBQueryHelper.getFilterSelectionCondition(urgentOnly);
    }

    @Override
    public void setAdvancedSearchFormData(HashMap<String, String> formData) {
        this.searchFormData = formData;
    }

    @Override
    public void assignedValuesBeforeBarcode() {
        if (searchFormData.size() > 0) {
            firstName.setText(searchFormData.get(DBConstants.KEY.FIRST_NAME));
            lastName.setText(searchFormData.get(DBConstants.KEY.LAST_NAME));
            motherGuardianFirstName.setText(searchFormData.get(DBConstants.KEY.MOTHER_FIRST_NAME));
            motherGuardianLastName.setText(searchFormData.get(DBConstants.KEY.MOTHER_LAST_NAME));
            motherGuardianPhoneNumber.setText(searchFormData.get(Constants.KEY.MOTHER_GUARDIAN_NUMBER));
            cardId.setText(searchFormData.get(AppConstants.KeyConstants.ZEIR_ID));
            childUniqueGovtId.setText(searchFormData.get(AppConstants.KeyConstants.CHILD_BIRTH_CERTIFICATE));
            childRegistrationNumber.setText(searchFormData.get(AppConstants.KeyConstants.CHILD_REGISTER_CARD_NUMBER));
        }
    }

    @Override
    protected HashMap<String, String> createSelectedFieldMap() {
        HashMap<String, String> fields = new HashMap<>();
        fields.put(DBConstants.KEY.FIRST_NAME, firstName.getText().toString());
        fields.put(DBConstants.KEY.LAST_NAME, lastName.getText().toString());
        fields.put(DBConstants.KEY.MOTHER_FIRST_NAME, motherGuardianFirstName.getText().toString());
        fields.put(DBConstants.KEY.MOTHER_LAST_NAME, motherGuardianLastName.getText().toString());
        fields.put(Constants.KEY.MOTHER_GUARDIAN_NUMBER, motherGuardianPhoneNumber.getText().toString());
        fields.put(START_DATE, startDate.getText().toString());
        fields.put(END_DATE, endDate.getText().toString());
        fields.put(AppConstants.KeyConstants.ZEIR_ID, cardId.getText().toString());
        fields.put(AppConstants.KeyConstants.CHILD_REGISTER_CARD_NUMBER, childRegistrationNumber.getText().toString());
        fields.put(AppConstants.KeyConstants.CHILD_BIRTH_CERTIFICATE, childUniqueGovtId.getText().toString());
        return fields;
    }

    @Override
    protected void clearFormFields() {
        super.clearFormFields();
        cardId.setText("");
        childRegistrationNumber.setText("");
        childUniqueGovtId.setText("");
        firstName.setText("");
        lastName.setText("");
        motherGuardianFirstName.setText("");
        motherGuardianLastName.setText("");
        motherGuardianPhoneNumber.setText("");
    }

    @Override
    protected String getMainCondition() {
        return DBQueryHelper.getHomeRegisterCondition();
    }

    @Override
    protected Map<String, String> getSearchMap(boolean outOfArea) {

        Map<String, String> searchParams = new HashMap<>();

        searchParams.putAll(getFieldValuesParams());
        searchParams.putAll(getStatusInfoParams());
        searchParams.putAll(getIdsInfoParams());

        return searchParams;
    }

    private Map<String, String> getIdsInfoParams() {
        Map<String, String> searchParams = new HashMap<>();

        String startDateString = startDate.getText().toString();
        if (StringUtils.isNotBlank(startDateString)) {
            searchParams.put(START_DATE, startDateString.trim());
        }

        String endDateString = endDate.getText().toString();
        if (StringUtils.isNotBlank(endDateString)) {
            searchParams.put(END_DATE, endDateString.trim());
        }

        String cardId = this.cardId.getText().toString();
        if (!TextUtils.isEmpty(cardId)) {
            searchParams.put(AppConstants.KeyConstants.ZEIR_ID, cardId);
        }

        String childUniqueId = this.childUniqueGovtId.getText().toString();
        if (!TextUtils.isEmpty(childUniqueId)) {
            searchParams.put(AppConstants.KeyConstants.CHILD_BIRTH_CERTIFICATE, childUniqueId);
        }

        String childRegNumber = this.childRegistrationNumber.getText().toString();
        if (!TextUtils.isEmpty(childRegNumber)) {
            searchParams.put(AppConstants.KeyConstants.CHILD_REGISTER_CARD_NUMBER, childRegNumber);
        }
        return searchParams;
    }

    private Map<String, String> getStatusInfoParams() {
        Map<String, String> searchParams = new HashMap<>();

        //Inactive
        boolean isInactive = inactive.isChecked();
        if (isInactive) {
            searchParams.put(INACTIVE, Boolean.toString(true));
        }
        //Active
        boolean isActive = active.isChecked();
        if (isActive) {
            searchParams.put(ACTIVE, Boolean.toString(true));
        }

        //Lost To Follow Up
        boolean isLostToFollowUp = lostToFollowUp.isChecked();
        if (isLostToFollowUp) {
            searchParams.put(LOST_TO_FOLLOW_UP, Boolean.toString(true));
        }

        if (isActive == isInactive && isActive == isLostToFollowUp) {
            searchParams.remove(INACTIVE);
            searchParams.remove(ACTIVE);
            searchParams.remove(LOST_TO_FOLLOW_UP);
        }

        return searchParams;
    }

    private Map<String, String> getFieldValuesParams() {
        Map<String, String> searchParams = new HashMap<>();

        String firstName = this.firstName.getText().toString();
        String lastName = this.lastName.getText().toString();
        String motherGuardianFirstNameString = motherGuardianFirstName.getText().toString();
        String motherGuardianLastNameString = motherGuardianLastName.getText().toString();
        String motherGuardianPhoneNumberString = motherGuardianPhoneNumber.getText().toString();

        if (StringUtils.isNotBlank(motherGuardianFirstNameString)) {
            searchParams.put(DBConstants.KEY.MOTHER_FIRST_NAME, motherGuardianFirstNameString);
        }

        if (StringUtils.isNotBlank(motherGuardianLastNameString)) {
            searchParams.put(DBConstants.KEY.MOTHER_LAST_NAME, motherGuardianLastNameString);
        }

        if (StringUtils.isNotBlank(motherGuardianPhoneNumberString)) {
            searchParams.put(Constants.KEY.MOTHER_GUARDIAN_NUMBER, motherGuardianPhoneNumberString);
        }

        if (!TextUtils.isEmpty(firstName)) {
            searchParams.put(DBConstants.KEY.FIRST_NAME, firstName);
        }

        if (!TextUtils.isEmpty(lastName)) {
            searchParams.put(DBConstants.KEY.LAST_NAME, lastName);
        }
        return searchParams;
    }


    /*
     *  No Need update count from this fragment
     * The whole functionality is working in
     * ChildRegisterFragment already
     */
    @Override
    protected void updateDueOverdueCountText() {
        /*
         *  No Need update count from this fragment
         * The whole functionality is working in
         * ChildRegisterFragment already
         */
    }

    public void searchByOpenSRPId(String barcodeSearchTerm) {
        boolean searchLocally = myCatchmentRadioButton.isChecked();

        if (outAndInMyCatchmentRadio.isChecked()) {
            searchLocally = false;
        }

        Map<String, String> searchParamsMap = new HashMap<>();
        searchParamsMap.put(AppConstants.KeyConstants.ZEIR_ID, barcodeSearchTerm);
        presenter.search(searchParamsMap, searchLocally);
    }
}

