package org.smartregister.uniceftunisia.repository;

import android.database.Cursor;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.repository.BaseRepository;
import org.smartregister.uniceftunisia.domain.Hia2Indicator;
import org.smartregister.uniceftunisia.util.DbConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-07-11
 */

public class HIA2IndicatorsRepository extends BaseRepository {

    private static final String[] HIA2_TABLE_COLUMNS = {DbConstants.Table.Hia2IndicatorsRepository.ID, DbConstants.Table.Hia2IndicatorsRepository.INDICATOR_CODE, DbConstants.Table.Hia2IndicatorsRepository.DESCRIPTION};

    public HashMap<String, Hia2Indicator> findAll() {
        HashMap<String, Hia2Indicator> response = new HashMap<>();
        Cursor cursor = null;

        try {
            cursor = getReadableDatabase().query(DbConstants.Table.Hia2IndicatorsRepository.TABLE_NAME, HIA2_TABLE_COLUMNS, null, null, null, null, null, null);
            List<Hia2Indicator> hia2Indicators = readAllDataElements(cursor);
            for (Hia2Indicator curIndicator : hia2Indicators) {
                response.put(curIndicator.getIndicatorCode(), curIndicator);
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return response;
    }

    public Hia2Indicator findByIndicatorCode(String indicatorCode) {
        Cursor cursor = null;
        Hia2Indicator hia2Indicator = null;

        try {
            cursor = getReadableDatabase().query(DbConstants.Table.Hia2IndicatorsRepository.TABLE_NAME, HIA2_TABLE_COLUMNS, DbConstants.Table.Hia2IndicatorsRepository.INDICATOR_CODE + " = ? COLLATE NOCASE ", new String[]{indicatorCode}, null, null, null, null);
            List<Hia2Indicator> hia2Indicators = readAllDataElements(cursor);
            if (hia2Indicators.size() == 1) {
                hia2Indicator = hia2Indicators.get(0);
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return hia2Indicator;
    }

    public Hia2Indicator findById(long id) {
        Cursor cursor = null;
        Hia2Indicator hia2Indicator = null;

        try {
            cursor = getReadableDatabase().query(DbConstants.Table.Hia2IndicatorsRepository.TABLE_NAME, HIA2_TABLE_COLUMNS, DbConstants.Table.Hia2IndicatorsRepository.ID + " = ?", new String[]{String.valueOf(id)}, null, null, null, null);
            List<Hia2Indicator> hia2Indicators = readAllDataElements(cursor);
            if (hia2Indicators.size() == 1) {
                hia2Indicator = hia2Indicators.get(0);
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return hia2Indicator;
    }

    private List<Hia2Indicator> readAllDataElements(Cursor cursor) {
        List<Hia2Indicator> hia2Indicators = new ArrayList<>();
        try {
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    Hia2Indicator hia2Indicator = new Hia2Indicator();
                    hia2Indicator.setId(cursor.getLong(cursor.getColumnIndex(DbConstants.Table.Hia2IndicatorsRepository.ID)));
                    hia2Indicator.setDescription(cursor.getString(cursor.getColumnIndex(DbConstants.Table.Hia2IndicatorsRepository.DESCRIPTION)));
                    hia2Indicator.setIndicatorCode(cursor.getString(cursor.getColumnIndex(DbConstants.Table.Hia2IndicatorsRepository.INDICATOR_CODE)));
                    hia2Indicators.add(hia2Indicator);

                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if(cursor != null) {
                cursor.close();
            }
        }

        return hia2Indicators;

    }

    /**
     * order by id asc so that the indicators are ordered by category and indicator id
     *
     * @return
     */
    public List<Hia2Indicator> fetchAll() {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.query(DbConstants.Table.Hia2IndicatorsRepository.TABLE_NAME, HIA2_TABLE_COLUMNS, null, null, null, null, DbConstants.Table.Hia2IndicatorsRepository.ID + " asc ");
        return readAllDataElements(cursor);
    }
}
