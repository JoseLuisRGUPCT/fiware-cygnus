/**
 * Copyright 2014 Telefonica Investigación y Desarrollo, S.A.U
 *
 * This file is part of fiware-connectors (FI-WARE project).
 *
 * fiware-connectors is free software: you can redistribute it and/or modify it under the terms of the GNU Affero
 * General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * fiware-connectors is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with fiware-connectors. If not, see
 * http://www.gnu.org/licenses/.
 *
 * For those usages not covered by the GNU Affero General Public License please contact with
 * francisco.romerobueno at telefonica dot com
 */

package es.tid.fiware.fiwareconnectors.ckanprotocol.backends.ckan;

import es.tid.fiware.fiwareconnectors.ckanprotocol.http.HttpClientFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author frb
 */
public class CKANBackend {
    
    private Logger logger;
    private String ckanHost;
    private String ckanPort;
    private String apiKey;
    private boolean ssl;
    private HttpClientFactory httpClientFactory;
    
    /**
     * Constructor.
     * @param apiKey
     */
    public CKANBackend(String ckanHost, String ckanPort, boolean ssl, String apiKey) {
        this.ckanHost = ckanHost;
        this.ckanPort = ckanPort;
        this.logger = Logger.getLogger(CKANBackend.class);
        this.apiKey = apiKey;
        this.ssl = ssl;
        this.httpClientFactory = new HttpClientFactory(ssl);
    } // CKANBackend
    
    /**
     * Gets the package identifiers/names within a given organization id/name.
     * @param orgId
     * @return The package identifiers/names within the given organization id/name
     */
    public List<String> getPackages(String orgId) {
        logger.info("Getting the packages within " + orgId + " organization");

        try {
            List<String> resIds = new ArrayList<String>();
            String url = "http" + (ssl ? "s" : "") + "://" + ckanHost + ":" + ckanPort
                    + "/api/3/action/organization_show?id=" + orgId;
            CKANResponse resp = doCKANRequest("GET", url, "");
            JSONObject result = (JSONObject) resp.getJsonObject().get("result");
            JSONArray pkgs = (JSONArray) result.get("packages");
            
            for (int i = 0; i < pkgs.size(); i++) {
                JSONObject pkg = (JSONObject) pkgs.get(i);
                resIds.add((String) pkg.get("id"));
            } // for
            
            return resIds;
        } catch (Exception e) {
            return null;
        } // try catch
    } // getPackages
    
    /**
     * Gets the resource identifiers/names within a given package id/name.
     * @param pkgId
     * @return The resource identifiers/names within the given package id/name
     */
    public List<String> getResources(String pkgId) {
        logger.info("Getting the resources within " + pkgId + " + package");
        
        try {
            List<String> resIds = new ArrayList<String>();
            String url = "http" + (ssl ? "s" : "") + "://" + ckanHost + ":" + ckanPort
                    + "/api/3/action/package_show?id=" + pkgId;
            CKANResponse resp = doCKANRequest("GET", url, "");
            JSONObject result = (JSONObject) resp.getJsonObject().get("result");
            JSONArray resources = (JSONArray) result.get("resources");
            
            for (int i = 0; i < resources.size(); i++) {
                JSONObject resource = (JSONObject) resources.get(i);
                resIds.add((String) resource.get("id"));
            } // for
            
            return resIds;
        } catch (Exception e) {
            return null;
        } // try catch
    } // getResources
    
    /**
     * Gets the total number of records within a given resource.
     * @param resId Resource identifier
     * @return The total number of records within the given resource
     */
    public int getNumRecords(String resId) {
        logger.info("Getting the number of records within " + resId);
        
        try {
            int numRecords = 0;
            int i = 0;
            
            while (true) {
                String url = "http" + (ssl ? "s" : "") + "://" + ckanHost + ":" + ckanPort
                        + "/api/3/action/datastore_search?limit=1000&offset=" + (i * 1000) + "&resource_id=" + resId;
                CKANResponse resp = doCKANRequest("GET", url, "");
                JSONObject result = (JSONObject) resp.getJsonObject().get("result");
                JSONArray records = (JSONArray) result.get("records");
                
                if (records.size() == 0) {
                    break;
                } // if
                
                numRecords += records.size();
                i++;
            } // while
            
            return numRecords;
        } catch (Exception e) {
            return 0;
        } // try catch
    } // resURL
    
    /**
     * Gets the records within a given resource.
     * @param resId
     * @param start
     * @param end
     * @return The records within the given resource
     */
    public JSONArray getRecords(String resId, long start, long end) {
        logger.info("Getting the [" + start + ", " + end + "] records within " + resId);
        
        try {
            String url = "http" + (ssl ? "s" : "") + "://" + ckanHost + ":" + ckanPort
                    + "/api/3/action/datastore_search?limit=" + end + "&offset=" + start + "&resource_id=" + resId;
            CKANResponse resp = doCKANRequest("GET", url, "");
            JSONObject result = (JSONObject) resp.getJsonObject().get("result");
            return (JSONArray) result.get("records");
        } catch (Exception e) {
            return null;
        } // try catch
    } // getRecords
    
    /**
     * Common method to perform HTTP requests using the CKAN API without payload.
     * @param method HTTP method
     * @param url URL path to be added to the base URL
     * @return CKANResponse associated to the request
     * @throws Exception
     */
    public CKANResponse doCKANRequest(String method, String url) throws Exception {
        return doCKANRequest(method, url, "");
    } // doCKANRequest

    /**
     * Common method to perform HTTP requests using the CKAN API with payload.
     * @param method HTTP method
     * @param url URL path to be added to the base URL
     * @param payload Request payload
     * @return CKANResponse associated to the request
     * @throws Exception
     */
    public CKANResponse doCKANRequest(String method, String url, String payload) throws Exception {
        HttpRequestBase request = null;
        HttpResponse response = null;
        
        try {
            // do the post
            if (method.equals("GET")) {
                request = new HttpGet(url);
            } else if (method.equals("POST")) {
                HttpPost r = new HttpPost(url);

                // payload (optional)
                if (!payload.equals("")) {
                    logger.debug("request payload: " + payload);
                    r.setEntity(new StringEntity(payload, ContentType.create("application/json")));
                } // if
                
                request = r;
            } else {
                throw new Exception("HTTP method not supported: " + method);
            } // if else

            // headers
            request.addHeader("Authorization", apiKey);

            // execute the request
            logger.debug("CKAN operation: " + request.toString());
        } catch (Exception e) {
            throw e;
        } // try catch
        
        try {
            response = httpClientFactory.getHttpClient(ssl).execute(request);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        } // try catch
        
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String res = "";
            String line;
            
            while ((line = reader.readLine()) != null) {
                res += line;
            } // while
            
            request.releaseConnection();
            long l = response.getEntity().getContentLength();
            logger.debug("CKAN response (" + l + " bytes): " + response.getStatusLine().toString());

            // get the JSON encapsulated in the response
            logger.debug("response payload: " + res);
            JSONParser j = new JSONParser();
            JSONObject o = (JSONObject) j.parse(res);

            // return result
            return new CKANResponse(o, response.getStatusLine().getStatusCode());
        } catch (Exception e) {
            throw e;
        } // try catch
    } // doCKANRequest
    
} // CKANBackend
