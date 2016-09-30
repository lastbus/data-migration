package com.bl.bd.admin.controllers;

import com.bl.bd.admin.dao.HiveConnection;
import com.bl.bd.admin.util.FileUtil;
import com.bl.bd.admin.util.HiveSqlUtil;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by MK33 on 2016/9/30.
 */
@Controller
public class HiveTableController {

    Logger logger = Logger.getLogger(HiveTableController.class.getName());

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    public String migration(@PathVariable String sourceTable, @PathVariable String targetCluster, @PathVariable String targetTable) {
        logger.debug(String.format("/%s/%s/%s", sourceTable, targetCluster, targetTable));
        JSONObject json = new JSONObject();
        json.put("status", "error");
        // 将源表导出到目标集群，然后再导入目标集群的 hive 表。
        Connection sourceHiveConnection = null;
        Connection targetHiveConnection = null;
        Statement sourceHiveStmt = null;
        Statement targetHiveStmt = null;
        try {
            // 得到一个连接
            sourceHiveConnection = HiveConnection.getInstance().getConnection();
            targetHiveConnection = HiveConnection.getInstance().getConnection();
            // 临时数据存放的文件名
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String tmpFileName = FileUtil.getTempWorkPath(sdf.format(new Date()) + "_" + sourceTable);
            String exportToClusterSql = HiveSqlUtil.exportSql(sourceTable, tmpFileName);
            logger.debug(exportToClusterSql);
            // 将数据导出到目标集群临时目录
            sourceHiveStmt = sourceHiveConnection.createStatement();
            boolean export = sourceHiveStmt.execute(exportToClusterSql);
            logger.debug("export data success");
            logger.debug(export);
            if (export) {
                ResultSet rs = sourceHiveStmt.getResultSet();
                while (rs.next()) {
                    logger.debug(rs.getString(1));
                }
            }

            String hiveImportSql = null;
            if (sourceTable == targetTable) {
                hiveImportSql = HiveSqlUtil.importSql(tmpFileName);
            } else {
                hiveImportSql = HiveSqlUtil.importSql(tmpFileName, targetTable);
            }

            logger.debug(hiveImportSql);
            // 将数据从临时目录导入目标集群 hive 表中
            boolean importTable = sourceHiveStmt.execute(hiveImportSql);
            logger.debug("import data success");
            if (importTable) {
                ResultSet rs = sourceHiveStmt.getResultSet();
                while (rs.next()) {
                    logger.debug(rs.getString(1));
                }
            }

            logger.info("export and import success");
            json.put("status", "ok");
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        } finally {
            if (sourceHiveStmt != null) try {
                sourceHiveStmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (sourceHiveConnection != null) try {
                sourceHiveConnection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return json.toString();
    }






}
