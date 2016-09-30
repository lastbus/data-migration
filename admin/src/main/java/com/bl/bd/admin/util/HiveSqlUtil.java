package com.bl.bd.admin.util;

/**
 * Created by MK33 on 2016/9/30.
 */
public class HiveSqlUtil {

    public static String exportSql(String from, String to) {
        return String.format("export table %s to %s", from, to);
    }

    public static String importSql(String from) {
        return String.format("import from %s ", from);
    }

    public static String importSql(String from, String table) {
        return String.format("import table %s from %s ", from, table);
    }

    public static String importSql(String from, String database, String table) {
        return String.format("use %s; import table %s from %s ", database, table, from);
    }

}
