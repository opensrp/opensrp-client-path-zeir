package org.smartregister.uniceftunisia.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.child.fragment.BaseAdvancedSearchFragment;
import org.smartregister.child.presenter.BaseChildAdvancedSearchPresenter;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.DBConstants;
import org.smartregister.uniceftunisia.R;
import org.smartregister.uniceftunisia.presenter.AdvancedSearchPresenter;
import org.smartregister.uniceftunisia.util.AppConstants;
import org.smartregister.uniceftunisia.util.DBQueryHelper;
import org.smartregister.util.Utils;
import org.smartregister.view.activity.BaseRegisterActivity;

import java.util.HashMap;
import java.util.Map;

import static org.smartregister.child.util.Constants.CHILD_STATUS.ACTIVE;
import static org.smartregister.child.util.Constants.CHILD_STATUS.INACTIVE;
import static org.smartregister.child.util.Constants.CHILD_STATUS.LOST_TO_FOLLOW_UP;

/**
 * Created by ndegwamartin on 08/03/2019.
 */
public class AdvancedSearchFragment extends BaseAdvancedSearchFragment {

    private AdvancedSearchPresenter presenter;
    private MaterialEditText firstName;
    private MaterialEditText lastName;
    private MaterialEditText openSrpId;
    private MaterialEditText motherGuardianFirstName;
    private MaterialEditText motherGuardianLastName;
    private MaterialEditText motherGuardianPhoneNumber;
    private MaterialEditText childRegistrationNumber;
    private MaterialEditText childUniqueGovtId;
    private MaterialEditText cardId;

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

        cardId = view.findViewById(org.smartregister.child.R.id.card_id);
        cardId.addTextChangedListener(advancedSearchTextwatcher);
        cardId.setEnabled(false);

        Button scanCardQRCodeView = view.findViewById(R.id.scanCardButton);
        scanCardQRCodeView.setText(R.string.scan_qr_code);
        scanCardQRCodeView.setVisibility(View.VISIBLE);
        scanCardQRCodeView.setEnabled(false);
        scanCardQRCodeView.setOnClickListener(view1 -> {
            if (getActivity() == null) {
                return;
            }
            Utils.showShortToast(getActivity(), "Implement card support");
        });
    }

    @Override
    public void populateSearchableFields(View view) {

        TextView dobRange = view.findViewById(R.id.date_of_birth_range_label);
        dobRange.setText(R.string.dob_range);

        firstName = view.findViewById(org.smartregister.child.R.id.first_name);
        firstName.addTextChangedListener(advancedSearchTextwatcher);
        firstName.setHint(R.string.child_first_name);
        firstName.setFloatingLabelText(getString(R.string.child_first_name));

        lastName = view.findViewById(org.smartregister.child.R.id.last_name);
        lastName.addTextChangedListener(advancedSearchTextwatcher);
        lastName.setHint(R.string.child_last_name);
        lastName.setFloatingLabelText(getString(R.string.child_last_name));

        openSrpId = view.findViewById(org.smartregister.child.R.id.opensrp_id);
        openSrpId.addTextChangedListener(advancedSearchTextwatcher);
        openSrpId.setVisibility(View.GONE);

        motherGuardianFirstName = view.findViewById(org.smartregister.child.R.id.mother_guardian_first_name);
        motherGuardianFirstName.addTextChangedListener(advancedSearchTextwatcher);
        motherGuardianFirstName.setHint(R.string.mother_caregiver_first_name);
        motherGuardianFirstName.setFloatingLabelText(getString(R.string.mother_caregiver_first_name));

        motherGuardianLastName = view.findViewById(org.smartregister.child.R.id.mother_guardian_last_name);
        motherGuardianLastName.addTextChangedListener(advancedSearchTextwatcher);
        motherGuardianLastName.setHint(R.string.mother_caregiver_last_name);
        motherGuardianLastName.setFloatingLabelText(getString(R.string.mother_caregiver_last_name));

        motherGuardianPhoneNumber = view.findViewById(org.smartregister.child.R.id.mother_guardian_phone_number);
        motherGuardianPhoneNumber.addTextChangedListener(advancedSearchTextwatcher);
        motherGuardianPhoneNumber.setHint(R.string.mother_caregiver_phone);
        motherGuardianPhoneNumber.setFloatingLabelText(getString(R.string.mother_caregiver_phone));

        //Defaults
        startDate.addTextChangedListener(advancedSearchTextwatcher);
        endDate.addTextChangedListener(advancedSearchTextwatcher);
        childUniqueGovtId.addTextChangedListener(advancedSearchTextwatcher);
        childRegistrationNumber.addTextChangedListener(advancedSearchTextwatcher);

        advancedFormSearchableFields.put(AppConstants.KEY.CARD_ID, cardId);
        advancedFormSearchableFields.put(AppConstants.KEY.BIRTH_REGISTRATION_NUMBER, childRegistrationNumber);
        advancedFormSearchableFields.put(AppConstants.KEY.CHILD_REG, childUniqueGovtId);
        advancedFormSearchableFields.put(DBConstants.KEY.FIRST_NAME, firstName);
        advancedFormSearchableFields.put(DBConstants.KEY.LAST_NAME, lastName);
        advancedFormSearchableFields.put(DBConstants.KEY.ZEIR_ID, openSrpId);
        advancedFormSearchableFields.put(DBConstants.KEY.MOTHER_FIRST_NAME, motherGuardianFirstName);
        advancedFormSearchableFields.put(DBConstants.KEY.MOTHER_LAST_NAME, motherGuardianLastName);
        advancedFormSearchableFields.put(AppConstants.KEY.MOTHER_PHONE_NUMBER, motherGuardianPhoneNumber);
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
    public void onClick(View view) {
        view.toString();
    }

    @Override
    public void assignedValuesBeforeBarcode() {
        if (searchFormData.size() > 0) {
            firstName.setText(searchFormData.get(DBConstants.KEY.FIRST_NAME));
            lastName.setText(searchFormData.get(DBConstants.KEY.LAST_NAME));
            motherGuardianFirstName.setText(searchFormData.get(DBConstants.KEY.MOTHER_FIRST_NAME));
            motherGuardianLastName.setText(searchFormData.get(DBConstants.KEY.MOTHER_LAST_NAME));
            motherGuardianPhoneNumber.setText(searchFormData.get(AppConstants.KEY.MOTHER_PHONE_NUMBER));
            openSrpId.setText(searchFormData.get(DBConstants.KEY.ZEIR_ID));
            cardId.setText(searchFormData.get(AppConstants.KEY.CARD_ID));
            childRegistrationNumber.setText(searchFormData.get(AppConstants.KEY.BIRTH_REGISTRATION_NUMBER));
            childUniqueGovtId.setText(searchFormData.get(AppConstants.KEY.CHILD_REG));
        }
    }

    @Override
    protected HashMap<String, String> createSelectedFieldMap() {
        HashMap<String, String> fields = new HashMap<>();
        fields.put(DBConstants.KEY.FIRST_NAME, firstName.getText().toString());
        fields.put(DBConstants.KEY.LAST_NAME, lastName.getText().toString());
        fields.put(DBConstants.KEY.MOTHER_FIRST_NAME, motherGuardianFirstName.getText().toString());
        fields.put(DBConstants.KEY.MOTHER_LAST_NAME, motherGuardianLastName.getText().toString());
        fields.put(AppConstants.KEY.MOTHER_PHONE_NUMBER, motherGuardianPhoneNumber.getText().toString());
        fields.put(DBConstants.KEY.ZEIR_ID, openSrpId.getText().toString());
        fields.put(START_DATE, startDate.getText().toString());
        fields.put(END_DATE, endDate.getText().toString());
        fields.put(AppConstants.KEY.CARD_ID, cardId.getText().toString());
        fields.put(AppConstants.KEY.BIRTH_REGISTRATION_NUMBER, childRegistrationNumber.getText().toString());
        fields.put(AppConstants.KEY.CHILD_REG, childUniqueGovtId.getText().toString());
        return fields;
    }

    @Override
    protected void clearFormFields() {
        super.clearFormFields();
        cardId.setText("");
        childRegistrationNumber.setText("");
        childUniqueGovtId.setText("");
        openSrpId.setText("");
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

        String firstName = this.firstName.getText().toString();
        String lastName = this.lastName.getText().toString();
        String motherGuardianFirstNameString = motherGuardianFirstName.getText().toString();
        String motherGuardianLastNameString = motherGuardianLastName.getText().toString();
        String motherGuardianPhoneNumberString = motherGuardianPhoneNumber.getText().toString();
        String zeir = openSrpId.getText().toString();

        if (StringUtils.isNotBlank(motherGuardianFirstNameString)) {
            searchParams.put(DBConstants.KEY.MOTHER_FIRST_NAME, motherGuardianFirstNameString);
        }

        if (StringUtils.isNotBlank(motherGuardianLastNameString)) {
            searchParams.put(DBConstants.KEY.MOTHER_LAST_NAME, motherGuardianLastNameString);
        }

        if (StringUtils.isNotBlank(motherGuardianPhoneNumberString)) {
            searchParams.put(AppConstants.KEY.MOTHER_PHONE_NUMBER, motherGuardianPhoneNumberString);
        }

        if (!TextUtils.isEmpty(firstName)) {
            searchParams.put(DBConstants.KEY.FIRST_NAME, firstName);
        }

        if (!TextUtils.isEmpty(lastName)) {
            searchParams.put(DBConstants.KEY.LAST_NAME, lastName);
        }

        if (!TextUtils.isEmpty(zeir)) {
            searchParams.put(DBConstants.KEY.ZEIR_ID, zeir);
        }

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
            searchParams.remove(Constants.CHILD_STATUS.INACTIVE);
            searchParams.remove(Constants.CHILD_STATUS.ACTIVE);
            searchParams.remove(Constants.CHILD_STATUS.LOST_TO_FOLLOW_UP);
        }

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
            searchParams.put(AppConstants.KEY.CARD_ID, cardId);
        }
        
        String childUniqueId = this.childUniqueGovtId.getText().toString();
        if (!TextUtils.isEmpty(childUniqueId)) {
            searchParams.put(AppConstants.KEY.CHILD_REG, childUniqueId);
        }  

        String childRegNumber = this.childRegistrationNumber.getText().toString();
        if (!TextUtils.isEmpty(childRegNumber)) {
            searchParams.put(AppConstants.KEY.BIRTH_REGISTRATION_NUMBER, childRegNumber);
        }        
        
        return searchParams;
    }
}

