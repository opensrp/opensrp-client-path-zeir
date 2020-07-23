package org.smartregister.uniceftunisia.dao;

import org.smartregister.dao.AbstractDao;

import java.util.List;

public class ChildDao extends AbstractDao {

    public static boolean isPrematureBaby(String baseEntityID) {
        String sql = String.format("select count(*) count\n" +
                "from ec_child_details\n" +
                "where base_entity_id = '%s'\n" +
                "  AND pcv4_required is '1'", baseEntityID);

        DataMap<Integer> dataMap = cursor -> getCursorIntValue(cursor, "count");

        List<Integer> res = readData(sql, dataMap);
        if (res == null || res.size() != 1)
            return false;

        return res.get(0) > 0;
    }
}
