package org.smartregister.uniceftunisia.processor;

import android.content.ContentValues;
import android.content.Context;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.util.ChildDbUtils;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.Utils;
import org.smartregister.domain.Client;
import org.smartregister.domain.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.jsonmapping.Table;
import org.smartregister.growthmonitoring.domain.Height;
import org.smartregister.growthmonitoring.domain.Weight;
import org.smartregister.growthmonitoring.repository.HeightRepository;
import org.smartregister.growthmonitoring.repository.WeightRepository;
import org.smartregister.immunization.domain.ServiceRecord;
import org.smartregister.immunization.domain.ServiceSchedule;
import org.smartregister.immunization.domain.ServiceType;
import org.smartregister.immunization.domain.VaccineSchedule;
import org.smartregister.immunization.repository.RecurringServiceRecordRepository;
import org.smartregister.immunization.repository.RecurringServiceTypeRepository;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.uniceftunisia.application.UnicefTunisiaApplication;

import java.util.Arrays;

@RunWith(PowerMockRunner.class)
@PrepareForTest({UnicefTunisiaApplication.class, Utils.class, VaccineSchedule.class, ServiceSchedule.class, ChildDbUtils.class, ChildLibrary.class})
public class AppClientProcessorForJavaTest {

    @Mock
    private UnicefTunisiaApplication unicefTunisiaApplication;

    @Mock
    private org.smartregister.Context openSrpContext;

    @Mock
    private HeightRepository heightRepository;

    @Mock
    private WeightRepository weightRepository;

    @Mock
    private ContentValues contentValues;

    @Captor
    private ArgumentCaptor<Weight> processWeightArgumentCaptor;

    @Captor
    private ArgumentCaptor<Height> processHeightArgumentCaptor;

    @Captor
    private ArgumentCaptor<ServiceRecord> recordServiceArgumentCaptor;

    @Captor
    private ArgumentCaptor addDetailsRepoArgumentCaptor;

    @Mock
    private RecurringServiceTypeRepository recurringServiceTypeRepository;

    @Mock
    private RecurringServiceRecordRepository recurringServiceRecordRepository;

    private AppClientProcessorForJava processorForJava;

    @Mock
    private DetailsRepository detailsRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        processorForJava = Mockito.spy(new AppClientProcessorForJava(Mockito.mock(Context.class)));
    }

    @Test
    public void processHeightWithEventClientNullShouldReturn() throws Exception {
        Object[] params = {null, null, true};
        Whitebox.invokeMethod(processorForJava, "processHeight", params);
        Mockito.verify(heightRepository, Mockito.atLeast(0)).add(Mockito.any(Height.class));
    }

    @Test
    public void processHeightWithTableNullShouldReturn() throws Exception {
        Object[] params = {new EventClient(new Event(), new Client("23")), null, true};
        Whitebox.invokeMethod(processorForJava, "processHeight", params);
        Mockito.verify(heightRepository, Mockito.atLeast(0)).add(Mockito.any(Height.class));
    }

    @Test
    public void processHeightWithValidEventClientAndTableShouldReturnTrue() throws Exception {
        PowerMockito.mockStatic(UnicefTunisiaApplication.class);
        PowerMockito.when(UnicefTunisiaApplication.getInstance()).thenReturn(unicefTunisiaApplication);
        PowerMockito.when(unicefTunisiaApplication.heightRepository()).thenReturn(heightRepository);
        Mockito.when(processorForJava.processCaseModel(ArgumentMatchers.any(EventClient.class), ArgumentMatchers.any(Table.class))).thenReturn(contentValues);
        Mockito.when(contentValues.size()).thenReturn(7);
        Mockito.when(contentValues.getAsString(HeightRepository.DATE)).thenReturn("2019-09-27 09:45:44");
        Mockito.when(contentValues.getAsString(HeightRepository.BASE_ENTITY_ID)).thenReturn("234");
        Mockito.when(contentValues.containsKey(HeightRepository.CM)).thenReturn(true);
        Mockito.when(contentValues.getAsString(HeightRepository.CM)).thenReturn("230");
        Mockito.when(contentValues.getAsString(HeightRepository.ANMID)).thenReturn("provider");
        Mockito.when(contentValues.getAsString(HeightRepository.LOCATIONID)).thenReturn("lombwe");
        Mockito.when(contentValues.containsKey(HeightRepository.Z_SCORE)).thenReturn(true);
        Mockito.when(contentValues.getAsString(HeightRepository.Z_SCORE)).thenReturn("45.0");
        Mockito.when(contentValues.getAsString(HeightRepository.CREATED_AT)).thenReturn("2019-09-27 09:45:44");
        Table table = new Table();
        table.name = "heights";
        Event event = new Event();
        event.setEventId("231");
        event.setFormSubmissionId("343");
        Client client = new Client("234");
        EventClient eventClient = new EventClient(event, client);

        Whitebox.invokeMethod(processorForJava, "processHeight", eventClient, table, false);
        Mockito.verify(heightRepository).add(processHeightArgumentCaptor.capture());
        Height resultHeightobj = processHeightArgumentCaptor.getValue();
        Assert.assertEquals(java.util.Optional.of(0).get(), resultHeightobj.getOutOfCatchment());
        Assert.assertEquals(Float.valueOf("230.0"), resultHeightobj.getCm());
        Assert.assertEquals(Double.valueOf("45.0"), resultHeightobj.getZScore());
        Assert.assertEquals("lombwe", resultHeightobj.getLocationId());
        Assert.assertEquals(HeightRepository.TYPE_Synced, resultHeightobj.getSyncStatus());
        Assert.assertEquals(Utils.getDate("2019-09-27 09:45:44"), resultHeightobj.getDate());
        Assert.assertEquals(Utils.getDate("2019-09-27 09:45:44"), resultHeightobj.getCreatedAt());
    }

    @Test
    public void processServiceWithEventClientNullShouldReturn() throws Exception {
        Object[] params = {null, null};
        Whitebox.invokeMethod(processorForJava, "processService", params);
        Mockito.verify(recurringServiceRecordRepository, Mockito.atLeast(0)).add(Mockito.any(ServiceRecord.class));
    }

    @Test
    public void processServiceWithTableNullShouldReturn() throws Exception {
        Object[] params = {new EventClient(new Event(), new Client("23")), null};
        Whitebox.invokeMethod(processorForJava, "processService", params);
        Mockito.verify(recurringServiceRecordRepository, Mockito.atLeast(0)).add(Mockito.any(ServiceRecord.class));
    }

    @Test
    public void processServiceWithNameNotHavingITNShouldReturnTrue() throws Exception {
        PowerMockito.mockStatic(UnicefTunisiaApplication.class);
        PowerMockito.when(UnicefTunisiaApplication.getInstance()).thenReturn(unicefTunisiaApplication);
        PowerMockito.when(unicefTunisiaApplication.recurringServiceTypeRepository()).thenReturn(recurringServiceTypeRepository);
        PowerMockito.when(unicefTunisiaApplication.recurringServiceRecordRepository()).thenReturn(recurringServiceRecordRepository);
        ServiceType serviceTypeDeworming2 = new ServiceType();
        serviceTypeDeworming2.setId(1L);
        serviceTypeDeworming2.setName("Deworming_2");
        serviceTypeDeworming2.setPrerequisite("prerequisite|Deworming 1");
        serviceTypeDeworming2.setPreOffset("+6m");
        serviceTypeDeworming2.setUnits("200 mg");
        serviceTypeDeworming2.setDateEntity("encounter");

        ServiceType serviceTypeDeworming3 = new ServiceType();
        serviceTypeDeworming3.setName("Deworming_3");
        serviceTypeDeworming3.setPrerequisite("prerequisite|Deworming 2");
        serviceTypeDeworming3.setPreOffset("+6m");
        serviceTypeDeworming3.setUnits("400 mg");
        serviceTypeDeworming3.setDateEntity("encounter");

        Mockito.when(recurringServiceTypeRepository.searchByName("Deworming 1")).thenReturn(Arrays.asList(serviceTypeDeworming2, serviceTypeDeworming3));
        Mockito.when(processorForJava.processCaseModel(ArgumentMatchers.any(EventClient.class), ArgumentMatchers.any(Table.class))).thenReturn(contentValues);
        Mockito.when(contentValues.size()).thenReturn(7);
        Mockito.when(contentValues.getAsString(HeightRepository.ANMID)).thenReturn("provider");
        Mockito.when(contentValues.getAsString(HeightRepository.LOCATIONID)).thenReturn("lombwe");
        Mockito.when(contentValues.getAsString(RecurringServiceTypeRepository.NAME)).thenReturn("Deworming_1");
        Mockito.when(contentValues.getAsString(RecurringServiceRecordRepository.DATE)).thenReturn("2019-09-27 09:45:44");

        Mockito.when(contentValues.getAsString(HeightRepository.CREATED_AT)).thenReturn("2019-09-27 09:45:44");

        Event event = new Event();
        event.setEventId("231");
        event.setFormSubmissionId("1234");

        Table table = new Table();
        table.name = "recurring_service_records";
        Object[] params = {new EventClient(event, new Client("23")), table};
        Whitebox.invokeMethod(processorForJava, "processService", params);

        Mockito.verify(recurringServiceRecordRepository).add(recordServiceArgumentCaptor.capture());
        ServiceRecord resultServiceRecord = recordServiceArgumentCaptor.getValue();
        Assert.assertEquals(Utils.getDate("2019-09-27 09:45:44"), resultServiceRecord.getDate());
        Assert.assertEquals(java.util.Optional.of(1L).get(), resultServiceRecord.getRecurringServiceId());
        Assert.assertEquals("Deworming 1", resultServiceRecord.getName());
        Assert.assertEquals(RecurringServiceRecordRepository.TYPE_Synced, resultServiceRecord.getSyncStatus());

        Assert.assertEquals("231", resultServiceRecord.getEventId());
        Assert.assertEquals("1234", resultServiceRecord.getFormSubmissionId());
        Assert.assertEquals("1234", resultServiceRecord.getFormSubmissionId());
    }

    @Test
    @Ignore("Fix this: Mocking ChildLibrary")
    public void processBCScarEventWithValidEventClientShouldPassCorrectArgsToDetailsRepo() throws Exception {
        PowerMockito.mockStatic(UnicefTunisiaApplication.class);
        PowerMockito.when(UnicefTunisiaApplication.getInstance()).thenReturn(unicefTunisiaApplication);
        Mockito.when(unicefTunisiaApplication.context()).thenReturn(openSrpContext);
        Mockito.when(openSrpContext.detailsRepository()).thenReturn(detailsRepository);
        Event event = new Event();
        event.setBaseEntityId("23213");
        event.setEventDate(new DateTime());
        Client client = new Client("23213");
        PowerMockito.when(ChildLibrary.getInstance()).thenReturn(Mockito.spy(ChildLibrary.getInstance()));
        Whitebox.invokeMethod(processorForJava, "processBCGScarEvent", new EventClient(event, client));
        Mockito.verify(detailsRepository).add((String) addDetailsRepoArgumentCaptor.capture(), (String) addDetailsRepoArgumentCaptor.capture(),
                (String) addDetailsRepoArgumentCaptor.capture(), (Long) addDetailsRepoArgumentCaptor.capture());
        Assert.assertEquals("23213", addDetailsRepoArgumentCaptor.getAllValues().get(0));
        Assert.assertEquals(Constants.SHOW_BCG_SCAR, addDetailsRepoArgumentCaptor.getAllValues().get(1));
        Assert.assertEquals(String.valueOf(event.getEventDate().getMillis()), addDetailsRepoArgumentCaptor.getAllValues().get(2));
        Assert.assertEquals(event.getEventDate().getMillis(), addDetailsRepoArgumentCaptor.getAllValues().get(3));
    }

    @Test
    public void processWeightWithEventClientNullShouldReturn() throws Exception {
        Object[] params = {null, null, true};
        Whitebox.invokeMethod(processorForJava, "processWeight", params);
        Mockito.verify(weightRepository, Mockito.atLeast(0)).add(Mockito.any(Weight.class));
    }

    @Test
    public void testProcessWeightWithTableNull() throws Exception {
        Object[] params = {new EventClient(new Event(), new Client("23")), null, true};
        Whitebox.invokeMethod(processorForJava, "processWeight", params);
        Mockito.verify(weightRepository, Mockito.atLeast(0)).add(Mockito.any(Weight.class));
    }

    @Test
    public void processWeightWithValidEventClientAndTableShouldReturnTrue() throws Exception {
        PowerMockito.mockStatic(UnicefTunisiaApplication.class);
        PowerMockito.when(UnicefTunisiaApplication.getInstance()).thenReturn(unicefTunisiaApplication);
        PowerMockito.when(unicefTunisiaApplication.weightRepository()).thenReturn(weightRepository);
        Mockito.when(processorForJava.processCaseModel(ArgumentMatchers.any(EventClient.class), ArgumentMatchers.any(Table.class))).thenReturn(contentValues);
        Mockito.when(contentValues.size()).thenReturn(7);
        Mockito.when(contentValues.getAsString(WeightRepository.DATE)).thenReturn("2019-09-27 09:45:44");
        Mockito.when(contentValues.getAsString(WeightRepository.BASE_ENTITY_ID)).thenReturn("234");
        Mockito.when(contentValues.containsKey(WeightRepository.KG)).thenReturn(true);
        Mockito.when(contentValues.getAsString(WeightRepository.KG)).thenReturn("20");
        Mockito.when(contentValues.getAsString(WeightRepository.ANMID)).thenReturn("provider");
        Mockito.when(contentValues.getAsString(WeightRepository.LOCATIONID)).thenReturn("lombwe");
        Mockito.when(contentValues.containsKey(WeightRepository.Z_SCORE)).thenReturn(true);
        Mockito.when(contentValues.getAsString(WeightRepository.Z_SCORE)).thenReturn("45.0");
        Mockito.when(contentValues.getAsString(WeightRepository.CREATED_AT)).thenReturn("2019-09-27 09:45:44");
        Table table = new Table();
        table.name = "weights";
        Event event = new Event();
        event.setEventId("231");
        event.setFormSubmissionId("343");
        Client client = new Client("234");
        EventClient eventClient = new EventClient(event, client);

        Whitebox.invokeMethod(processorForJava, "processWeight", eventClient, table, false);
        Mockito.verify(weightRepository).add(processWeightArgumentCaptor.capture());
        Weight resultWeightObj = processWeightArgumentCaptor.getValue();
        Assert.assertEquals(java.util.Optional.of(0).get(), resultWeightObj.getOutOfCatchment());
        Assert.assertEquals(Float.valueOf("20.0"), resultWeightObj.getKg());
        Assert.assertEquals(Double.valueOf("45.0"), resultWeightObj.getZScore());
        Assert.assertEquals("lombwe", resultWeightObj.getLocationId());
        Assert.assertEquals(WeightRepository.TYPE_Synced, resultWeightObj.getSyncStatus());
        Assert.assertEquals(Utils.getDate("2019-09-27 09:45:44"), resultWeightObj.getDate());
        Assert.assertEquals(Utils.getDate("2019-09-27 09:45:44"), resultWeightObj.getCreatedAt());
    }
}