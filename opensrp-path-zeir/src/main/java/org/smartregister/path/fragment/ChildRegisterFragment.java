package org.smartregister.path.fragment;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.core.content.ContextCompat;
import androidx.loader.content.Loader;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.child.domain.RegisterClickables;
import org.smartregister.child.fragment.BaseChildRegisterFragment;
import org.smartregister.child.util.AppExecutors;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.Utils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.path.R;
import org.smartregister.path.activity.ChildImmunizationActivity;
import org.smartregister.path.activity.ChildRegisterActivity;
import org.smartregister.path.model.ChildRegisterFragmentModel;
import org.smartregister.path.presenter.ChildRegisterFragmentPresenter;
import org.smartregister.path.util.DBQueryHelper;
import org.smartregister.view.activity.BaseRegisterActivity;

import java.util.List;

import timber.log.Timber;

public class ChildRegisterFragment extends BaseChildRegisterFragment {

    private boolean registerQueryFinished = false;

    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }

        String viewConfigurationIdentifier = ((BaseRegisterActivity) getActivity()).getViewIdentifiers().get(0);
        presenter = new ChildRegisterFragmentPresenter(this, new ChildRegisterFragmentModel(), viewConfigurationIdentifier);
    }

    @Override
    public String getMainCondition() {
        return presenter().getMainCondition();
    }

    @Override
    protected String getDefaultSortQuery() {
        return presenter().getDefaultSortQuery();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onViewClicked(View view) {
        super.onViewClicked(view);
        RegisterClickables registerClickables = new RegisterClickables();
        if (view.getTag(R.id.record_action) != null) {
            registerClickables.setRecordWeight(
                    Constants.RECORD_ACTION.GROWTH.equals(view.getTag(R.id.record_action)));
            registerClickables.setRecordAll(
                    Constants.RECORD_ACTION.VACCINATION.equals(view.getTag(R.id.record_action)));
            registerClickables.setNextAppointmentDate(view.getTag(R.id.next_appointment_date) != null ? String
                    .valueOf(view.getTag(R.id.next_appointment_date)) : "");
        }

        CommonPersonObjectClient client = null;
        if (view.getTag() != null && view.getTag() instanceof CommonPersonObjectClient) {
            client = (CommonPersonObjectClient) view.getTag();
        }

        switch (view.getId()) {
            case R.id.child_profile_info_layout:
                ChildImmunizationActivity.launchActivity(getActivity(), client, registerClickables);
                break;
            case R.id.record_growth:
                registerClickables.setRecordWeight(true);
                ChildImmunizationActivity.launchActivity(getActivity(), client, registerClickables);
                break;
            case R.id.record_vaccination:
                registerClickables.setRecordAll(true);
                ChildImmunizationActivity.launchActivity(getActivity(), client, registerClickables);
                break;
            case R.id.filter_selection:
                toggleFilterSelection();
                break;
            case R.id.global_search:
                ((ChildRegisterActivity) getActivity()).startAdvancedSearch();
                break;
            case R.id.scan_qr_code:
                ((ChildRegisterActivity) getActivity()).startQrCodeScanner();
                break;
            case R.id.back_button:
                ((ChildRegisterActivity) getActivity()).openDrawer();
                break;
            default:
                break;
        }

    }

    @Override
    protected String filterSelectionCondition(boolean urgentOnly) {
        return DBQueryHelper.getFilterSelectionCondition(urgentOnly);
    }

    @Override
    public void setupViews(View view) {
        super.setupViews(view);
        View globalSearchButton = view.findViewById(org.smartregister.child.R.id.global_search);
        View registerClientButton = view.findViewById(org.smartregister.child.R.id.register_client);
        if (globalSearchButton != null && registerClientButton != null) {
            globalSearchButton.setVisibility(View.INVISIBLE);
            registerClientButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        onViewClicked(view);
    }

    @Override
    public void setupSearchView(View view) {
        super.setupSearchView(view);
        ((View) searchView.getParent().getParent()).setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.toolbar_background));
        searchView.setHint(requireContext().getString(R.string.search_hint));
    }

    @Override
    public void countExecute() {
        AppExecutors executors = new AppExecutors();
        executors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String sql;
                    if (filters != null && !filters.isEmpty()) {
                        sql = Utils.metadata().getRegisterQueryProvider().getCountExecuteQuery(mainCondition, filters);
                    } else {
                        sql = "SELECT count(id) FROM ec_child_details WHERE (date_removed IS NULL AND ec_child_details.inactive is NOT true AND is_closed IS NOT '1')";
                    }

                    Timber.i(sql);
                    int totalCount = commonRepository().countSearchIds(sql);

                    executors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            clientAdapter.setTotalcount(totalCount);
                            Timber.i("Total Register Count %d", clientAdapter.getTotalcount());
                            clientAdapter.setCurrentlimit(20);
                            clientAdapter.setCurrentoffset(0);

                        }
                    });
                } catch (Exception e) {
                    Timber.e(e);
                }
            }
        });
    }

    @Override
    protected String filterAndSortQuery() {
        SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder(mainSelect);
        String query = "";
        try {
            if (isValidFilterForFts(commonRepository())) {
                String sql;
                if (filters != null && !filters.isEmpty()) {
                    sql = Utils.metadata().getRegisterQueryProvider().getObjectIdsQuery(this.mainCondition, this.filters) + (StringUtils.isBlank(this.getDefaultSortQuery()) ? "" : " order by " + this.getDefaultSortQuery());
                } else {
                    sql = "SELECT ec_child_details.id FROM ec_child_details INNER JOIN ec_client ON ec_child_details.id = ec_client.id WHERE (ec_child_details.date_removed IS NULL AND ec_child_details.inactive is NOT true AND ec_child_details.is_closed IS NOT '1') order by ec_client.last_interacted_with DESC ";
                }

                sql = sqb.addlimitandOffset(sql, clientAdapter.getCurrentlimit(), clientAdapter.getCurrentoffset());

                List<String> ids = commonRepository().findSearchIds(sql);
                query = Utils.metadata().getRegisterQueryProvider().mainRegisterQuery() +
                        " WHERE _id IN (%s) " + (StringUtils.isBlank(getDefaultSortQuery()) ? "" : " order by " + getDefaultSortQuery());

                String joinedIds = "'" + StringUtils.join(ids, "','") + "'";
                return query.replace("%s", joinedIds);
            } else {
                if (!TextUtils.isEmpty(filters) && !TextUtils.isEmpty(Sortqueries)) {
                    sqb.addCondition(filters);
                    query = sqb.orderbyCondition(Sortqueries);
                    query = sqb.Endquery(sqb.addlimitandOffset(query
                            , clientAdapter.getCurrentlimit()
                            , clientAdapter.getCurrentoffset()));
                }
                return query;
            }

        } catch (Exception e) {
            Timber.e(e);
        }

        return query;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        super.onLoadFinished(loader, cursor);

        /*if (!registerQueryFinished && getOverDueCount() == 0) {
            // Get notified when all the recycler views have been rendered and the previous cursor is done accessing the DB
            clientsView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    clientsView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    registerQueryFinished = true;

                    runVaccineOverdueQuery();
                }
            });
        }*/
    }

    /**
     * Runs the query to count the clients with overdue/urgent vaccines.
     * <p>
     * This query is expensive and should be avoided as it almost blocks any access from the DB. The query takes 20-50 seconds
     */
    private void runVaccineOverdueQuery() {
        AppExecutors executors = new AppExecutors();
        executors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Timber.e("Started running the overdue count query");

                String sqlOverdueCount = Utils.metadata().getRegisterQueryProvider()
                        .getCountExecuteQuery(filterSelectionCondition(true), "");
                int overDueCount = commonRepository().countSearchIds(sqlOverdueCount);
                setOverDueCount(overDueCount);

                Timber.e("Gotten the overdue count: " + overDueCount);

                executors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        updateDueOverdueCountText();
                        registerQueryFinished = false;
                    }
                });
            }
        });
    }
}
