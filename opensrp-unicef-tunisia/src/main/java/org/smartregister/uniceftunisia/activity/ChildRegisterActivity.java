package org.smartregister.uniceftunisia.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;
import org.smartregister.child.activity.BaseChildRegisterActivity;
import org.smartregister.child.model.BaseChildRegisterModel;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.JsonFormUtils;
import org.smartregister.child.util.Utils;
import org.smartregister.uniceftunisia.R;
import org.smartregister.uniceftunisia.contract.NavigationMenuContract;
import org.smartregister.uniceftunisia.fragment.AdvancedSearchFragment;
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

    @Override
    protected Fragment[] getOtherFragments() {
        ADVANCED_SEARCH_POSITION = 1;
        Fragment[] fragments = new Fragment[1];
        fragments[ADVANCED_SEARCH_POSITION - 1] = new WeakReference<>(new AdvancedSearchFragment()).get();
        return fragments;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
        WeakReference<ChildRegisterFragment> weakReference = new WeakReference<>(new ChildRegisterFragment());
        return weakReference.get();
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        createDrawer();
    }

    private void createDrawer() {
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
    public void closeDrawer() {
        if (navigationMenu != null) {
            NavigationMenu.closeDrawer();
        }
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
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
//        intent.putExtra(JsonFormConstants.PERFORM_FORM_TRANSLATION,  true);
        startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
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
        bottomNavigationView.getMenu().removeItem(org.smartregister.R.id.action_library);
    }
}
