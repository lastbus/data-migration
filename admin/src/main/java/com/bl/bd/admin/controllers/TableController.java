package com.bl.bd.admin.controllers;

import com.bl.bd.admin.dao.HiveService;
import com.bl.bd.admin.dao.SolrConnection;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.json.JSONArray;
import org.json.JSONObject;
//import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by MK33 on 2016/9/13.
 */
@Controller
public class TableController {

    /**
     * weather solr is refreshing data
     */
    boolean refesh = false;


    @RequestMapping(value = "/search/database/{dataBase}/table/{table}", method = RequestMethod.GET)
    @ResponseBody
    public String tables(@PathVariable String dataBase, @PathVariable String table) {
        System.out.println("/search/database/" + dataBase + "/table/" + table);
        SolrClient solrClient = SolrConnection.getInstance().getHttpClientConn();
        SolrQuery solrQuery = new SolrQuery();
        String normalDataBase = dataBase;
        if (!dataBase.startsWith("*")) normalDataBase = "*" + dataBase;
        if (!dataBase.endsWith("*")) normalDataBase += "*";
        String normalTable = table;
        if (!table.startsWith("*")) normalTable = "*" + table;
        if (!table.endsWith("*")) normalTable += "*";
        solrQuery.setFields("environment", "database", "table");
        solrQuery.add("q", normalDataBase + " AND " + normalTable);
        System.out.println(solrQuery.toString());
        JSONObject jsonObject = new JSONObject();
        try {
            QueryResponse response = solrClient.query(solrQuery);
            SolrDocumentList docList = response.getResults();
            int number = (int) docList.getNumFound();
            System.out.println("There are " + number + "  records.");
            jsonObject.put("total", number);
            JSONArray jsonArray = new JSONArray();
            Iterator<SolrDocument> iterator = docList.iterator();
            int i = 0;
            while (iterator.hasNext()) {
                JSONObject json = new JSONObject();
                SolrDocument solrDocument = iterator.next();
                json.put("env", solrDocument.getFirstValue("environment").toString());
                json.put("database", solrDocument.getFirstValue("database"));
                json.put("partition", solrDocument.getFirstValue("table").hashCode() % 2 == 0 ? true : false);
                json.put("table", solrDocument.getFirstValue("table"));
                jsonArray.put(json);
                i += 1;
            }
            jsonObject.put("count", i);
            jsonObject.put("status", "OK");
            jsonObject.put("tables", jsonArray);
            System.out.println("result: " + jsonObject);
            System.out.println("count: " + jsonArray.length());
            return jsonObject.toString();
        } catch (SolrServerException e) {
            System.out.println("SolrServerException");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IOException");
            e.printStackTrace();
        }
        jsonObject.put("status", "ERROR");
        return jsonObject.toString();
    }

    @RequestMapping(value = "/search/database/{database}/table/{table}/{currentPage}/{sizePerPage}", method = RequestMethod.GET)
    @ResponseBody
    public String tables(@PathVariable String database, @PathVariable String table, @PathVariable int currentPage, @PathVariable int sizePerPage) {
        System.out.println(String.format("/search/database/%s/table/%s/%s/%s", database, table, currentPage, sizePerPage));
        SolrClient solrClient = SolrConnection.getInstance().getHttpClientConn();
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setFields("environment", "database", "table");
        String normalDataBase = database;
        if (!database.startsWith("*")) normalDataBase = "*" + database;
        if (!database.endsWith("*")) normalDataBase += "*";
        String normalTable = table;
        if (!table.startsWith("*")) normalTable = "*" + table;
        if (!table.endsWith("*")) normalTable += "*";
        solrQuery.add("q", normalDataBase + " AND " + normalTable);
        solrQuery.setStart((currentPage - 1) * sizePerPage);
        solrQuery.setRows(sizePerPage);
        System.out.println(solrQuery.toString());
        JSONObject jsonObject = new JSONObject();
        try {
            QueryResponse queryResponse = null;
            queryResponse = solrClient.query(solrQuery);
            SolrDocumentList results = queryResponse.getResults();
            long total = results.getNumFound();
            jsonObject.put("total", total);
            JSONArray jsonArray = new JSONArray();
            
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }


    @RequestMapping(value = "/search/table/{table}", method = RequestMethod.GET)
    @ResponseBody
    public String tables(@PathVariable String table) {
        System.out.println("/search/table/" + table);
        return tables("*", table);
    }

    @RequestMapping(value = "/refresh", method = RequestMethod.GET)
    @ResponseBody
    public String reFreshSourceTableInfo() {
        System.out.println("/refresh");
        JSONObject json = new JSONObject();
        if (!refesh) {
            // 如果没有正在更新，开启另一个线程则更新
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    System.out.println("Begin to refresh " + new Date());
                    try {
                        HiveService.refreshIndex();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("refresh ends " + new Date());
                    refesh = false;
                }
            };
            Thread t = new Thread(runnable);
            t.start();
            refesh = true;
            json.put("result", "success");
        } else {
            // 如果已经正在更新，则返回
            json.put("result", "正在更新");
        }
        return json.toString();
    }

}
