package org.smartregister.uniceftunisia.repository;

import android.content.ContentValues;

import net.sqlcipher.MatrixCursor;
import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.reporting.ReportingLibrary;
import org.smartregister.reporting.domain.IndicatorTally;
import org.smartregister.reporting.repository.DailyIndicatorCountRepository;
import org.smartregister.uniceftunisia.BaseRobolectricTest;
import org.smartregister.uniceftunisia.application.UnicefTunisiaApplication;
import org.smartregister.uniceftunisia.domain.MonthlyTally;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 06-03-2020.
 */
public class MonthlyTalliesRepositoryTest extends BaseRobolectricTest {

    private MonthlyTalliesRepository monthlyTalliesRepository;

    @Mock
    private SQLiteDatabase database;

    private DailyTalliesRepository dailyTalliesRepository;

    @Before
    public void setUp() throws Exception {
        monthlyTalliesRepository = Mockito.spy(new MonthlyTalliesRepository());
        dailyTalliesRepository = Mockito.spy(UnicefTunisiaApplication.getInstance().dailyTalliesRepository());
        ReflectionHelpers.setField(UnicefTunisiaApplication.getInstance(), "dailyTalliesRepository", dailyTalliesRepository);

        Mockito.doReturn(database).when(monthlyTalliesRepository).getReadableDatabase();
        Mockito.doReturn(database).when(monthlyTalliesRepository).getWritableDatabase();
    }

    @Test
    public void createTableShouldMake8CreateAndIndexQueries() {
        SQLiteDatabase database = Mockito.mock(SQLiteDatabase.class);

        MonthlyTalliesRepository.createTable(database);
        ArgumentCaptor<String> queryStringsCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(database, Mockito.times(8)).execSQL(queryStringsCaptor.capture());

        List<String> queryStrings = queryStringsCaptor.getAllValues();
        int countOfCreateTable = 0;
        int countCreateUniqueIndex = 0;
        int countCreateIndex = 0;

        for (String queryString: queryStrings) {
            if (queryString.contains("CREATE TABLE")) {
                countOfCreateTable++;
            }

            if (queryString.contains("CREATE UNIQUE INDEX")) {
                countCreateUniqueIndex++;
            }

            if (queryString.contains("CREATE INDEX")) {
                countCreateIndex++;
            }
        }

        Assert.assertEquals(1, countOfCreateTable);
        Assert.assertEquals(1, countCreateUniqueIndex);
        Assert.assertEquals(6, countCreateIndex);
    }

    @Test
    public void findUneditedDraftMonthsShouldReturnValidMonthsWithinRange() throws ParseException {
        List<String> tallyMonths = new ArrayList<>();
        tallyMonths.add("2020-09");
        tallyMonths.add("2020-08");
        tallyMonths.add("2020-07");
        tallyMonths.add("2020-06");
        tallyMonths.add("2019-06");

        Mockito.doReturn(tallyMonths).when(dailyTalliesRepository).findAllDistinctMonths(Mockito.any(SimpleDateFormat.class)
                , Mockito.any(Date.class), Mockito.any(Date.class), Mockito.nullable(String.class));

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"month"}, 0);
        matrixCursor.addRow(new String[]{"2020-09"});

        Mockito.doReturn(matrixCursor).when(database).query(Mockito.eq("monthly_tallies"), Mockito.any(String[].class)
                , Mockito.anyString(), Mockito.nullable(String[].class), Mockito.nullable(String.class)
                , Mockito.nullable(String.class), Mockito.nullable(String.class));

        List<Date> monthsWithoutDrafts = monthlyTalliesRepository.findUneditedDraftMonths(
                MonthlyTalliesRepository.DF_YYYYMM.parse("2020-01"),
                MonthlyTalliesRepository.DF_YYYYMM.parse("2020-12"));
        Assert.assertEquals(3, monthsWithoutDrafts.size());
        Assert.assertEquals("2020-08", MonthlyTalliesRepository.DF_YYYYMM.format(monthsWithoutDrafts.get(0)));
        Assert.assertEquals("2020-07", MonthlyTalliesRepository.DF_YYYYMM.format(monthsWithoutDrafts.get(1)));
        Assert.assertEquals("2020-06", MonthlyTalliesRepository.DF_YYYYMM.format(monthsWithoutDrafts.get(2)));
    }


    @Test
    public void findUneditedDraftMonthsShouldReturn0WhenDateRangeIsInadequate() {
        List<String> tallyMonths = new ArrayList<>();
        tallyMonths.add("2020-09");
        tallyMonths.add("2020-08");
        tallyMonths.add("2020-07");
        tallyMonths.add("2020-06");
        Mockito.doReturn(tallyMonths).when(dailyTalliesRepository).findAllDistinctMonths(Mockito.any(SimpleDateFormat.class)
                , Mockito.any(Date.class), Mockito.any(Date.class), Mockito.nullable(String.class));

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"month"}, 0);
        matrixCursor.addRow(new String[]{"2020-09"});

        Mockito.doReturn(matrixCursor).when(database).query(Mockito.eq("monthly_tallies"), Mockito.any(String[].class)
                , Mockito.anyString(), Mockito.nullable(String[].class), Mockito.nullable(String.class)
                , Mockito.nullable(String.class), Mockito.nullable(String.class));

        List<Date> monthsWithoutDrafts = monthlyTalliesRepository.findUneditedDraftMonths(new Date(), new Date());
        Assert.assertEquals(0, monthsWithoutDrafts.size());
    }

    @Test
    public void findDraftsShouldReturnDraftUnsentTallies() {
        Date dateNow = new Date();
        Calendar calendarStartDate = Calendar.getInstance();
        calendarStartDate.add(Calendar.MONTH, -24);

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"_id", "indicator_code", "provider_id", "value", "month"
                , "edited", "date_sent", "indicator_grouping", "created_at", "updated_at"}, 0);
        matrixCursor.addRow(new Object[]{1L, "CHN_01", "anm", "90", "2020-09", 0, dateNow.getTime(), null, dateNow.getTime(), dateNow.getTime()});
        matrixCursor.addRow(new Object[]{2L, "CHN_02", "anm", "100", "2020-09", 0, dateNow.getTime(), null, dateNow.getTime(), dateNow.getTime()});

        Mockito.doReturn(matrixCursor).when(database).query(Mockito.eq("monthly_tallies"), Mockito.any(String[].class)
                , Mockito.anyString(), Mockito.nullable(String[].class), Mockito.nullable(String.class)
                , Mockito.nullable(String.class), Mockito.nullable(String.class), Mockito.nullable(String.class));


        List<MonthlyTally> draftMonthly = monthlyTalliesRepository.findDrafts("2020-09");
        Assert.assertEquals(2, draftMonthly.size());

        Assert.assertEquals("CHN_01", draftMonthly.get(0).getIndicator());
        Assert.assertEquals("90", draftMonthly.get(0).getValue());

        Assert.assertEquals("CHN_02", draftMonthly.get(1).getIndicator());
        Assert.assertEquals("100", draftMonthly.get(1).getValue());


        Assert.assertEquals(2, draftMonthly.size());
    }
    @Test
    public void findDraftsShouldReturnTotalTalliesFromDailyTallies() {
        DailyIndicatorCountRepository dailyIndicatorCountRepository = Mockito.spy(ReportingLibrary.getInstance().dailyIndicatorCountRepository());
        ReflectionHelpers.setField(ReportingLibrary.getInstance(), "dailyIndicatorCountRepository", dailyIndicatorCountRepository);

        ArrayList<IndicatorTally> chn1IndicatorTallies = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            IndicatorTally indicatorTally = new IndicatorTally((i+1L), 30, "CHN_01", new Date());
            chn1IndicatorTallies.add(indicatorTally);
        }

        ArrayList<IndicatorTally> chn2IndicatorTallies = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            IndicatorTally indicatorTally = new IndicatorTally((i+1L), 10, "CHN_02", new Date());
            chn2IndicatorTallies.add(indicatorTally);
        }

        HashMap<String, List<IndicatorTally>> dailyTallies = new HashMap<>();
        dailyTallies.put("CHN_01", chn1IndicatorTallies);
        dailyTallies.put("CHN_02", chn2IndicatorTallies);

        Mockito.doReturn(dailyTallies).when(dailyIndicatorCountRepository).findTalliesInMonth(Mockito.any(Date.class), Mockito.nullable(String.class));

        List<MonthlyTally> draftMonthly = monthlyTalliesRepository.findDrafts("2020-09");
        Assert.assertEquals(2, draftMonthly.size());

        Assert.assertEquals("600", draftMonthly.get(0).getValue());
        Assert.assertEquals("CHN_01", draftMonthly.get(0).getIndicator());

        Assert.assertEquals("200", draftMonthly.get(1).getValue());
        Assert.assertEquals("CHN_02", draftMonthly.get(1).getIndicator());
    }

    @Test
    public void findShouldExtractAllCursorRows() {
        Date dateNow = new Date();
        Calendar calendarStartDate = Calendar.getInstance();
        calendarStartDate.add(Calendar.MONTH, -24);

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"_id", "indicator_code", "provider_id", "value", "month"
                , "edited", "date_sent", "indicator_grouping", "created_at", "updated_at"}, 0);
        matrixCursor.addRow(new Object[]{1L, "CHN_01", "anm", "90", "2020-09", 0, dateNow.getTime(), null, dateNow.getTime(), dateNow.getTime()});
        matrixCursor.addRow(new Object[]{2L, "CHN_02", "anm", "100", "2020-09", 0, dateNow.getTime(), null, dateNow.getTime(), dateNow.getTime()});

        Mockito.doReturn(matrixCursor).when(database).query(Mockito.eq("monthly_tallies"), Mockito.any(String[].class)
                , Mockito.anyString(), Mockito.nullable(String[].class), Mockito.nullable(String.class)
                , Mockito.nullable(String.class), Mockito.nullable(String.class), Mockito.nullable(String.class));

        List<MonthlyTally> monthlyTallies = monthlyTalliesRepository.find("2020-09");
        Assert.assertEquals(2, monthlyTallies.size());

        Assert.assertEquals("CHN_01", monthlyTallies.get(0).getIndicator());
        Assert.assertEquals("90", monthlyTallies.get(0).getValue());

        Assert.assertEquals("100", monthlyTallies.get(1).getValue());
        Assert.assertEquals("CHN_02", monthlyTallies.get(1).getIndicator());
    }

    @Test
    public void findAllSentShouldReturnValidMonthlyTalliesFromCursorData() {
        Date dateNow = new Date();
        Calendar calendarStartDate = Calendar.getInstance();
        calendarStartDate.add(Calendar.MONTH, -24);

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"_id", "indicator_code", "provider_id", "value", "month"
                , "edited", "date_sent", "indicator_grouping", "created_at", "updated_at"}, 0);
        matrixCursor.addRow(new Object[]{1L, "CHN_01", "anm", "90", "2020-09", 0, dateNow.getTime(), null, dateNow.getTime(), dateNow.getTime()});
        matrixCursor.addRow(new Object[]{2L, "CHN_02", "anm", "100", "2020-09", 0, dateNow.getTime(), null, dateNow.getTime(), dateNow.getTime()});

        Mockito.doReturn(matrixCursor).when(database).query(Mockito.eq("monthly_tallies"), Mockito.any(String[].class)
                , Mockito.anyString(), Mockito.nullable(String[].class), Mockito.nullable(String.class)
                , Mockito.nullable(String.class), Mockito.nullable(String.class), Mockito.nullable(String.class));

        HashMap<String, ArrayList<MonthlyTally>> sentMonthlyTallies = monthlyTalliesRepository.findAllSent(MonthlyTalliesRepository.DF_YYYYMM);
        Assert.assertEquals(2, sentMonthlyTallies.get("2020-09").size());
        Assert.assertEquals("90", sentMonthlyTallies.get("2020-09").get(0).getValue());
        Assert.assertEquals("100", sentMonthlyTallies.get("2020-09").get(1).getValue());
        Assert.assertEquals("CHN_01", sentMonthlyTallies.get("2020-09").get(0).getIndicator());
        Assert.assertEquals("CHN_02", sentMonthlyTallies.get("2020-09").get(1).getIndicator());
    }

    @Test
    public void saveShouldCallDbInsertForEachMapEntry() {
        int mapEntries = 30;

        HashMap<String, String> formValues = new HashMap<>();

        for (int i = 0; i < mapEntries; i++) {
            formValues.put("CH_" + i, String.valueOf(i * 45));
        }

        Assert.assertTrue(monthlyTalliesRepository.save(formValues, new Date()));
        Mockito.verify(database).beginTransaction();
        Mockito.verify(database).setTransactionSuccessful();
        Mockito.verify(database).endTransaction();
        Mockito.verify(database, Mockito.times(mapEntries)).insertWithOnConflict(Mockito.eq("monthly_tallies")
                , Mockito.isNull(), Mockito.any(ContentValues.class), Mockito.eq(SQLiteDatabase.CONFLICT_REPLACE));
    }

    @Test
    public void saveShouldReturnFalseWhenInsertThrowsException() {
        int mapEntries = 30;

        HashMap<String, String> formValues = new HashMap<>();

        for (int i = 0; i < mapEntries; i++) {
            formValues.put("CH_" + i, String.valueOf(i * 45));
        }

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                throw new SQLException("Some test error");
            }
        }).when(database).insertWithOnConflict(Mockito.eq("monthly_tallies")
                , Mockito.isNull(), Mockito.any(ContentValues.class), Mockito.eq(SQLiteDatabase.CONFLICT_REPLACE));

        Assert.assertFalse(monthlyTalliesRepository.save(formValues, new Date()));
        Mockito.verify(database).beginTransaction();
        Mockito.verify(database).endTransaction();
    }

    @Test
    public void saveShouldReturnTrueAndCallDbInsertTransactionMethods() {
        MonthlyTally monthlyTally = new MonthlyTally();
        monthlyTally.setMonth(new Date());


        Assert.assertTrue(monthlyTalliesRepository.save(monthlyTally));
        Mockito.verify(database).beginTransaction();
        Mockito.verify(database).setTransactionSuccessful();
        Mockito.verify(database).endTransaction();
        Mockito.verify(database).insertWithOnConflict(Mockito.eq("monthly_tallies")
                , Mockito.isNull(), Mockito.any(ContentValues.class), Mockito.eq(SQLiteDatabase.CONFLICT_REPLACE));
    }

    @Test
    public void saveShouldReturnFalseAndCallDbTransactionMethods() {
        MonthlyTally monthlyTally = new MonthlyTally();
        monthlyTally.setMonth(new Date());

        // Mock throwing an exception
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                throw new SQLException("Some test error");
            }
        }).when(database).insertWithOnConflict(Mockito.eq("monthly_tallies")
                , Mockito.isNull(), Mockito.any(ContentValues.class), Mockito.eq(SQLiteDatabase.CONFLICT_REPLACE));

        Assert.assertFalse(monthlyTalliesRepository.save(monthlyTally));

        Mockito.verify(database).beginTransaction();
        Mockito.verify(database, Mockito.times(0)).setTransactionSuccessful();
        Mockito.verify(database).endTransaction();
    }

    @Test
    public void findEditedDraftMonthsShouldReturnResultsWithinDateRange() {
        Date endDate = new Date();
        Calendar calendarStartDate = Calendar.getInstance();
        calendarStartDate.add(Calendar.MONTH, -24);

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"month", "created_at"}, 0);

        Calendar tallyMonth = (Calendar) calendarStartDate.clone();
        tallyMonth.add(Calendar.MONTH, -12);
        for (int i = 0; i < 36; i++) {
            tallyMonth.add(Calendar.MONTH, 1);
            matrixCursor.addRow(new Object[]{MonthlyTalliesRepository.DF_YYYYMM.format(tallyMonth.getTime()), new Date().getTime()});
        }

        Mockito.doReturn(matrixCursor).when(database).query(Mockito.eq("monthly_tallies"), Mockito.any(String[].class)
                , Mockito.anyString(), Mockito.nullable(String[].class), Mockito.eq("month"), Mockito.nullable(String.class), Mockito.nullable(String.class));

        List<MonthlyTally> monthlyTallies = monthlyTalliesRepository.findEditedDraftMonths(calendarStartDate.getTime(), endDate);
        Assert.assertEquals(24, monthlyTallies.size());
    }
}