package org.smartregister.uniceftunisia;

import com.google.common.collect.Lists;

import org.smartregister.immunization.domain.jsonmapping.Vaccine;
import org.smartregister.immunization.domain.jsonmapping.VaccineGroup;
import org.smartregister.uniceftunisia.application.UnicefTunisiaApplication;

import java.util.ArrayList;
import java.util.List;

public class TestUnicefTunisiaApplication extends UnicefTunisiaApplication {

    @Override
    public void onCreate() {

        Vaccine vaccine;
        Vaccine vaccine2;
        VaccineGroup vaccineGroup;
        List<VaccineGroup> vaccineGroups = new ArrayList<>();

        vaccine = new Vaccine();
        vaccine.setName("opv0");


        vaccine2 = new Vaccine();
        vaccine2.setName("bcg");

        vaccineGroup = new VaccineGroup();
        vaccineGroup.vaccines = Lists.newArrayList(vaccine, vaccine2);

        vaccineGroups.add(vaccineGroup);

        vaccine = new Vaccine();
        vaccine.setName("opv1");

        vaccine2 = new Vaccine();
        vaccine2.setName("opv2");

        vaccineGroup = new VaccineGroup();
        vaccineGroup.vaccines = Lists.newArrayList(vaccine, vaccine2);

        vaccineGroups.add(vaccineGroup);

        vaccine = new Vaccine();
        vaccine.setName("penta1");

        vaccine2 = new Vaccine();
        vaccine2.setName("mr1");

        vaccineGroup = new VaccineGroup();
        vaccineGroup.vaccines = Lists.newArrayList(vaccine, vaccine2);

        vaccineGroups.add(vaccineGroup);

        vaccine = new Vaccine();
        vaccine.setName("penta2");

        vaccine2 = new Vaccine();
        vaccine2.setName("mr2");

        vaccineGroup = new VaccineGroup();
        vaccineGroup.vaccines = Lists.newArrayList(vaccine, vaccine2);

        vaccineGroups.add(vaccineGroup);

        setVaccineGroups(vaccineGroups);

        super.onCreate();
        setTheme(R.style.Theme_AppCompat); //or just R.style.Theme_AppCompat
    }

    @Override
    protected void fixHardcodedVaccineConfiguration() {
        //Override
    }

}
