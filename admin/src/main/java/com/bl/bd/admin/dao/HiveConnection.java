package com.bl.bd.admin.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by MK33 on 2016/9/21.
 */
public class HiveConnection {
    public static HiveConnection hiveConnection = null;
    public String url = "jdbc:hive2://m78sit:10000/default;user=hive;password=123456";
    public String preUrl = "jdbc:hive2://10.201.48.101:10000/default;user=hive;password=nF7=8H*%";

    private HiveConnection() {}

    public static HiveConnection getInstance() {
        if (hiveConnection == null) {
            return new HiveConnection();
        } else {
            return hiveConnection;
        }
    }

    public Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.apache.hive.jdbc.HiveDriver");
        Connection conn = DriverManager.getConnection(url);
        return conn;
    }

    public Connection getConnect(String type) throws SQLException, ClassNotFoundException {
        Class.forName("org.apache.hive.jdbc.HiveDriver");
        Connection conn = DriverManager.getConnection(url(type));
        return conn;
    }

    public String url(String type) {
        String url = null;
        if (type.equalsIgnoreCase("pre")) {
            url = preUrl;
        } else {
            url = this.url;
        }
        return url;
    }
}
