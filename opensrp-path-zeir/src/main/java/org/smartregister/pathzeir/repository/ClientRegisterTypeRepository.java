package org.smartregister.pathzeir.repository;

import android.content.ContentValues;

import androidx.annotation.NonNull;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.pathzeir.util.AppConstants;
import org.smartregister.repository.BaseRepository;

import java.util.Date;

public class ClientRegisterTypeRepository extends BaseRepository implements ClientRegisterTypeDao {

    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + AppConstants.TABLE_NAME.REGISTER_TYPE + "("
            + AppConstants.Columns.RegisterType.BASE_ENTITY_ID + " VARCHAR NOT NULL,"
            + AppConstants.Columns.RegisterType.REGISTER_TYPE + " VARCHAR NOT NULL, "
            + AppConstants.Columns.RegisterType.DATE_CREATED + " INTEGER NOT NULL, "
            + AppConstants.Columns.RegisterType.DATE_REMOVED + " INTEGER NULL, "
            + "UNIQUE(" + AppConstants.Columns.RegisterType.BASE_ENTITY_ID + ", " + AppConstants.Columns.RegisterType.REGISTER_TYPE + ") ON CONFLICT REPLACE)";

    private static final String INDEX_BASE_ENTITY_ID = "CREATE INDEX " + AppConstants.TABLE_NAME.REGISTER_TYPE
            + "_" + AppConstants.Columns.RegisterType.BASE_ENTITY_ID + "_index ON " + AppConstants.TABLE_NAME.REGISTER_TYPE +
            "(" + AppConstants.Columns.RegisterType.BASE_ENTITY_ID + " COLLATE NOCASE);";

    public static void createTable(@NonNull SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
        database.execSQL(INDEX_BASE_ENTITY_ID);
    }

    @Override
    public boolean removeAll(String baseEntityId) {
        return false;
    }

    @Override
    public boolean add(String registerType, String baseEntityId) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(AppConstants.Columns.RegisterType.BASE_ENTITY_ID, baseEntityId);
        contentValues.put(AppConstants.Columns.RegisterType.REGISTER_TYPE, registerType);
        contentValues.put(AppConstants.Columns.RegisterType.DATE_CREATED, new Date().getTime());
        long result = database.insert(AppConstants.TABLE_NAME.REGISTER_TYPE, null, contentValues);
        return result != -1;
    }
}
