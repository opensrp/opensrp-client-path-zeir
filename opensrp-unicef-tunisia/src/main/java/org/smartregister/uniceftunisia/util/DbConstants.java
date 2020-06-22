package org.smartregister.uniceftunisia.util;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 13-03-2020.
 */
public interface DbConstants {

    interface Table {
        interface Hia2IndicatorsRepository {

            String TABLE_NAME = "indicators";
            String ID = "_id";
            String INDICATOR_CODE = "indicator_code";
            String DESCRIPTION = "description";
        }

        interface MonthlyTalliesRepository {
            String TABLE_NAME = "monthly_tallies";
            String ID = "_id";
            String PROVIDER_ID = "provider_id";
            String INDICATOR_CODE = "indicator_code";
            String VALUE = "value";
            String MONTH = "month";
            String EDITED = "edited";
            String DATE_SENT = "date_sent";
            String INDICATOR_GROUPING = "indicator_grouping";
            String UPDATED_AT = "updated_at";
            String CREATED_AT = "created_at";
        }
    }
}
