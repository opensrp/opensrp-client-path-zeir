package org.smartregister.uniceftunisia.repository;

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
import org.smartregister.uniceftunisia.BaseRobolectricTest;
import org.smartregister.uniceftunisia.application.UnicefTunisiaApplication;
import org.smartregister.uniceftunisia.shadow.ShadowSQLiteDatabase;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 06-03-2020.
 */
@Config(shadows = {ShadowSQLiteDatabase.class})
public class UnicefTunisiaRepositoryTest extends BaseRobolectricTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private UnicefTunisiaRepository unicefTunisiaRepository;

    @Mock
    private SQLiteDatabase sqLiteDatabase;

    @Before
    public void setUp() {
        unicefTunisiaRepository = Mockito.spy((UnicefTunisiaRepository) UnicefTunisiaApplication.getInstance().getRepository());

        Mockito.doReturn(sqLiteDatabase).when(unicefTunisiaRepository).getReadableDatabase();
        Mockito.doReturn(sqLiteDatabase).when(unicefTunisiaRepository).getReadableDatabase(Mockito.anyString());
        Mockito.doReturn(sqLiteDatabase).when(unicefTunisiaRepository).getWritableDatabase();
        Mockito.doReturn(sqLiteDatabase).when(unicefTunisiaRepository).getWritableDatabase(Mockito.anyString());

        ReflectionHelpers.setField(UnicefTunisiaApplication.getInstance(), "repository", unicefTunisiaRepository);
    }

    @Test
    @Ignore("Fix Issue on Reporting Library: YML parser cannot parse primitive 'boolean' type requires 'Boolean' class type to parse 'isMultiResult' attribute")
    public void onCreateShouldCreate32tables() {
        Mockito.doNothing().when(unicefTunisiaRepository).onUpgrade(Mockito.any(SQLiteDatabase.class), Mockito.anyInt(), Mockito.anyInt());
        SQLiteDatabase database = Mockito.mock(SQLiteDatabase.class);
        unicefTunisiaRepository.onCreate(database);

        // TODO: Investigate this counter
        Mockito.verify(database, Mockito.times(35)).execSQL(Mockito.contains("CREATE TABLE"));
    }
}