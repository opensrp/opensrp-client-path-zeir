package org.smartregister.pathzeir.fragment;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.child.domain.Field;
import org.smartregister.child.util.ChildDbUtils;
import org.smartregister.pathzeir.util.AppConstants;

import java.util.ArrayList;
import java.util.HashMap;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ChildDbUtils.class)
public class ChildRegisterDataFragmentTest {

    private HashMap<String, String> detailMap;

    private ChildRegistrationDataFragment childRegisterDataFragment;

    @Before
    public void setUp() {
        childRegisterDataFragment = new ChildRegistrationDataFragment();
        detailMap = new HashMap<>();
        detailMap.put("gender", "Male");
        detailMap.put("first_name", "Jan");
        detailMap.put("zeir_id", "1889542");
        detailMap.put("last_name", "Puff");
        detailMap.put("base_entity_id", "1111111");
    }

    @Test
    public void testResetAdapterData() {
        ChildRegistrationDataFragment fragment = PowerMockito.spy(childRegisterDataFragment);
        PowerMockito.mockStatic(ChildDbUtils.class);

        ArrayList<Field> fields = new ArrayList<>();
        Field field1 = new Field();
        Field field2 = new Field();
        Field field3 = new Field();
        Field field4 = new Field();
        Field field5 = new Field();
        Field field6 = new Field();
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

        ReflectionHelpers.setField(fragment, "fields", fields);
        ReflectionHelpers.setField(fragment, "fieldNameAliasMap", new HashMap<String, Integer>() {
        });

        HashMap<String, String> weightMap = new HashMap<>();
        weightMap.put(AppConstants.KeyConstants.BIRTH_WEIGHT.toLowerCase(), "3");
        PowerMockito.when(ChildDbUtils.fetchChildFirstGrowthAndMonitoring(Mockito.anyString())).thenReturn(weightMap);
        Mockito.doReturn("").when(fragment).cleanValue(Mockito.any(), Mockito.anyString());

        fragment.resetAdapterData(detailMap);

        Assert.assertEquals(fragment.getmAdapter().getItemCount(), detailMap.size());
    }

}

