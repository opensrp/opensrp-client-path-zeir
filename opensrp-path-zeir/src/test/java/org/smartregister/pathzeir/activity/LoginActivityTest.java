package org.smartregister.pathzeir.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.pathzeir.BaseActivityUnitTest;
import org.smartregister.pathzeir.R;
import org.smartregister.view.contract.BaseLoginContract;

public class LoginActivityTest extends BaseActivityUnitTest {

    private static final String STRING_SETTINGS = "Settings";
    private LoginActivity loginActivity;
    private ActivityController<LoginActivity> controller;

    @Mock
    private Menu menu;

    @Mock
    private BaseLoginContract.Presenter presenter;

    @Mock
    private ProgressDialog progressDialog;

    @Mock
    private Button loginButton;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        controller = Robolectric.buildActivity(LoginActivity.class).create().start();
        loginActivity = controller.get();
    }

    @After
    public void tearDown() {
        destroyController();
    }

    @Test
    public void testUserNameEditTextIsInitialized() {
        EditText userNameEditText = ReflectionHelpers.getField(loginActivity, "userNameEditText");
        Assert.assertNotNull(userNameEditText);
    }

    @Test
    public void testPasswordEditTextIsInitialized() {
        EditText userPasswordEditText = ReflectionHelpers.getField(loginActivity, "passwordEditText");
        Assert.assertNotNull(userPasswordEditText);
    }


    @Test
    public void testShowPasswordCheckBoxIsInitialized() {
        CheckBox showPasswordCheckBox = ReflectionHelpers.getField(loginActivity, "showPasswordCheckBox");
        Assert.assertNotNull(showPasswordCheckBox);
    }

    @Test
    public void testProgressDialogIsInitialized() {
        ProgressDialog progressDialog = ReflectionHelpers.getField(loginActivity, "progressDialog");
        Assert.assertNotNull(progressDialog);
    }


    @Test
    public void testOnCreateOptionsMenuShouldAddSettingsItem() {
        LoginActivity spyActivity = Mockito.spy(loginActivity);
        spyActivity.onCreateOptionsMenu(menu);
        Mockito.verify(menu).add(STRING_SETTINGS);
    }


    @Test
    public void testOnDestroyShouldCallOnDestroyPresenterMethod() {

        LoginActivity spyActivity = Mockito.spy(loginActivity);
        ReflectionHelpers.setField(spyActivity, "mLoginPresenter", presenter);
        spyActivity.onDestroy();
        Mockito.verify(presenter).onDestroy(Mockito.anyBoolean());
    }

    @Test
    public void testShowProgressShouldShowProgressDialogWhenParamIsTrue() {
        LoginActivity spyActivity = Mockito.spy(loginActivity);
        ReflectionHelpers.setField(spyActivity, "progressDialog", progressDialog);
        spyActivity.showProgress(true);
        Mockito.verify(progressDialog).show();
    }

    @Test
    public void testShowProgressShouldDismissProgressDialogWhenParamIsFalse() {
        LoginActivity spyActivity = Mockito.spy(loginActivity);
        ReflectionHelpers.setField(spyActivity, "progressDialog", progressDialog);
        spyActivity.showProgress(false);
        Mockito.verify(progressDialog).dismiss();
    }

    @Test
    public void testEnableLoginShouldCallLoginButtonSetClickableMethodWithCorrectParameter() {
        LoginActivity spyActivity = Mockito.spy(loginActivity);
        ReflectionHelpers.setField(spyActivity, "loginButton", loginButton);
        spyActivity.enableLoginButton(false);
        Mockito.verify(loginButton).setClickable(Mockito.anyBoolean());
    }

    @Test
    public void testOnEditorActionShouldCallAttemptLoginMethodFromPresenterIfActionIsEnter() {

        LoginActivity spyActivity = Mockito.spy(loginActivity);
        ReflectionHelpers.setField(spyActivity, "mLoginPresenter", presenter);

        Mockito.verify(presenter, Mockito.times(0)).attemptLogin(DUMMY_USERNAME, DUMMY_PASSWORD.toCharArray());

        EditText userNameEditText = Mockito.spy(new EditText(ApplicationProvider.getApplicationContext()));
        userNameEditText.setText(DUMMY_USERNAME);

        EditText passwordEditText = Mockito.spy(new EditText(ApplicationProvider.getApplicationContext()));
        passwordEditText.setText(DUMMY_PASSWORD);
    }

    @Test
    public void testOnClickShouldInvokeAttemptLoginPresenterMethodIfLoginButtonClicked() {
        LoginActivity spyActivity = Mockito.spy(loginActivity);
        spyActivity.onClick(new View(ApplicationProvider.getApplicationContext()));//default
        ReflectionHelpers.setField(spyActivity, "mLoginPresenter", presenter);
        EditText editTextUsername = spyActivity.findViewById(R.id.login_user_name_edit_text);
        editTextUsername.setText(DUMMY_USERNAME);
        EditText editTextPassword = spyActivity.findViewById(R.id.login_password_edit_text);
        editTextPassword.setText(DUMMY_PASSWORD);
        Button loginButton = spyActivity.findViewById(R.id.login_login_btn);
        spyActivity.onClick(loginButton);
        Mockito.verify(presenter, Mockito.times(1)).attemptLogin(DUMMY_USERNAME, DUMMY_PASSWORD.toCharArray());
    }

    @Test
    public void testResetPasswordErrorShouldInvokeSetUsernameErrorWithNull() {
        LoginActivity spyActivity = Mockito.spy(loginActivity);
        ReflectionHelpers.setField(spyActivity, "mLoginPresenter", presenter);
        EditText passwordEditText = Mockito.spy(new EditText(ApplicationProvider.getApplicationContext()));
        ReflectionHelpers.setField(spyActivity, "passwordEditText", passwordEditText);
        spyActivity.resetPaswordError();
        Mockito.verify(passwordEditText).setError(null);
    }

    @Test
    public void testSetPasswordErrorShouldShowErrorDialogWithCorrectMessage() {
        LoginActivity spyActivity = Mockito.spy(loginActivity);
        ReflectionHelpers.setField(spyActivity, "mLoginPresenter", presenter);
        EditText passwordEditText = Mockito.spy(new EditText(ApplicationProvider.getApplicationContext()));
        ReflectionHelpers.setField(spyActivity, "passwordEditText", passwordEditText);
        Mockito.doNothing().when(spyActivity)
                .showErrorDialog(ApplicationProvider.getApplicationContext().getString(R.string.unauthorized));
        spyActivity.setUsernameError(R.string.unauthorized);
        Mockito.verify(spyActivity).showErrorDialog(ApplicationProvider.getApplicationContext().getString(R.string.unauthorized));
    }

    @Test
    public void testSetUsernameErrorShouldShowErrorDialogWithCorrectMessage() {
        LoginActivity spyActivity = Mockito.spy(loginActivity);
        ReflectionHelpers.setField(spyActivity, "mLoginPresenter", presenter);
        EditText userNameEditText = Mockito.spy(new EditText(ApplicationProvider.getApplicationContext()));
        ReflectionHelpers.setField(spyActivity, "userNameEditText", userNameEditText);
        Mockito.doNothing().when(spyActivity)
                .showErrorDialog(ApplicationProvider.getApplicationContext().getString(R.string.unauthorized));
        spyActivity.setPasswordError(R.string.unauthorized);
        Mockito.verify(spyActivity).showErrorDialog(ApplicationProvider.getApplicationContext().getString(R.string.unauthorized));
    }

    @Test
    public void testResetUsernameErrorShouldInvokeSetUsernameErrorWithNull() {
        LoginActivity spyActivity = Mockito.spy(loginActivity);
        ReflectionHelpers.setField(spyActivity, "mLoginPresenter", presenter);
        EditText userNameEditText = Mockito.spy(new EditText(ApplicationProvider.getApplicationContext()));
        ReflectionHelpers.setField(spyActivity, "userNameEditText", userNameEditText);
        spyActivity.resetUsernameError();
        Mockito.verify(userNameEditText).setError(null);
    }

    @Test
    public void testGetActivityContextReturnsCorrectInstance() {
        LoginActivity spyActivity = Mockito.spy(loginActivity);
        Assert.assertEquals(spyActivity, spyActivity.getActivityContext());
    }

    @Override
    protected Activity getActivity() {
        return loginActivity;
    }

    @Override
    protected ActivityController<LoginActivity> getActivityController() {
        return controller;
    }
}