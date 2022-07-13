package org.smartregister.path.util;

import com.google.common.collect.Lists;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.domain.ChildMetadata;
import org.smartregister.child.util.Constants;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.util.VaccineCache;
import org.smartregister.path.BaseUnitTest;
import org.smartregister.path.activity.ChildFormActivity;
import org.smartregister.path.activity.ChildImmunizationActivity;
import org.smartregister.path.activity.ChildProfileActivity;
import org.smartregister.path.activity.ChildRegisterActivity;
import org.smartregister.path.repository.AppChildRegisterQueryProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
@Ignore("Fix memory leak error: #250")
public class DBQueryHelperTest extends BaseUnitTest {
    @Mock
    private ImmunizationLibrary immunizationLibrary;

    @Mock
    private ChildLibrary childLibrary;

    private final Map<String, VaccineCache> vaccineCacheMap = new HashMap<>();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getFilterSelectionConditionWithVaccineArrayHavingTwoVaccines() {
        ArrayList<VaccineRepo.Vaccine> arrayList = new ArrayList<>();
        arrayList.add(VaccineRepo.Vaccine.HepB);
        arrayList.add(VaccineRepo.Vaccine.penta1);
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);
        ReflectionHelpers.setStaticField(ImmunizationLibrary.class, "instance", immunizationLibrary);

        ChildMetadata metadata = new ChildMetadata(ChildFormActivity.class, ChildProfileActivity.class,
                ChildImmunizationActivity.class, ChildRegisterActivity.class, true, new AppChildRegisterQueryProvider());
        metadata.updateChildRegister(AppConstants.JsonForm.CHILD_ENROLLMENT, AppConstants.TableNameConstants.ALL_CLIENTS,
                AppConstants.TableNameConstants.ALL_CLIENTS, AppConstants.EventTypeConstants.CHILD_REGISTRATION,
                AppConstants.EventTypeConstants.UPDATE_CHILD_REGISTRATION, AppConstants.EventTypeConstants.OUT_OF_CATCHMENT, AppConstants.ConfigurationConstants.CHILD_REGISTER,
                AppConstants.RelationshipConstants.MOTHER, AppConstants.JsonForm.OUT_OF_CATCHMENT_SERVICE);

        Mockito.doReturn(metadata).when(childLibrary).metadata();

        VaccineCache vaccineCache = new VaccineCache();
        vaccineCache.vaccines = VaccineRepo.Vaccine.values();
        vaccineCache.vaccineRepo = Lists.newArrayList(arrayList);

        vaccineCacheMap.put(Constants.CHILD_TYPE, vaccineCache);
        ReflectionHelpers.setStaticField(ImmunizationLibrary.class, "vaccineCacheMap", vaccineCacheMap);

        String expectedUrgentTrue = "(ec_client.dod IS NULL AND ec_client.date_removed is null AND ec_client.is_closed IS NOT '1' " +
                "AND ec_child_details.is_closed IS NOT '1') AND  ( ec_child_details.inactive IS NULL OR ec_child_details.inactive != 'true' )  " +
                "AND  ( ec_child_details.lost_to_follow_up IS NULL OR ec_child_details.lost_to_follow_up != 'true' )  AND  (  HepB = 'urgent' OR  PENTA_1 = 'urgent'  ) ";
        Assert.assertEquals(expectedUrgentTrue, DBQueryHelper.getFilterSelectionCondition(true));

        String expectedUrgentFalse = "(ec_client.dod IS NULL AND ec_client.date_removed is null AND ec_client.is_closed IS NOT '1' " +
                "AND ec_child_details.is_closed IS NOT '1') AND  ( ec_child_details.inactive IS NULL OR ec_child_details.inactive != 'true' )  " +
                "AND  ( ec_child_details.lost_to_follow_up IS NULL OR ec_child_details.lost_to_follow_up != 'true' )  " +
                "AND  (  HepB = 'urgent' OR  PENTA_1 = 'urgent'  OR  HepB = 'normal' OR  PENTA_1 = 'normal'  ) COLLATE NOCASE";
        Assert.assertEquals(expectedUrgentFalse, DBQueryHelper.getFilterSelectionCondition(false));
    }

    @Test
    public void testGetHomeRegisterCondition() {
        Assert.assertEquals("(ec_client.dod IS NULL AND ec_client.date_removed is null AND ec_client.is_closed IS NOT '1' AND ec_child_details.is_closed IS NOT '1')",
                DBQueryHelper.getHomeRegisterCondition());
    }

    @After
    public void tearDown() {
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", null);
        ReflectionHelpers.setStaticField(ImmunizationLibrary.class, "instance", null);
    }
}