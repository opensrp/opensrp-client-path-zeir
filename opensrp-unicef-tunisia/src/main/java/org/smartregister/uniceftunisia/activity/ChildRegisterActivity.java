package org.smartregister.uniceftunisia.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;
import org.smartregister.child.activity.BaseChildRegisterActivity;
import org.smartregister.child.model.BaseChildRegisterModel;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.JsonFormUtils;
import org.smartregister.child.util.Utils;
import org.smartregister.uniceftunisia.R;
import org.smartregister.uniceftunisia.contract.NavigationMenuContract;
import org.smartregister.uniceftunisia.event.LoginEvent;
import org.smartregister.uniceftunisia.fragment.ChildRegisterFragment;
import org.smartregister.uniceftunisia.presenter.AppChildRegisterPresenter;
import org.smartregister.uniceftunisia.util.AppConstants;
import org.smartregister.uniceftunisia.util.AppUtils;
import org.smartregister.uniceftunisia.view.NavDrawerActivity;
import org.smartregister.uniceftunisia.view.NavigationMenu;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.lang.ref.WeakReference;

public class ChildRegisterActivity extends BaseChildRegisterActivity implements NavDrawerActivity, NavigationMenuContract {

    private NavigationMenu navigationMenu;

    @Override
    public NavigationMenu getNavigationMenu() {
        return navigationMenu;
    }

    @Override
    protected void attachBaseContext(android.content.Context base) {
        // get language from prefs
        String lang = AppUtils.getLanguage(base.getApplicationContext());
        super.attachBaseContext(AppUtils.setAppLocale(base, lang));
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Fragment[] getOtherFragments() {
        return new Fragment[0];
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void registerBottomNavigation() {
        //do nothing
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public String getRegistrationForm() {
        return AppConstants.JSON_FORM.CHILD_ENROLLMENT;
    }

    @Override
    public void startNFCCardScanner() {
        // Todo
    }

    @Override
    protected void initializePresenter() {
        presenter = new AppChildRegisterPresenter(this, new BaseChildRegisterModel());
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        WeakReference<ChildRegisterFragment> childRegisterFragmentWeakReference = new WeakReference<>(
                new ChildRegisterFragment());

        return childRegisterFragmentWeakReference.get();
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        createDrawer();
    }

    private void createDrawer() {
        WeakReference<ChildRegisterActivity> childRegisterActivityWeakReference = new WeakReference<>(this);
        navigationMenu = NavigationMenu.getInstance(childRegisterActivityWeakReference.get(), null, null);
        navigationMenu.getNavigationAdapter().setSelectedView(AppConstants.DrawerMenu.CHILD_CLIENTS);
        navigationMenu.runRegisterCount();
    }

    @Override
    public void openDrawer() {
        if (navigationMenu != null) {
            navigationMenu.openDrawer();
        }
    }

    @Override
    public void closeDrawer() {
        if (navigationMenu != null) {
            navigationMenu.closeDrawer();
        }
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void showNfcNotInstalledDialog(LoginEvent event) {
        if (event != null) {
            AppUtils.removeStickyEvent(event);

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    showNfcDialog();
                }
            });
        }
    }

    private void showNfcDialog() {
        AppUtils.showDialogMessage(this, R.string.nfc_sdk_missing, R.string.please_install_nfc_sdk);
    }

    public void refresh() {
        Intent intent = new Intent(ChildRegisterActivity.this, ChildRegisterActivity.class);
        getApplicationContext()
                .startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @Override
    public void startFormActivity(JSONObject jsonForm) {
        Intent intent = new Intent(this, Utils.metadata().childFormActivity);
        if (jsonForm.has(AppConstants.KEY.ENCOUNTER_TYPE) && jsonForm.optString(AppConstants.KEY.ENCOUNTER_TYPE).equals(
                AppConstants.KEY.BIRTH_REGISTRATION)) {
            JsonFormUtils.addChildRegLocHierarchyQuestions(jsonForm);
        }
        intent.putExtra(Constants.INTENT_KEY.JSON, jsonForm.toString());

        Form form = new Form();
        form.setWizard(false);
        form.setHideSaveLabel(true);
        form.setNextLabel("");

        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
        startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
    }


    @Override
    public void finishActivity() {
        finish();
    }
}
