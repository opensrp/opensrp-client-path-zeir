package org.smartregister.uniceftunisia.util;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.immunization.domain.VaccineSchedule;
import org.smartregister.uniceftunisia.application.UnicefTunisiaApplication;
import org.smartregister.uniceftunisia.dao.ChildDao;

public class VaccineUtils {
    public static void refreshImmunizationSchedules(String caseId) {
        boolean prematureBaby = ChildDao.isPrematureBaby(caseId);
        String conditionalVaccine = null;

        if (prematureBaby) {
            conditionalVaccine = AppConstants.ConditionalVaccines.PRETERM_VACCINES;
        }

        if (!StringUtils.equalsIgnoreCase(conditionalVaccine, ImmunizationLibrary.getInstance().getCurrentConditionalVaccine())) {
            VaccineSchedule.setVaccineSchedules(null);
            ImmunizationLibrary.getInstance().setCurrentConditionalVaccine(conditionalVaccine);
            UnicefTunisiaApplication.getInstance().initOfflineSchedules();
        } else if (conditionalVaccine == null && ImmunizationLibrary.getInstance().getCurrentConditionalVaccine() == null) {
            UnicefTunisiaApplication.getInstance().initOfflineSchedules();
        }
    }
}
