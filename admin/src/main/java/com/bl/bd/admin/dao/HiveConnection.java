package com.bl.bd.admin.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by MK33 on 2016/9/21.
 */
public class HiveConnection {
    public static HiveConnection hiveConnection = null;
    public static String url = "jdbc:hive2://m78sit:10000/default;user=hive;password=123456";

    private HiveConnection() {}

    public static HiveConnection getInstance() {
        if (hiveConnection == null) {
            return new HiveConnection();
        } else {
            return hiveConnection;
        }
    }

    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.apache.hive.jdbc.HiveDriver");
        Connection conn = DriverManager.getConnection(url);
        return conn;
    }
}
