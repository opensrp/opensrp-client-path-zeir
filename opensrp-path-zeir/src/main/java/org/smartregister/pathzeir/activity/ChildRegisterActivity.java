package org.smartregister.pathzeir.activity;

import android.content.Intent;
import android.view.MenuItem;

import androidx.fragment.app.Fragment;

import com.google.android.gms.vision.barcode.Barcode;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.child.activity.BaseChildRegisterActivity;
import org.smartregister.child.util.ChildJsonFormUtils;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.Utils;
import org.smartregister.client.utils.domain.Form;
import org.smartregister.pathzeir.R;
import org.smartregister.pathzeir.fragment.AdvancedSearchFragment;
import org.smartregister.pathzeir.fragment.ChildRegisterFragment;
import org.smartregister.pathzeir.model.AppChildRegisterModel;
import org.smartregister.pathzeir.presenter.AppChildRegisterPresenter;
import org.smartregister.pathzeir.util.AppConstants;
import org.smartregister.pathzeir.view.NavDrawerActivity;
import org.smartregister.pathzeir.view.NavigationMenu;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.lang.ref.WeakReference;

public class ChildRegisterActivity extends BaseChildRegisterActivity implements NavDrawerActivity {

    private NavigationMenu navigationMenu;
    private Fragment[] fragments;

    @Override
    protected Fragment[] getOtherFragments() {
        ADVANCED_SEARCH_POSITION = 1;
        fragments = new Fragment[1];
        fragments[ADVANCED_SEARCH_POSITION - 1] = new WeakReference<>(new AdvancedSearchFragment()).get();
        return fragments;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AllConstants.BARCODE.BARCODE_REQUEST_CODE && resultCode == RESULT_OK) {
            Barcode barcode = data.getParcelableExtra(AllConstants.BARCODE.BARCODE_KEY);
            ((AppChildRegisterPresenter) presenter).updateChildCardStatus(barcode.displayValue);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public String getRegistrationForm() {
        return AppConstants.JsonForm.CHILD_ENROLLMENT;
    }

    @Override
    public void startNFCCardScanner() {
        // Todo
    }

    @Override
    protected void initializePresenter() {
        presenter = new AppChildRegisterPresenter(this, new AppChildRegisterModel());
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        WeakReference<ChildRegisterFragment> weakReference = new WeakReference<>(new ChildRegisterFragment());
        return weakReference.get();
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        WeakReference<ChildRegisterActivity> weakReference = new WeakReference<>(this);
        navigationMenu = NavigationMenu.getInstance(weakReference.get());
    }

    @Override
    public void openDrawer() {
        if (navigationMenu != null) {
            navigationMenu.openDrawer();
        }
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Override
    public void startFormActivity(JSONObject jsonForm) {
        Form form = new Form();
        form.setWizard(false);
        form.setHideSaveLabel(true);
        form.setNextLabel("");

        Intent intent = new Intent(this, Utils.metadata().childFormActivity);
        intent.putExtra(Constants.INTENT_KEY.JSON, jsonForm.toString());
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
        intent.putExtra(JsonFormConstants.PERFORM_FORM_TRANSLATION, true);
        startActivityForResult(intent, ChildJsonFormUtils.REQUEST_CODE_GET_JSON);
    }

    @Override
    protected void updateSearchItems(String barcodeSearchTerm) {
        advancedSearchFormData.put(AppConstants.KeyConstants.ZEIR_ID, barcodeSearchTerm);
        Fragment fragment = fragments[ADVANCED_SEARCH_POSITION - 1];
        if (fragment instanceof AdvancedSearchFragment) {
            AdvancedSearchFragment advancedSearchFragment = (AdvancedSearchFragment) fragment;
            advancedSearchFragment.searchByOpenSRPId(barcodeSearchTerm);
        }
    }

    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    protected void registerBottomNavigation() {
        super.registerBottomNavigation();

        MenuItem clients = bottomNavigationView.getMenu().findItem(R.id.action_clients);
        if (clients != null) {
            clients.setTitle(getString(R.string.header_children));
        }
        bottomNavigationView.getMenu().removeItem(R.id.action_library);
    }
}
