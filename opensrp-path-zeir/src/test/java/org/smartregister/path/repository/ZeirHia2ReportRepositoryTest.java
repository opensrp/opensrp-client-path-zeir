package org.smartregister.path.repository;

import android.content.ContentValues;

import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.path.BaseRobolectricTest;
import org.smartregister.repository.Hia2ReportRepository;

import java.util.Date;

public class ZeirHia2ReportRepositoryTest extends BaseRobolectricTest {

    @Mock
    private SQLiteDatabase sqLiteDatabase;

    private ZeirHia2ReportRepository zeirHia2ReportRepository;


    @Before
    public void setUp() {
        zeirHia2ReportRepository = new ZeirHia2ReportRepository();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAddReportIfReportDoesNotExistShouldCallInsert() throws JSONException {
        ZeirHia2ReportRepository zeirHia2ReportRepositorySpy = Mockito.spy(zeirHia2ReportRepository);
        JSONObject reportJsonObject = new JSONObject();
        reportJsonObject.put(ZeirHia2ReportRepository.ReportColumn.grouping.name(), "chn");
        reportJsonObject.put(ZeirHia2ReportRepository.ReportColumn.reportType.name(), new JSONObject().toString());
        reportJsonObject.put(ZeirHia2ReportRepository.ReportColumn.updatedAt.name(), new Date());
        reportJsonObject.put(ZeirHia2ReportRepository.ReportColumn.reportType.name(), "Report A");
        reportJsonObject.put(ZeirHia2ReportRepository.ReportColumn.formSubmissionId.name(), "6745");

        Mockito.doReturn(sqLiteDatabase).when(zeirHia2ReportRepositorySpy).getWritableDatabase();
        Mockito.doReturn(false).when(zeirHia2ReportRepositorySpy)
                .checkIfExistsByFormSubmissionId(
                        Mockito.eq(Hia2ReportRepository.Table.hia2_report), Mockito.anyString());

        zeirHia2ReportRepositorySpy.addReport(reportJsonObject);

        Mockito.verify(sqLiteDatabase, Mockito.times(1))
                .insert(Mockito.eq(Hia2ReportRepository.Table.hia2_report.name()), Mockito.eq(null), Mockito.any(ContentValues.class));

    }

    @Test
    public void testAddReportIfReportExistsShouldCallUpdate() throws JSONException {
        ZeirHia2ReportRepository zeirHia2ReportRepositorySpy = Mockito.spy(zeirHia2ReportRepository);
        JSONObject reportJsonObject = new JSONObject();
        reportJsonObject.put(ZeirHia2ReportRepository.ReportColumn.grouping.name(), "chn");
        reportJsonObject.put(ZeirHia2ReportRepository.ReportColumn.reportType.name(), new JSONObject().toString());
        reportJsonObject.put(ZeirHia2ReportRepository.ReportColumn.updatedAt.name(), new Date());
        reportJsonObject.put(ZeirHia2ReportRepository.ReportColumn.reportType.name(), "Report A");
        reportJsonObject.put(ZeirHia2ReportRepository.ReportColumn.formSubmissionId.name(), "454");

        Mockito.doReturn(sqLiteDatabase).when(zeirHia2ReportRepositorySpy).getWritableDatabase();
        Mockito.doReturn(true).when(zeirHia2ReportRepositorySpy)
                .checkIfExistsByFormSubmissionId(
                        Mockito.eq(Hia2ReportRepository.Table.hia2_report),
                        Mockito.eq(reportJsonObject.getString(ZeirHia2ReportRepository.ReportColumn.formSubmissionId.name())));

        zeirHia2ReportRepositorySpy.addReport(reportJsonObject);

        Mockito.verify(zeirHia2ReportRepositorySpy, Mockito.times(1))
                .checkIfExistsByFormSubmissionId(Mockito.eq(Hia2ReportRepository.Table.hia2_report),
                        Mockito.eq(reportJsonObject.getString(ZeirHia2ReportRepository.ReportColumn.formSubmissionId.name())));

        Mockito.verify(sqLiteDatabase, Mockito.never())
                .insert(Mockito.eq(Hia2ReportRepository.Table.hia2_report.name()),
                        Mockito.eq(null),
                        Mockito.any(ContentValues.class));

        Mockito.verify(sqLiteDatabase, Mockito.times(1))
                .update(Mockito.eq(Hia2ReportRepository.Table.hia2_report.name()),
                        Mockito.any(ContentValues.class),
                        Mockito.eq(ZeirHia2ReportRepository.ReportColumn.formSubmissionId.name() + "=?"),
                        Mockito.eq(new String[]{reportJsonObject.getString(ZeirHia2ReportRepository.ReportColumn.formSubmissionId.name())}));

    }
}