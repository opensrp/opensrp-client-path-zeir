package org.smartregister.path.repository;

import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.path.BaseRobolectricTest;
import org.smartregister.path.application.ZeirApplication;
import org.smartregister.path.shadow.ShadowSQLiteDatabase;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 06-03-2020.
 */
@Config(shadows = {ShadowSQLiteDatabase.class})
public class ZeirRepositoryTest extends BaseRobolectricTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private ZeirRepository zeirRepository;

    @Mock
    private SQLiteDatabase sqLiteDatabase;

    @Before
    public void setUp() {
        zeirRepository = Mockito.spy((ZeirRepository) ZeirApplication.getInstance().getRepository());

        Mockito.doReturn(sqLiteDatabase).when(zeirRepository).getReadableDatabase();
        Mockito.doReturn(sqLiteDatabase).when(zeirRepository).getReadableDatabase(Mockito.anyString());
        Mockito.doReturn(sqLiteDatabase).when(zeirRepository).getWritableDatabase();
        Mockito.doReturn(sqLiteDatabase).when(zeirRepository).getWritableDatabase(Mockito.anyString());

        ReflectionHelpers.setField(ZeirApplication.getInstance(), "repository", zeirRepository);
    }

    @Test
    @Ignore("Fix Issue on Reporting Library: YML parser cannot parse primitive 'boolean' type requires 'Boolean' class type to parse 'isMultiResult' attribute")
    public void onCreateShouldCreate32tables() {
        Mockito.doNothing().when(zeirRepository).onUpgrade(Mockito.any(SQLiteDatabase.class), Mockito.anyInt(), Mockito.anyInt());
        SQLiteDatabase database = Mockito.mock(SQLiteDatabase.class);
        zeirRepository.onCreate(database);

        // TODO: Investigate this counter
        Mockito.verify(database, Mockito.times(35)).execSQL(Mockito.contains("CREATE TABLE"));
    }
}