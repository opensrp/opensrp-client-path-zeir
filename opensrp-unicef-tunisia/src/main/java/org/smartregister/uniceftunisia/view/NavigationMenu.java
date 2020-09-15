package org.smartregister.uniceftunisia.view;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Seconds;
import org.json.JSONObject;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.JsonFormUtils;
import org.smartregister.child.util.Utils;
import org.smartregister.domain.FetchStatus;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.reporting.service.IndicatorGeneratorIntentService;
import org.smartregister.uniceftunisia.R;
import org.smartregister.uniceftunisia.activity.ChildRegisterActivity;
import org.smartregister.uniceftunisia.activity.HIA2ReportsActivity;
import org.smartregister.uniceftunisia.application.UnicefTunisiaApplication;
import org.smartregister.uniceftunisia.contract.NavigationContract;
import org.smartregister.uniceftunisia.presenter.NavigationPresenter;
import org.smartregister.uniceftunisia.util.AppConstants;
import org.smartregister.util.FormUtils;
import org.smartregister.util.LangUtils;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

public class NavigationMenu implements NavigationContract.View, SyncStatusBroadcastReceiver.SyncStatusListener {

    private static NavigationMenu instance;
    private static WeakReference<Activity> activityWeakReference;
    private static String[] langArray;
    private LinearLayout syncMenuItem;
    private LinearLayout enrollmentMenuItem;
    private LinearLayout outOfAreaMenu;
    private LinearLayout registerView;
    private LinearLayout reportView;
    private TextView loggedInUserTextView;
    private TextView userInitialsTextView;
    private TextView syncTextView;
    private TextView logoutButton;
    private NavigationContract.Presenter mPresenter;
    private DrawerLayout drawer;
    private ImageButton cancelButton;
    private Spinner languageSpinner;

    public static NavigationMenu getInstance(@NonNull Activity activity) {

        SyncStatusBroadcastReceiver.getInstance().removeSyncStatusListener(instance);
        int orientation = activity.getResources().getConfiguration().orientation;
        activityWeakReference = new WeakReference<>(activity);
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (instance == null) {
                instance = new NavigationMenu();
                langArray = activity.getResources().getStringArray(R.array.languages);
            }
            instance.init(activity);
            SyncStatusBroadcastReceiver.getInstance().addSyncStatusListener(instance);
            return instance;
        } else {
            return null;
        }

    }

    private void init(Activity activity) {
        try {
            mPresenter = new NavigationPresenter(this);
            registerDrawer(activity);
            setParentView(activity);
            prepareViews(activity);
            appLogout(activity);
            syncApp(activity);
            goToReport();
            recordOutOfArea(activity);
            attachCloseDrawer();
            goToRegister();
            attachLanguageSpinner(activity);

        } catch (Exception e) {
            Timber.e(e.toString());
        }
    }

    @Override
    public void prepareViews(final Activity activity) {
        drawer = activity.findViewById(R.id.drawer_layout);
        drawer.setFilterTouchesWhenObscured(true);
        logoutButton = activity.findViewById(R.id.logout_button);
        syncMenuItem = activity.findViewById(R.id.sync_menu);
        outOfAreaMenu = activity.findViewById(R.id.out_of_area_menu);
        registerView = activity.findViewById(R.id.register_view);
        reportView = activity.findViewById(R.id.report_view);
        enrollmentMenuItem = activity.findViewById(R.id.enrollment);
        loggedInUserTextView = activity.findViewById(R.id.logged_in_user_text_view);
        userInitialsTextView = activity.findViewById(R.id.user_initials_text_view);
        syncTextView = activity.findViewById(R.id.sync_text_view);
        cancelButton = drawer.findViewById(R.id.cancel_button);
        languageSpinner = activity.findViewById(R.id.language_spinner);
        mPresenter.refreshLastSync();
    }

    private void registerDrawer(Activity parentActivity) {
        if (drawer != null) {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    parentActivity, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

        }
    }

    @Override
    public void onSyncStart() {
        //Do nothing
    }

    @Override
    public void onSyncInProgress(FetchStatus fetchStatus) {
        //Do nothing
    }

    @Override
    public void onSyncComplete(FetchStatus fetchStatus) {
        if (!fetchStatus.equals(FetchStatus.fetchedFailed) && !fetchStatus.equals(FetchStatus.noConnection)) {
            mPresenter.refreshLastSync();
        }
    }

    private void setParentView(Activity activity) {
        ViewGroup current = (ViewGroup) ((ViewGroup) (activity.findViewById(android.R.id.content))).getChildAt(0);
        if (!(current instanceof DrawerLayout)) {
            if (current.getParent() != null) {
                ((ViewGroup) current.getParent()).removeView(current);
            }

            LayoutInflater mInflater = LayoutInflater.from(activity);
            ViewGroup contentView = (ViewGroup) mInflater.inflate(R.layout.navigation_drawer, null);
            activity.setContentView(contentView);

            RelativeLayout rl = activity.findViewById(R.id.navigation_content);

            if (current.getParent() != null) {
                ((ViewGroup) current.getParent()).removeView(current);
            }

            if (current instanceof RelativeLayout) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                current.setLayoutParams(params);
            }
            rl.addView(current);
        }
    }

    private void syncApp(final Activity parentActivity) {
        syncMenuItem.setOnClickListener(v -> {
            Intent intent = new Intent(parentActivity.getApplicationContext(), IndicatorGeneratorIntentService.class);
            parentActivity.getApplicationContext().startService(intent);
            mPresenter.sync(parentActivity);
            Timber.i("IndicatorGeneratorIntentService start service called");
        });
    }

    private void goToReport() {
        reportView.setOnClickListener(v -> startReportActivity());
    }

    private void recordOutOfArea(final Activity parentActivity) {
        outOfAreaMenu.setOnClickListener(v -> startFormActivity(parentActivity));
    }

    private void attachCloseDrawer() {
        cancelButton.setOnClickListener(v -> {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
        });
    }

    private void appLogout(final Activity parentActivity) {
        mPresenter.displayCurrentUser();
        logoutButton.setOnClickListener(v -> logout(parentActivity));
    }

    private void goToRegister() {
        registerView.setOnClickListener(v -> {
            if (activityWeakReference.get() instanceof HIA2ReportsActivity) {
                // start register activity
                Intent intent = new Intent(activityWeakReference.get(), ChildRegisterActivity.class);
                activityWeakReference.get().startActivity(intent);
            } else {
                drawer.closeDrawer(GravityCompat.START);
            }
        });
    }

    @Override
    public void refreshCurrentUser(String name) {
        if (loggedInUserTextView != null) {
            loggedInUserTextView.setText(name);
        }
        if (userInitialsTextView != null && mPresenter.getLoggedInUserInitials() != null) {
            userInitialsTextView.setText(mPresenter.getLoggedInUserInitials());
        }
    }

    @Override
    public void logout(Activity activity) {
        Toast.makeText(activity.getApplicationContext(), activity.getResources().getText(R.string.action_log_out),
                Toast.LENGTH_SHORT).show();
        UnicefTunisiaApplication.getInstance().logoutCurrentUser();
    }

    private void startFormActivity(Activity activity) {
        try {
            JSONObject formJson = new FormUtils(activity).getFormJson(AppConstants.JsonForm.OUT_OF_CATCHMENT_SERVICE);
            JsonFormUtils.addAvailableVaccines(ChildLibrary.getInstance().context().applicationContext(), formJson);

            Form form = new Form();
            form.setWizard(false);
            form.setHideSaveLabel(true);
            form.setNextLabel("");

            Intent intent = new Intent(activity, Utils.metadata().childFormActivity);
            intent.putExtra(Constants.INTENT_KEY.JSON, formJson.toString());
            intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
            intent.putExtra(JsonFormConstants.PERFORM_FORM_TRANSLATION, true);
            activity.startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
        } catch (Exception e) {
            Timber.e(e);
        }
    }


    @Override
    public void refreshLastSync(Date lastSync) {
        if (syncTextView != null) {
            String lastSyncTime = getLastSyncTime();
            if (lastSync != null && !TextUtils.isEmpty(lastSyncTime)) {
                lastSyncTime = " " + String.format(activityWeakReference.get().getResources().getString(R.string.last_sync), lastSyncTime);
                syncTextView.setText(lastSyncTime);
            }
        }
    }

    private String getLastSyncTime() {
        String lastSync = "";
        long milliseconds = ChildLibrary.getInstance().getEcSyncHelper().getLastCheckTimeStamp();
        if (milliseconds > 0) {
            DateTime lastSyncTime = new DateTime(milliseconds);
            DateTime now = new DateTime(Calendar.getInstance());
            Minutes minutes = Minutes.minutesBetween(lastSyncTime, now);
            if (minutes.getMinutes() < 1) {
                Seconds seconds = Seconds.secondsBetween(lastSyncTime, now);
                lastSync = activityWeakReference.get().getString(R.string.x_seconds, seconds.getSeconds());
            } else if (minutes.getMinutes() >= 1 && minutes.getMinutes() < 60) {
                lastSync = activityWeakReference.get().getString(R.string.x_minutes, minutes.getMinutes());
            } else if (minutes.getMinutes() >= 60 && minutes.getMinutes() < 1440) {
                Hours hours = Hours.hoursBetween(lastSyncTime, now);
                lastSync = activityWeakReference.get().getString(R.string.x_hours, hours.getHours());
            } else {
                Days days = Days.daysBetween(lastSyncTime, now);
                lastSync = activityWeakReference.get().getString(R.string.x_days, days.getDays());
            }
        }
        return lastSync;
    }

    public DrawerLayout getDrawer() {
        return drawer;
    }

    private void attachLanguageSpinner(final Activity activity) {

        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(activityWeakReference.get(), R.array.languages, R.layout.language_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        languageSpinner.setAdapter(adapter);
        languageSpinner.setOnItemSelectedListener(null);
        String langPref = LangUtils.getLanguage(activity.getApplicationContext());
        for (int i = 0; i < langArray.length; i++) {

            if (langPref != null && langArray[i].toLowerCase().startsWith(langPref)) {
                languageSpinner.setSelection(i);
                break;
            } else {
                languageSpinner.setSelection(0);
            }
        }

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            int count = 0;

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (count >= 1) {

                    Timber.d("Selected %s", adapter.getItem(i));

                    String lang = adapter.getItem(i).toString().toLowerCase();
                    Locale LOCALE;
                    switch (lang) {
                        case "français":
                            LOCALE = Locale.FRENCH;
                            break;
                        case "عربى":
                        case "العربية":
                            LOCALE = new Locale(AppConstants.LOCALE.ARABIC_LOCALE);
                            languageSpinner.setSelection(i);
                            break;
                        default:
                            LOCALE = Locale.ENGLISH;
                            break;
                    }

                    // save language
                    LangUtils.saveLanguage(activity.getApplicationContext(), LOCALE.getLanguage());

                    launchActivity(activity, activity.getClass());
                }
                count++;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void launchActivity(Activity fromActivity, Class<?> clazz) {
        Intent intent = new Intent(fromActivity, clazz);
        fromActivity.getApplicationContext().startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    private void startReportActivity() {

        if (activityWeakReference.get() instanceof HIA2ReportsActivity) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }
        Intent intent = new Intent(activityWeakReference.get(), HIA2ReportsActivity.class);
        activityWeakReference.get().startActivity(intent);
        drawer.closeDrawer(GravityCompat.START);
    }

    public void openDrawer() {
        drawer.openDrawer(GravityCompat.START);
    }

    public static void closeDrawer() {
        if (instance != null && instance.getDrawer() != null) {
            instance.getDrawer().closeDrawer(Gravity.START);
        }
    }
}
