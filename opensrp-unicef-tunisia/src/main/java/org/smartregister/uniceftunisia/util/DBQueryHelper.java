package org.smartregister.uniceftunisia.util;

import org.smartregister.child.util.Constants;
import org.smartregister.child.util.Utils;
import org.smartregister.domain.AlertStatus;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.util.VaccinateActionUtils;

import java.util.List;

/**
 * Created by ndegwamartin on 2019-05-30.
 */

public class DBQueryHelper {

    public static final String getHomeRegisterCondition() {
        return AppConstants.TABLE_NAME.CHILD + "." + Constants.KEY.DATE_REMOVED + " IS NULL ";
    }

    public static String getFilterSelectionCondition(boolean urgentOnly) {

        final String AND = " AND ";
        final String OR = " OR ";
        final String IS_NULL_OR = " IS NULL OR ";
        final String TRUE = "'true'";

        String childDetailsTable = Utils.metadata().getRegisterQueryProvider().getChildDetailsTable();
        String mainCondition = " ( " + Constants.KEY.DOD + " is NULL OR " + Constants.KEY.DOD + " = '' ) " +
                AND + " ( " + childDetailsTable + "." + Constants.CHILD_STATUS.INACTIVE + IS_NULL_OR + childDetailsTable + "." + Constants.CHILD_STATUS.INACTIVE + " != " + TRUE + " ) " +
                AND + " ( " + childDetailsTable + "." + Constants.CHILD_STATUS.LOST_TO_FOLLOW_UP + IS_NULL_OR + childDetailsTable + "." + Constants.CHILD_STATUS.LOST_TO_FOLLOW_UP + " != " + TRUE + " ) " +
                AND + " ( ";
        List<VaccineRepo.Vaccine> vaccines = ImmunizationLibrary.getVaccineCacheMap().get(Constants.CHILD_TYPE).vaccineRepo;

        vaccines.remove(VaccineRepo.Vaccine.bcg2);

        final String URGENT = "'" + AlertStatus.urgent.value() + "'";
        final String NORMAL = "'" + AlertStatus.normal.value() + "'";

        for (int i = 0; i < vaccines.size(); i++) {
            VaccineRepo.Vaccine vaccine = vaccines.get(i);
            if (i == vaccines.size() - 1) {
                mainCondition += " " + VaccinateActionUtils.addHyphen(vaccine.display()) + " = " + URGENT + " ";
            } else {
                mainCondition += " " + VaccinateActionUtils.addHyphen(vaccine.display()) + " = " + URGENT + OR;
            }
        }

        if (urgentOnly) {
            return mainCondition + " ) ";
        }

        mainCondition += OR;
        for (int i = 0; i < vaccines.size(); i++) {
            VaccineRepo.Vaccine vaccine = vaccines.get(i);
            if (i == vaccines.size() - 1) {
                mainCondition += " " + VaccinateActionUtils.addHyphen(vaccine.display()) + " = " + NORMAL + " ";
            } else {
                mainCondition += " " + VaccinateActionUtils.addHyphen(vaccine.display()) + " = " + NORMAL + OR;
            }
        }

        return mainCondition + " ) ";
    }

    public static String getSortQuery() {
        return Utils.metadata().getRegisterQueryProvider().getDemographicTable() + "." + AppConstants.KEY.LAST_INTERACTED_WITH + " DESC ";
    }

    public static String ancDueOverdueFilter(boolean overdue) {
        if (overdue) {
            return "(contact_status IS NULL OR contact_status != 'active') \n" +
                    "AND DATE('now') > DATE(next_contact_date, '+6 day') AND (edd IS NOT NULL AND DATE(edd) > DATE('now')) ";
        } else {
            return "(contact_status IS NULL OR contact_status != 'active') AND DATE('now') > DATE(next_contact_date, '-1 day') AND DATE('now') < DATE(next_contact_date, '+7 day') AND (edd IS NOT NULL AND DATE(edd) > DATE('now'))";
        }
    }
}
