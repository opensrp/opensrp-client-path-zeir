package org.smartregister.uniceftunisia.repository;

import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.smartregister.uniceftunisia.BaseRobolectricTest;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 06-03-2020.
 */
public class DailyTalliesRepositoryTest extends BaseRobolectricTest {

    private DailyTalliesRepository dailyTalliesRepository;

    @Mock
    private SQLiteDatabase database;

    @Before
    public void setUp() {
        dailyTalliesRepository = Mockito.spy(new DailyTalliesRepository());
        Mockito.doReturn(database).when(dailyTalliesRepository).getReadableDatabase();
    }

    @Test
    public void findAllDistinctMonthsShouldOnlyReturnDistinctMonths() {
        Date startDate = new Date();

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"day"}, 0);
        int counter = (3 * 30) - 10;

        Calendar calendar = Calendar.getInstance();
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.add(Calendar.DAY_OF_MONTH, - (dayOfMonth - 1));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (int i = 0; i < counter; i++) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            matrixCursor.addRow(new String[]{simpleDateFormat.format(calendar.getTime())});
        }

        matrixCursor = Mockito.spy(matrixCursor);

        Mockito.doReturn(matrixCursor).when(database).query(Mockito.eq(true), Mockito.eq("indicator_daily_tally")
                , Mockito.any(String[].class), Mockito.anyString(), Mockito.nullable(String[].class), Mockito.nullable(String.class)
                , Mockito.nullable(String.class), Mockito.nullable(String.class), Mockito.nullable(String.class));

        List<String> distinctMonths = dailyTalliesRepository.findAllDistinctMonths(new SimpleDateFormat("yyyy-MM"), startDate, startDate);
        Assert.assertEquals(3, distinctMonths.size());
        Mockito.verify(matrixCursor).close();
    }

}