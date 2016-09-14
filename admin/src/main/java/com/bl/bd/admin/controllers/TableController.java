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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Iterator;

/**
 * Created by MK33 on 2016/9/13.
 */
@Controller
public class TableController {


    @RequestMapping(value = "/search/database/{dataBase}/table/{table}", method = RequestMethod.GET)
    @ResponseBody
    public String tables(@PathVariable String dataBase, @PathVariable String table) {
        SolrClient solrClient = SolrConnection.getInstance().getHttpClientConn();
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.set("q", "payloads:" + table);
        solrQuery.setFields("title", "name", "payloads");
        try {
            QueryResponse response = solrClient.query(solrQuery);
            SolrDocumentList docList = response.getResults();
            int number = (int) docList.getNumFound();
            System.out.println("There are " + number + "  records.");
            JSONArray jsonArray = new JSONArray();
            Iterator<SolrDocument> iterator = docList.iterator();
            while (iterator.hasNext()) {
                JSONObject json = new JSONObject();
                SolrDocument solrDocument = iterator.next();
                json.put("name", solrDocument.getFirstValue("payloads").toString());
                json.put("payloads", solrDocument.getFirstValue("payloads"));
                json.put("partition", solrDocument.getFirstValue("payloads").hashCode() % 2 == 0 ? true : false );
                jsonArray.put(json);
            }
            return jsonArray.toString();
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < 5; i++) {
            JSONObject json = new JSONObject();
            json.put("name", String.valueOf(i));
            json.put("partition", (i % 2 == 0) ?true:false);
            jsonArray.put(json);
        }
        System.out.println(jsonArray);
        return jsonArray.toString();
    }

    @RequestMapping(value = "/search/table/{table}", method = RequestMethod.GET)
    @ResponseBody
    public String tables(@PathVariable String table) {
        return tables("*", table);
    }

    @RequestMapping(value = "/refresh", method = RequestMethod.GET)
    @ResponseBody
    public String reFreshSourceTableInfo() {
        JSONObject json = new JSONObject();
        try {
            HiveService.refreshIndex();
            json.put("result", "success");
        } catch (IOException e) {
            e.printStackTrace();
            json.put("result", "error");
        }
        return json.toString();
    }

}
