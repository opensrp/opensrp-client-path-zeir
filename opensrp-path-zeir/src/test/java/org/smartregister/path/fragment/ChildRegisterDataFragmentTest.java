package org.smartregister.path.fragment;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.domain.Field;
import org.smartregister.child.util.ChildAppProperties;
import org.smartregister.path.BaseUnitTest;
import org.smartregister.repository.Repository;
import org.smartregister.util.AppProperties;

import java.util.ArrayList;
import java.util.HashMap;
@Ignore("Fix memory leak error: #250")
public class ChildRegisterDataFragmentTest extends BaseUnitTest {

    @Mock
    private CoreLibrary coreLibrary;

    @Mock
    private ChildLibrary childLibrary;

    @Mock
    private Repository repository;

    @Mock
    private SQLiteDatabase sqLiteDatabase;

    private HashMap<String, String> detailMap;

    private ChildRegistrationDataFragment childRegisterDataFragment;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        childRegisterDataFragment = new ChildRegistrationDataFragment();
        detailMap = new HashMap<>();
        detailMap.put("gender", "Male");
        detailMap.put("first_name", "Jan");
        detailMap.put("zeir_id", "1889542");
        detailMap.put("last_name", "Puff");
        detailMap.put("base_entity_id", "1111111");
        detailMap.put("birth_weight", "5");
        detailMap.put("birth_height", "55");
    }

    @Test
    public void testResetAdapterData() {
        ChildRegistrationDataFragment fragment = Mockito.spy(childRegisterDataFragment);

        String baseEntityId = "1111111";
        String dateCreated = "2020-01-22 10:28:38";

        Cursor weightCursor = Mockito.mock(Cursor.class);
        Mockito.when(sqLiteDatabase.query("weights", new String[]{"kg", "created_at"},
                "base_entity_id = ?",
                new String[]{baseEntityId}, null, null, "created_at asc", "1")).thenReturn(weightCursor);
        Mockito.when(weightCursor.getCount()).thenReturn(1);
        Mockito.when(weightCursor.moveToNext()).thenReturn(true);
        Mockito.when(weightCursor.getString(0)).thenReturn("20");
        Mockito.when(weightCursor.getString(1)).thenReturn(dateCreated);


        Cursor heightCursor = Mockito.mock(Cursor.class);
        Mockito.when(sqLiteDatabase.query("heights", new String[]{"cm", "created_at"},
                "base_entity_id = ? and created_at = ?",
                new String[]{baseEntityId, dateCreated}, null, null, null, "1")).thenReturn(heightCursor);
        Mockito.when(heightCursor.getCount()).thenReturn(1);
        Mockito.when(heightCursor.moveToNext()).thenReturn(true);
        Mockito.when(heightCursor.getString(0)).thenReturn("30");

        Mockito.when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(childLibrary.getRepository()).thenReturn(repository);
        AppProperties appProperties = Mockito.mock(AppProperties.class);
        Context context = Mockito.mock(Context.class);
        Mockito.when(appProperties.hasProperty(ChildAppProperties.KEY.MONITOR_HEIGHT)).thenReturn(true);
        Mockito.when(appProperties.getPropertyBoolean(ChildAppProperties.KEY.MONITOR_HEIGHT)).thenReturn(true);
        Mockito.when(context.getAppProperties()).thenReturn(appProperties);
        Mockito.when(coreLibrary.context()).thenReturn(context);
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);

        ArrayList<Field> fields = new ArrayList<>();
        Field field1 = new Field();
        Field field2 = new Field();
        Field field3 = new Field();
        Field field4 = new Field();
        Field field5 = new Field();
        Field field6 = new Field();
        Field field7 = new Field();
        ReflectionHelpers.setField(field1, "key", "gender");
        fields.add(field1);
        ReflectionHelpers.setField(field2, "key", "first_name");
        fields.add(field2);
        ReflectionHelpers.setField(field3, "key", "zeir_id");
        fields.add(field3);
        ReflectionHelpers.setField(field4, "key", "last_name");
        fields.add(field4);
        ReflectionHelpers.setField(field5, "key", "base_entity_id");
        fields.add(field5);
        ReflectionHelpers.setField(field6, "key", "birth_weight");
        fields.add(field6);
        ReflectionHelpers.setField(field7, "key", "birth_height");
        fields.add(field7);

        ReflectionHelpers.setField(fragment, "fields", fields);
        ReflectionHelpers.setField(fragment, "fieldNameAliasMap", new HashMap<String, Integer>() {
        });

        Mockito.doReturn("").when(fragment).cleanValue(Mockito.any(), Mockito.anyString());
        Mockito.doReturn(RandomStringUtils.random(6)).when(fragment).getResourceLabel(Mockito.anyString());

        fragment.resetAdapterData(detailMap);

        Assert.assertEquals(fragment.getmAdapter().getItemCount(), detailMap.size());
    }

    @Test
    public void testAddUnFormattedFieldsNotNull()
    {
        Assert.assertNotNull(childRegisterDataFragment.addUnFormattedNumberFields());
    }

    @After
    public void tearDown() {
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", null);
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", null);
    }

}

