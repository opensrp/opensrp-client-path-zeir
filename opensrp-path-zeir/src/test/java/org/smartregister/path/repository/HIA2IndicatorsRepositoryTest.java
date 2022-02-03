package org.smartregister.path.repository;


import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import android.content.ContentValues;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.smartregister.path.BaseRobolectricTest;
import org.smartregister.path.model.Hia2Indicator;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HIA2IndicatorsRepositoryTest extends BaseRobolectricTest {

    private HIA2IndicatorsRepository hia2IndicatorsRepository;

    @Mock
    private SQLiteDatabase mockSqLiteDatabase;

    @Mock
    private Cursor mockCursor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        hia2IndicatorsRepository = spy(new HIA2IndicatorsRepository());
    }


    @Test
    public void testCreateTableShouldExecute8Queries() {
        HIA2IndicatorsRepository.createTable(mockSqLiteDatabase);
        verify(mockSqLiteDatabase, times(8)).execSQL(anyString());
    }

    @Test
    public void testFindAllByGroupingShouldReturnEmptyMap() {
        String grouping = "groupA";
        doAnswer(new Answer<Boolean>() {
            int count = 1;

            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                if (count == 0) {
                    return true;
                }
                count--;
                return false;
            }
        }).when(mockCursor).isAfterLast();
        doReturn(true).when(mockCursor).moveToFirst();
        doReturn(1).when(mockCursor).getCount();
        doReturn(mockCursor).when(mockSqLiteDatabase).query(eq(HIA2IndicatorsRepository.TABLE_NAME),
                eq(HIA2IndicatorsRepository.HIA2_TABLE_COLUMNS), eq("grouping = ?"), eq(new String[]{grouping}), eq(null), eq(null), eq(null), eq(null));
        doReturn(mockSqLiteDatabase).when(hia2IndicatorsRepository).getReadableDatabase();
        HashMap<String, Hia2Indicator> map = hia2IndicatorsRepository.findAllByGrouping(grouping);
        assertTrue(map.isEmpty());
    }

    @Test
    public void testSaveShouldInvokeInsert() {
        Map<String, String> indicator = new HashMap<>();
        indicator.put("label", "labelA");
        indicator.put("description", "descriptionA");
        indicator.put("dhisId", "dhisIdA");
        doReturn(false).when(mockCursor).moveToFirst();
        doReturn(mockCursor).when(mockSqLiteDatabase).rawQuery(anyString(), eq(null));
        hia2IndicatorsRepository.save(mockSqLiteDatabase, Collections.singletonList(indicator));

        verify(mockSqLiteDatabase).beginTransaction();
        verify(mockSqLiteDatabase).insert(eq(HIA2IndicatorsRepository.TABLE_NAME), eq(null), any(ContentValues.class));

        verify(mockSqLiteDatabase).setTransactionSuccessful();
        verify(mockSqLiteDatabase).endTransaction();
    }

    @Test
    public void testSaveShouldInvokeUpdate() {
        Long id = 1L;

        Map<String, String> indicator = new HashMap<>();
        indicator.put("label", "labelA");
        indicator.put("description", "descriptionA");
        indicator.put("dhisId", "dhisIdA");
        doReturn(true).when(mockCursor).moveToFirst();
        doReturn(id).when(mockCursor).getLong(0);
        doReturn(mockCursor).when(mockSqLiteDatabase).rawQuery(anyString(), eq(null));
        hia2IndicatorsRepository.save(mockSqLiteDatabase, Collections.singletonList(indicator));

        verify(mockSqLiteDatabase).beginTransaction();
        verify(mockSqLiteDatabase).update(eq(HIA2IndicatorsRepository.TABLE_NAME), any(ContentValues.class), eq(HIA2IndicatorsRepository.ID_COLUMN + " = ?"), eq(new String[]{id.toString()}));

        verify(mockSqLiteDatabase).setTransactionSuccessful();
        verify(mockSqLiteDatabase).endTransaction();
    }
}