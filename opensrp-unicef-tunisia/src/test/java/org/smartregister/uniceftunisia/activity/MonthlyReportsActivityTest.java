package org.smartregister.uniceftunisia.activity;

import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.uniceftunisia.BaseRobolectricTest;
import org.smartregister.uniceftunisia.application.UnicefTunisiaApplication;
import org.smartregister.uniceftunisia.reporting.monthly.MonthlyReportsActivity;
import org.smartregister.uniceftunisia.repository.DailyTalliesRepository;
import org.smartregister.uniceftunisia.repository.MonthlyTalliesRepository;

import java.util.Calendar;
import java.util.Date;

public class MonthlyReportsActivityTest extends BaseRobolectricTest {

    private MonthlyReportsActivity monthlyReportsActivity;

    @Mock
    private SQLiteDatabase database;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        MonthlyTalliesRepository.createTable(database);
        MonthlyTalliesRepository monthlyTalliesRepository = Mockito.spy(new MonthlyTalliesRepository());
        DailyTalliesRepository dailyTalliesRepository = Mockito.spy((UnicefTunisiaApplication.getInstance().dailyTalliesRepository()));
        ReflectionHelpers.setField(UnicefTunisiaApplication.getInstance(), "dailyTalliesRepository", dailyTalliesRepository);

        Mockito.doReturn(database).when(monthlyTalliesRepository).getReadableDatabase();
        Mockito.doReturn(database).when(monthlyTalliesRepository).getWritableDatabase();
        setupMonthlyRepository();
        monthlyReportsActivity = Robolectric.setupActivity(MonthlyReportsActivity.class);
    }

    @Test
    public void testThaActivityStarted() {
        Assert.assertNotNull(monthlyReportsActivity);
    }

    private void setupMonthlyRepository() {
        Calendar calendarStartDate = Calendar.getInstance();
        calendarStartDate.add(Calendar.MONTH, -24);
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"month", "created_at"}, 0);
        Calendar tallyMonth = (Calendar) calendarStartDate.clone();
        tallyMonth.add(Calendar.MONTH, -12);
        for (int i = 0; i < 36; i++) {
            tallyMonth.add(Calendar.MONTH, 1);
            matrixCursor.addRow(new Object[]{MonthlyTalliesRepository.DF_YYYYMM.format(tallyMonth.getTime()), new Date().getTime()});
        }
        Mockito.doReturn(matrixCursor).when(database).query(Mockito.eq("monthly_tallies"),
                Mockito.any(String[].class), Mockito.anyString(), Mockito.nullable(String[].class),
                Mockito.eq("month"), Mockito.nullable(String.class), Mockito.nullable(String.class));
    }
}