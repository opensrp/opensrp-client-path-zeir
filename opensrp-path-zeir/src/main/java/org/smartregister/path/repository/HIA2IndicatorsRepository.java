package org.smartregister.path.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;

import androidx.annotation.Nullable;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.path.model.Hia2Indicator;
import org.smartregister.repository.BaseRepository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-07-11
 */

public class HIA2IndicatorsRepository extends BaseRepository {

    public static final String INDICATORS_CSV_FILE = "DataDictionaryReporting.csv";
    private static final String HIA2_INDICATORS_SQL = "CREATE TABLE hia2_indicators (_id INTEGER NOT NULL,provider_id VARCHAR,indicator_code VARCHAR NOT NULL,label VARCHAR,dhis_id VARCHAR ,category_option_combo VARCHAR , description VARCHAR,category VARCHAR ,grouping VARCHAR, created_at DATETIME NULL,updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP)";
    public static final String TABLE_NAME = "hia2_indicators";
    protected static final String ID_COLUMN = "_id";
    private static final String PROVIDER_ID = "provider_id";
    private static final String INDICATOR_CODE = "indicator_code";
    private static final String LABEL = "label";
    private static final String GROUPING = "grouping";
    private static final String DESCRIPTION = "description";
    private static final String DHIS_ID = "dhis_id";
    private static final String CATEGORY_OPTION_COMBO = "category_option_combo";

    private static final String CATEGORY = "category";

    private static final String CREATED_AT_COLUMN = "created_at";
    private static final String UPDATED_AT_COLUMN = "updated_at";
    protected static final String[] HIA2_TABLE_COLUMNS = {ID_COLUMN, PROVIDER_ID, INDICATOR_CODE, LABEL, DHIS_ID, CATEGORY_OPTION_COMBO, DESCRIPTION, CATEGORY, GROUPING, CREATED_AT_COLUMN, UPDATED_AT_COLUMN};
    public static final Map<Integer, String> CSV_COLUMN_MAPPING;

    private static final String PROVIDER_ID_INDEX = "CREATE INDEX " + TABLE_NAME + "_" + PROVIDER_ID + "_index ON " + TABLE_NAME + "(" + PROVIDER_ID + " COLLATE NOCASE);";
    private static final String KEY_INDEX = "CREATE INDEX " + TABLE_NAME + "_" + INDICATOR_CODE + "_index ON " + TABLE_NAME + "(" + INDICATOR_CODE + " COLLATE NOCASE);";
    private static final String VALUE_INDEX = "CREATE INDEX " + TABLE_NAME + "_" + UPDATED_AT_COLUMN + "_index ON " + TABLE_NAME + "(" + UPDATED_AT_COLUMN + ");";
    private static final String DHIS_ID_INDEX = "CREATE INDEX " + TABLE_NAME + "_" + DHIS_ID + "_index ON " + TABLE_NAME + "(" + DHIS_ID + ");";
    private static final String LABEL_INDEX = "CREATE INDEX " + TABLE_NAME + "_" + LABEL + "_index ON " + TABLE_NAME + "(" + LABEL + ");";
    private static final String DESCRIPTION_INDEX = "CREATE INDEX " + TABLE_NAME + "_" + DESCRIPTION + "_index ON " + TABLE_NAME + "(" + DESCRIPTION + ");";
    private static final String CATEGORY_INDEX = "CREATE INDEX " + TABLE_NAME + "_" + CATEGORY + "_index ON " + TABLE_NAME + "(" + CATEGORY + ");";

    static {
        CSV_COLUMN_MAPPING = new HashMap<>();
        CSV_COLUMN_MAPPING.put(0, HIA2IndicatorsRepository.ID_COLUMN);
        CSV_COLUMN_MAPPING.put(1, HIA2IndicatorsRepository.INDICATOR_CODE);
        CSV_COLUMN_MAPPING.put(2, HIA2IndicatorsRepository.LABEL);
        CSV_COLUMN_MAPPING.put(3, HIA2IndicatorsRepository.DHIS_ID);
        CSV_COLUMN_MAPPING.put(4, HIA2IndicatorsRepository.CATEGORY_OPTION_COMBO);
        CSV_COLUMN_MAPPING.put(5, HIA2IndicatorsRepository.DESCRIPTION);
        CSV_COLUMN_MAPPING.put(6, HIA2IndicatorsRepository.GROUPING);
        CSV_COLUMN_MAPPING.put(999, HIA2IndicatorsRepository.CATEGORY); //999 means nothing really, just to hold the column name for categories since category is a row in the hia2 csv
    }

    protected static void createTable(SQLiteDatabase database) {
        database.execSQL(HIA2_INDICATORS_SQL);
        database.execSQL(PROVIDER_ID_INDEX);
        database.execSQL(KEY_INDEX);
        database.execSQL(VALUE_INDEX);
        database.execSQL(DHIS_ID_INDEX);
        database.execSQL(LABEL_INDEX);
        database.execSQL(DESCRIPTION_INDEX);
        database.execSQL(CATEGORY_INDEX);
    }

    public HashMap<String, Hia2Indicator> findAllByGrouping(String grouping) {
        HashMap<String, Hia2Indicator> response = new HashMap<>();

        try (Cursor cursor = getReadableDatabase()
                .query(TABLE_NAME, HIA2_TABLE_COLUMNS, "grouping = ?", new String[]{grouping}, null, null, null, null)) {
            List<Hia2Indicator> hia2Indicators = readAllDataElements(cursor);
            for (Hia2Indicator curIndicator : hia2Indicators) {
                response.put(curIndicator.getIndicatorCode(), curIndicator);
            }
        } catch (Exception e) {
            Timber.e(e);
        }

        return response;
    }

    private List<Hia2Indicator> readAllDataElements(Cursor cursor) {
        List<Hia2Indicator> hia2Indicators = new ArrayList<>();
        try {
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    Hia2Indicator hia2Indicator = new Hia2Indicator();
                    hia2Indicator.setId(cursor.getLong(cursor.getColumnIndex(ID_COLUMN)));
                    hia2Indicator.setLabel(cursor.getString(cursor.getColumnIndex(LABEL)));
                    hia2Indicator.setDescription(cursor.getString(cursor.getColumnIndex(DESCRIPTION)));
                    hia2Indicator.setDhisId(cursor.getString(cursor.getColumnIndex(DHIS_ID)));
                    hia2Indicator.setCategoryOptionCombo(cursor.getString(cursor.getColumnIndex(CATEGORY_OPTION_COMBO)));
                    hia2Indicator.setIndicatorCode(cursor.getString(cursor.getColumnIndex(INDICATOR_CODE)));
                    hia2Indicator.setCategory(cursor.getString(cursor.getColumnIndex(CATEGORY)));
                    hia2Indicator.setGrouping(cursor.getString(cursor.getColumnIndex(GROUPING)));
                    hia2Indicator.setCreatedAt(new Date(cursor.getLong(cursor.getColumnIndex(CREATED_AT_COLUMN))));
                    hia2Indicator.setUpdatedAt(new Date(Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(UPDATED_AT_COLUMN))).getTime()));
                    hia2Indicators.add(hia2Indicator);
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return hia2Indicators;
    }

    public void save(@Nullable SQLiteDatabase database, @Nullable List<Map<String, String>> hia2Indicators) {
        if (database != null && hia2Indicators != null && !hia2Indicators.isEmpty()) {
            try {
                database.beginTransaction();
                for (Map<String, String> hia2Indicator : hia2Indicators) {
                    ContentValues cv = new ContentValues();

                    for (String column : hia2Indicator.keySet()) {

                        String value = hia2Indicator.get(column);
                        cv.put(column, value);

                    }
                    Long id = checkIfExists(database, cv.getAsString(INDICATOR_CODE));

                    if (id != null) {
                        database.update(TABLE_NAME, cv, ID_COLUMN + " = ?", new String[]{id.toString()});
                    } else {
                        database.insert(TABLE_NAME, null, cv);
                    }
                }
                database.setTransactionSuccessful();
            } catch (SQLException e) {
                Timber.e(e);
            } finally {
                database.endTransaction();
            }
        }
    }


    private Long checkIfExists(SQLiteDatabase db, String indicatorCode) {
        Long exists = null;
        String query = "SELECT " + ID_COLUMN + " FROM " + TABLE_NAME + " WHERE " + INDICATOR_CODE + " = '" + indicatorCode + "' COLLATE NOCASE ";

        try (Cursor mCursor = db.rawQuery(query, null)) {
            if (mCursor != null && mCursor.moveToFirst()) {
                exists = mCursor.getLong(0);
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return exists;
    }
}
