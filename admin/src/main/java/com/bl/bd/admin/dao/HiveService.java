package com.bl.bd.admin.dao;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;

/**
 * Created by MK33 on 2016/9/13.
 */
public class HiveService {

    private static Logger logger = Logger.getLogger(HiveService.class.getName());
    private static String propsFile = "hive.properties";

    /** refresh hive table information, then index into solr */
    public static void refreshIndex() throws IOException {
        Properties props = null;
        try {
            props = loadProperties(propsFile);
        } catch (IOException e) {
            logger.error("load " + propsFile + "  encounter error : " + e);
            e.printStackTrace();
            throw new IOException("load " + propsFile + " encounter error!");
        }
        if (props == null) {
            throw new IOException("cannot get configuration file : " + propsFile);
        }
        SolrConnection solrConn = SolrConnection.getInstance();
        SolrClient solrClient = solrConn.getHttpClientConn();
        for (Object type : props.keySet()) {
            String url = props.getProperty(type.toString());
            try {
                Map<String, List<String>> tables = fetchTables(url);
                for (String dataBase : tables.keySet()) {
                    List<String> listTable = tables.get(dataBase);
                    if (!listTable.isEmpty()) {
                        for (String t : listTable) {
                            SolrInputDocument solrInputDocument = new SolrInputDocument();
                            solrInputDocument.addField("id", type + "_" + dataBase + "_" + t);
                            solrInputDocument.addField("title", type);
                            solrInputDocument.addField("name", dataBase);
                            solrInputDocument.addField("payloads", t);
                            solrClient.add(solrInputDocument);
                            solrClient.commit();
                            logger.info("indexed " + t);
                        }
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (SolrServerException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * load properties files
     */
    public static Properties loadProperties(String fileName) throws IOException {
        InputStream inputStream = HiveService.class.getClassLoader().getResourceAsStream(fileName);
        if (inputStream == null) return null;
        Properties properties = new Properties();
        properties.load(inputStream);
        return properties;
    }

    /**
     * get tables from a hive url connection
     */
    public static Map<String, List<String>> fetchTables(String url) throws ClassNotFoundException, SQLException {
        logger.info(url);
        Map<String, List<String>> map = new HashMap<>();

        Class.forName("org.apache.hive.jdbc.HiveDriver");
        Connection conn = DriverManager.getConnection(url);
        Statement stmt = conn.createStatement();
        String databases = "show databases";
        ResultSet rs = stmt.executeQuery(databases);
        while (rs.next()) {
            logger.debug(rs.getString(1));
            map.put(rs.getString(1), null);
        }
        for (String key : map.keySet()) {
            List<String> list = new ArrayList<>();
            stmt.execute("use " + key);
            rs = stmt.executeQuery("show tables");
            while (rs.next()) {
                logger.debug(rs.getString(1));
                list.add(rs.getString(1));
            }
            map.put(key, list);
            logger.debug(key + "  has tables :  " + list.size());
        }

        return map;

    }


    public static void main(String[] args) throws ClassNotFoundException, SQLException {
//        try {
//            refreshIndex();
//        } catch (IOException e) {
//            System.out.println("refresh encounter error");
//            e.printStackTrace();
//        }

        query();
    }


    public static void index() throws ClassNotFoundException, SQLException {
        Map<String, List<String>> map = new HashMap<>();

        String hiveUrl = "jdbc:hive2://m78sit:10000/default;user=hive;password=123456";
        Class.forName("org.apache.hive.jdbc.HiveDriver");
        Connection conn = DriverManager.getConnection(hiveUrl);
        Statement stmt = conn.createStatement();
        String databases = "show databases";
        ResultSet rs = stmt.executeQuery(databases);

        while (rs.next()) {
            System.out.println(rs.getString(1));
            map.put(rs.getString(1), null);
        }

        for (String key : map.keySet()) {
            List<String> list = new ArrayList<>();
            stmt.execute("use " + key);
            rs = stmt.executeQuery("show tables");
            while (rs.next()) {
                list.add(rs.getString(1));
            }
            map.put(key, list);
        }

        for (String key : map.keySet()) {
            System.out.println(key + " : ");
            System.out.print("\t");
            for (String table : map.get(key)) {
                System.out.print(table + ", ");
            }
            System.out.println();
        }

    }

    public static void query() {
        SolrClient solrClient = SolrConnection.getInstance().getHttpClientConn();
        SolrQuery solrQuery = new SolrQuery();
//        solrQuery.setQuery("s12_api_token_index_his*");
        solrQuery.setFields("title", "name", "payloads");
        solrQuery.set("q", "payloads:s12_api_token_index_his");
        try {
            QueryResponse response = solrClient.query(solrQuery);
            SolrDocumentList docList = response.getResults();
            System.out.print(docList.toString());
            int number = (int) docList.getNumFound();
            System.out.println("There are " + number + "  records.");
            JSONArray jsonArray = new JSONArray();
            Iterator<SolrDocument> iterator = docList.iterator();
            while (iterator.hasNext()) {
                JSONObject json = new JSONObject();
                SolrDocument solrDocument = iterator.next();
                json.put("name", solrDocument.getFirstValue("name").toString());
                json.put("payloads", solrDocument.getFirstValue("payloads"));
                json.put("partition", solrDocument.getFirstValue("payloads").hashCode() % 2 == 0 ? true : false );
                jsonArray.put(json);
            }
            System.out.println(jsonArray.toString());

        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
