package com.bl.bd.admin.dao;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;

/**
 * Created by MK33 on 2016/9/14.
 */
public class SolrConnection {
    private static SolrConnection solrConnection = null;
    private static HttpSolrClient solrClient = null;
    private static String url = "http://localhost:8983/solr/test-0";

    public SolrClient getHttpClientConn() {
//        if (solrClient == null) {
            solrClient = new HttpSolrClient.Builder(url).build();
//        }
        return solrClient;
    }

    private SolrConnection() {}

    public static SolrConnection getInstance() {
        if (solrClient == null) {
            return new SolrConnection();
        } else {
            return solrConnection;
        }
    }
}
