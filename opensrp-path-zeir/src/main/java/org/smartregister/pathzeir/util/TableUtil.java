package org.smartregister.pathzeir.util;

public class TableUtil {

    public static String getAllClientColumn(String column) {
        return getColumn(AppConstants.TableNameConstants.ALL_CLIENTS, column);
    }

    public static String getMotherDetailsColumn(String column) {
        return getColumn(AppConstants.TableNameConstants.MOTHER_DETAILS, column);
    }

    public static String getChildDetailsColumn(String column) {
        return getColumn(AppConstants.TableNameConstants.CHILD_DETAILS, column);
    }

    private static String getColumn(String tableName, String column) {
        return String.format("%s.%s ", tableName, column);
    }
}
