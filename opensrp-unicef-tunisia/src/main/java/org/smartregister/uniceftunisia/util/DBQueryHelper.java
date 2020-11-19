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

    public static String getHomeRegisterCondition() {
        return AppConstants.TABLE_NAME.ALL_CLIENTS + "." + Constants.KEY.DATE_REMOVED + " IS NULL ";
    }

    public static String getFilterSelectionCondition(boolean urgentOnly) {

        final String AND = " AND ";
        final String OR = " OR ";
        final String IS_NULL_OR = " IS NULL OR ";
        final String TRUE = "'true'";

        String childDetailsTable = Utils.metadata().getRegisterQueryProvider().getChildDetailsTable();
        StringBuilder mainCondition = new StringBuilder(" ( " + AppConstants.TABLE_NAME.ALL_CLIENTS + "." + AppConstants.KEY.DATE_REMOVED + " is NULL OR " + AppConstants.TABLE_NAME.ALL_CLIENTS + "." + AppConstants.KEY.DATE_REMOVED + " = '' ) " +
                AND + " ( " + childDetailsTable + "." + Constants.CHILD_STATUS.INACTIVE + IS_NULL_OR + childDetailsTable + "." + Constants.CHILD_STATUS.INACTIVE + " != " + TRUE + " ) " +
                AND + " ( " + childDetailsTable + "." + Constants.CHILD_STATUS.LOST_TO_FOLLOW_UP + IS_NULL_OR + childDetailsTable + "." + Constants.CHILD_STATUS.LOST_TO_FOLLOW_UP + " != " + TRUE + " ) " +
                AND + " ( ");
        List<VaccineRepo.Vaccine> vaccines = ImmunizationLibrary.getVaccineCacheMap().get(Constants.CHILD_TYPE).vaccineRepo;

        vaccines.remove(VaccineRepo.Vaccine.bcg2);

        final String URGENT = "'" + AlertStatus.urgent.value() + "'";
        final String NORMAL = "'" + AlertStatus.normal.value() + "'";

        for (int i = 0; i < vaccines.size(); i++) {
            VaccineRepo.Vaccine vaccine = vaccines.get(i);
            if (i == vaccines.size() - 1) {
                mainCondition.append(" ").append(VaccinateActionUtils.addHyphen(vaccine.display())).append(" = ").append(URGENT).append(" ");
            } else {
                mainCondition.append(" ").append(VaccinateActionUtils.addHyphen(vaccine.display())).append(" = ").append(URGENT).append(OR);
            }
        }

        if (urgentOnly) {
            return mainCondition + " ) ";
        }

        mainCondition.append(OR);
        for (int i = 0; i < vaccines.size(); i++) {
            VaccineRepo.Vaccine vaccine = vaccines.get(i);
            if (i == vaccines.size() - 1) {
                mainCondition.append(" ").append(VaccinateActionUtils.addHyphen(vaccine.display())).append(" = ").append(NORMAL).append(" ");
            } else {
                mainCondition.append(" ").append(VaccinateActionUtils.addHyphen(vaccine.display())).append(" = ").append(NORMAL).append(OR);
            }
        }

        return mainCondition + " ) COLLATE NOCASE";
    }

    public static String getSortQuery() {
        return Utils.metadata().getRegisterQueryProvider().getDemographicTable() + "." + AppConstants.KEY.LAST_INTERACTED_WITH + " DESC ";
    }

}
