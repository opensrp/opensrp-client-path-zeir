package org.smartregister.uniceftunisia.dao;

import org.smartregister.child.dao.ChildDao;

import java.util.ArrayList;
import java.util.List;

public class AppChildDao extends ChildDao {

    public static boolean isPrematureBaby(String baseEntityID) {
        String sql = String.format("SELECT count(*) count\n" +
                "FROM ec_child_details\n" +
                "WHERE base_entity_id = '%s'\n" +
                "  AND pcv4_required is '1'", baseEntityID);

        DataMap<Integer> dataMap = cursor -> getCursorIntValue(cursor, "count");

        List<Integer> result = readData(sql, dataMap);
        if (result == null || result.size() != 1)
            return false;

        return result.get(0) > 0;
    }

    public static List<String> getChildrenAboveFiveYears() {
        String sql = "SELECT ec_client.base_entity_id\n" +
                "FROM ec_child_details\n" +
                "         join ec_client on ec_client.base_entity_id = ec_child_details.base_entity_id\n" +
                "WHERE cast(strftime('%Y-%m-%d %H:%M:%S', 'now') - strftime('%Y-%m-%d %H:%M:%S', ec_client.dob) as int) >= 5\n" +
                "  AND ec_client.is_closed = '0'\n" +
                "  AND ec_client.date_removed is null\n" +
                "  AND ec_child_details.is_closed = '0'";

        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "base_entity_id");

        List<String> result = readData(sql, dataMap);
        if (result == null) return new ArrayList<>();
        return result;
    }
}
