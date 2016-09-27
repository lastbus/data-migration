package com.bl.bd.admin.dao;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrResponse;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import java.io.IOException;
import java.util.Iterator;

/**
 * Created by MK33 on 2016/9/14.
 */
public class SolrConnection {
    private static SolrConnection solrConnection = null;
    private static String url = "http://localhost:8983/solr/hive";

    public SolrClient getHttpClientConn() {
        if (solrConnection == null) getInstance();
         return new HttpSolrClient.Builder(url).build();
    }

    private SolrConnection() {}

    public static SolrConnection getInstance() {
        if (solrConnection == null) {
            solrConnection = new SolrConnection();
            return solrConnection;
        } else {
            return solrConnection;
        }
    }


    public static void main(String[] args) {
        SolrClient solrClient = getInstance().getHttpClientConn();
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.add("q", "partition:dt*");
        try {
            QueryResponse response = solrClient.query(solrQuery);
            SolrDocumentList docs = response.getResults();
            Iterator<SolrDocument> iterator = docs.iterator();
            while (iterator.hasNext()){
                SolrDocument doc = iterator.next();
                System.out.println(doc.get("partition").getClass());
                System.out.println(doc.get("hasPartition").getClass());
            }

        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                solrClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
