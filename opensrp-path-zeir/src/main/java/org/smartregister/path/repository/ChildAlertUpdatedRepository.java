package org.smartregister.path.repository;

import android.content.ContentValues;

import androidx.annotation.NonNull;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.path.util.AppConstants;
import org.smartregister.repository.BaseRepository;

import java.util.Date;

public class ChildAlertUpdatedRepository extends BaseRepository {

    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + AppConstants.TableNameConstants.CHILD_UPDATED_ALERTS + "("
            + AppConstants.Columns.RegisterType.BASE_ENTITY_ID + " VARCHAR NOT NULL,"
            + AppConstants.Columns.RegisterType.DATE_CREATED + " INTEGER NOT NULL, "
            + "UNIQUE(" + AppConstants.Columns.RegisterType.BASE_ENTITY_ID + ") ON CONFLICT REPLACE)";

    private static final String INDEX_BASE_ENTITY_ID = "CREATE INDEX " + AppConstants.TableNameConstants.CHILD_UPDATED_ALERTS
            + "_" + AppConstants.Columns.RegisterType.BASE_ENTITY_ID + "_index ON " + AppConstants.TableNameConstants.CHILD_UPDATED_ALERTS +
            "(" + AppConstants.Columns.RegisterType.BASE_ENTITY_ID + " COLLATE NOCASE);";

    public static void createTable(@NonNull SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
        database.execSQL(INDEX_BASE_ENTITY_ID);
    }


    public boolean findOne(String baseEntityId) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(AppConstants.TableNameConstants.CHILD_UPDATED_ALERTS, new String[]{AppConstants.Columns.RegisterType.BASE_ENTITY_ID},
                AppConstants.Columns.RegisterType.BASE_ENTITY_ID + "=?",
                new String[]{baseEntityId}, null, null, null, "1");
        return cursor != null && cursor.getCount() > 0;

    }

    public boolean saveOrUpdate(@NonNull String baseEntityId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(AppConstants.Columns.RegisterType.BASE_ENTITY_ID, baseEntityId);
        contentValues.put(AppConstants.Columns.RegisterType.DATE_CREATED, new Date().getTime());
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        long rows = sqLiteDatabase.insert(AppConstants.TableNameConstants.CHILD_UPDATED_ALERTS, null, contentValues);
        return rows != -1;
    }

    public boolean deleteAll() {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        int rows = sqLiteDatabase.delete(AppConstants.TableNameConstants.CHILD_UPDATED_ALERTS
                , null
                , null);
        return rows > 0;
    }
}
