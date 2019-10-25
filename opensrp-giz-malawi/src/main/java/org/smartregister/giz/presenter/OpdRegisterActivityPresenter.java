package org.smartregister.giz.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.domain.FetchStatus;
import org.smartregister.giz.R;
import org.smartregister.giz.interactor.OpdRegisterActivityInteractor;
import org.smartregister.opd.contract.OpdRegisterActivityContract;
import org.smartregister.opd.pojos.OpdEventClient;
import org.smartregister.opd.pojos.RegisterParams;
import org.smartregister.opd.presenter.BaseOpdRegisterActivityPresenter;
import org.smartregister.opd.utils.OpdJsonFormUtils;
import org.smartregister.opd.utils.OpdUtils;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-20
 */

public class OpdRegisterActivityPresenter extends BaseOpdRegisterActivityPresenter implements OpdRegisterActivityContract.Presenter, OpdRegisterActivityContract.InteractorCallBack {

    private WeakReference<OpdRegisterActivityContract.View> viewReference;
    private OpdRegisterActivityContract.Interactor interactor;
    private OpdRegisterActivityContract.Model model;

    public OpdRegisterActivityPresenter(OpdRegisterActivityContract.View view, OpdRegisterActivityContract.Model model) {
        super(view,model);
        viewReference = new WeakReference<>(view);
        interactor = new OpdRegisterActivityInteractor();
        this.model = model;
    }

    @Override
    public void saveForm(@NonNull String jsonString,@NonNull RegisterParams registerParams) {
        try {
            if (registerParams.getFormTag() == null) {
                registerParams.setFormTag(OpdJsonFormUtils.formTag(OpdUtils.getAllSharedPreferences()));
            }
            List<OpdEventClient> opdEventClientList = model.processRegistration(jsonString, registerParams.getFormTag());
            if (opdEventClientList == null || opdEventClientList.isEmpty()) {
                return;
            }
            registerParams.setEditMode(false);
            interactor.saveRegistration(opdEventClientList, jsonString, registerParams, this);

        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    public void onNoUniqueId() {
        if (getView() != null) {
            getView().displayShortToast(R.string.no_unique_id);
        }
    }

    @Override
    public void onRegistrationSaved(boolean isEdit) {
        if (getView() != null) {
            getView().refreshList(FetchStatus.fetched);
            getView().hideProgressDialog();
        }
    }

    @Nullable
    private OpdRegisterActivityContract.View getView() {
        if (viewReference != null) {
            return viewReference.get();
        } else {
            return null;
        }
    }

    @Override
    public void saveLanguage(String language) {
        model.saveLanguage(language);

        if (getView() != null) {
            getView().displayToast(String.format(getView().getContext().getString(R.string.language_x_selected), language));
        }
    }

    @Override
    public void registerViewConfigurations(List<String> viewIdentifiers) {
        model.registerViewConfigurations(viewIdentifiers);
    }

    @Override
    public void unregisterViewConfiguration(List<String> viewIdentifiers) {
        model.unregisterViewConfiguration(viewIdentifiers);
    }

    public void setModel(OpdRegisterActivityContract.Model model) {
        this.model = model;
    }

}
