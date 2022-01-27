package org.smartregister.path.repository;

import android.content.ContentValues;

import androidx.annotation.Nullable;

import org.json.JSONObject;
import org.smartregister.domain.db.Column;
import org.smartregister.domain.db.ColumnAttribute;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.Hia2ReportRepository;

import java.util.Date;

import timber.log.Timber;

public class ZeirHia2ReportRepository extends Hia2ReportRepository {

    public enum ReportColumn implements Column {
        creator(ColumnAttribute.Type.text, false, false),
        dateCreated(ColumnAttribute.Type.date, false, true),
        editor(ColumnAttribute.Type.text, false, false),
        dateEdited(ColumnAttribute.Type.date, false, false),
        voided(ColumnAttribute.Type.bool, false, false),
        dateVoided(ColumnAttribute.Type.date, false, false),
        voider(ColumnAttribute.Type.text, false, false),
        voidReason(ColumnAttribute.Type.text, false, false),

        reportId(ColumnAttribute.Type.text, true, true),
        syncStatus(ColumnAttribute.Type.text, false, true),
        validationStatus(ColumnAttribute.Type.text, false, true),
        json(ColumnAttribute.Type.text, false, false),
        locationId(ColumnAttribute.Type.text, false, false),
        childLocationId(ColumnAttribute.Type.text, false, false),
        reportDate(ColumnAttribute.Type.date, false, true),
        reportType(ColumnAttribute.Type.text, false, true),
        formSubmissionId(ColumnAttribute.Type.text, false, false),
        providerId(ColumnAttribute.Type.text, false, false),
        entityType(ColumnAttribute.Type.text, false, false),
        version(ColumnAttribute.Type.text, false, false),
        updatedAt(ColumnAttribute.Type.date, false, true),
        serverVersion(ColumnAttribute.Type.longnum, false, true),
        grouping(ColumnAttribute.Type.text, false, false);

        private ColumnAttribute column;

        ReportColumn(ColumnAttribute.Type type, boolean pk, boolean index) {
            this.column = new ColumnAttribute(type, pk, index);
        }

        public ColumnAttribute column() {
            return column;
        }
    }

    @Override
    public void addReport(@Nullable JSONObject jsonObject) {
        if (jsonObject != null) {
            try {
                ContentValues values = new ContentValues();
                values.put(ReportColumn.json.name(), jsonObject.toString());
                values.put(ReportColumn.reportType.name(),
                        jsonObject.has(ReportColumn.reportType.name()) ? jsonObject.getString(
                                ReportColumn.reportType.name()) : "");
                values.put(ReportColumn.updatedAt.name(), dateFormat.format(new Date()));
                values.put(ReportColumn.reportDate.name(),
                        jsonObject.optString(ReportColumn.reportDate.name()
                        ));
                values.put(ReportColumn.providerId.name(),
                        jsonObject.optString(ReportColumn.providerId.name()
                        ));
                values.put(ReportColumn.dateCreated.name(),
                        jsonObject.optString(ReportColumn.dateCreated.name()
                        ));
                values.put(ReportColumn.grouping.name(),
                        jsonObject.optString(ReportColumn.grouping.name()
                        ));

                values.put(ReportColumn.syncStatus.name(), BaseRepository.TYPE_Unsynced);
                //update existing event if eventid present
                if (jsonObject.has(ReportColumn.formSubmissionId.name())
                        && jsonObject.getString(ReportColumn.formSubmissionId.name()) != null) {
                    //sanity check
                    if (checkIfExistsByFormSubmissionId(Table.hia2_report,
                            jsonObject.getString(ReportColumn
                                    .formSubmissionId
                                    .name()))) {
                        getWritableDatabase().update(Table.hia2_report.name(),
                                values,
                                ReportColumn.formSubmissionId.name() + "=?",
                                new String[]{jsonObject.getString(
                                        ReportColumn.formSubmissionId.name())});
                    } else {
                        //that odd case
                        values.put(ReportColumn.formSubmissionId.name(),
                                jsonObject.getString(ReportColumn.formSubmissionId.name()));

                        getWritableDatabase().insert(Table.hia2_report.name(), null, values);

                    }
                } else {
                    getWritableDatabase().insert(Table.hia2_report.name(), null, values);
                }

            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }
}
