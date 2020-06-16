package org.smartregister.uniceftunisia.interactor;

import android.database.Cursor;

import org.smartregister.child.util.Constants;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.uniceftunisia.application.UnicefTunisiaApplication;
import org.smartregister.uniceftunisia.contract.NavigationContract;
import org.smartregister.uniceftunisia.util.AppExecutors;
import org.smartregister.uniceftunisia.util.AppConstants;

import java.text.MessageFormat;
import java.util.Date;

import timber.log.Timber;

public class NavigationInteractor implements NavigationContract.Interactor {

    private static NavigationInteractor instance;
    private AppExecutors appExecutors = new AppExecutors();

    public static NavigationInteractor getInstance() {
        if (instance == null)
            instance = new NavigationInteractor();

        return instance;
    }

    @Override
    public void getRegisterCount(final String registerType, final NavigationContract.InteractorCallback<Integer> callback) {
        if (callback != null) {
            appExecutors.diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        final Integer finalCount = getCount(registerType);
                        appExecutors.mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                callback.onResult(finalCount);
                            }
                        });
                    } catch (final Exception e) {
                        appExecutors.mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                callback.onError(e);
                            }
                        });
                    }
                }
            });

        }
    }

    private int getCount(String tempRegisterType) {
        String registerType = tempRegisterType;
        int count = 0;
        Cursor cursor = null;
        if (AppConstants.RegisterType.OPD.equals(registerType)){
            registerType = "'"+ AppConstants.RegisterType.OPD+"'," + "'"+ AppConstants.RegisterType.ANC+"'," + "'"+ AppConstants.RegisterType.CHILD+"'";
        } else {
            registerType = "'"+registerType+"'";

        }

        String mainCondition = String.format(" where %s is null AND register_type IN (%s) ", AppConstants.TABLE_NAME.ALL_CLIENTS+"."+ AppConstants.KEY.DATE_REMOVED, registerType);

        if (registerType.contains(AppConstants.RegisterType.CHILD)) {
            mainCondition += " AND ( " + Constants.KEY.DOD + " is NULL OR " + Constants.KEY.DOD + " = '' ) ";
        }

        try {
            SmartRegisterQueryBuilder smartRegisterQueryBuilder = new SmartRegisterQueryBuilder();
            String query = MessageFormat.format("select count(*) from {0} inner join client_register_type on ec_client.id=client_register_type.base_entity_id {1}", AppConstants.TABLE_NAME.ALL_CLIENTS, mainCondition);
            query = smartRegisterQueryBuilder.Endquery(query);
            Timber.i("2%s", query);
            cursor = commonRepository(AppConstants.TABLE_NAME.ALL_CLIENTS).rawCustomQueryForAdapter(query);
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return count;
    }

    private CommonRepository commonRepository(String tableName) {
        return UnicefTunisiaApplication.getInstance().getContext().commonrepository(tableName);
    }

    @Override
    public Date sync() {
        Date syncDate = null;
        try {
            syncDate = new Date(getLastCheckTimeStamp());
        } catch (Exception e) {
            Timber.e(e);
        }

        return syncDate;
    }

    private Long getLastCheckTimeStamp() {
        return UnicefTunisiaApplication.getInstance().getEcSyncHelper().getLastCheckTimeStamp();
    }
}
