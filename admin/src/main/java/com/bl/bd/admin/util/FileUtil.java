package com.bl.bd.admin.util;

/**
 * Created by MK33 on 2016/9/30.
 */
public class FileUtil {



    public static String getTempWorkPath() {
        return "/tmp/migration/hive";
    }

    public static String getTempWorkPath(String name) {
        return getTempWorkPath() + "/" + name;
    }

}
