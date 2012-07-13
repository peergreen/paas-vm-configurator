/**
 * JPaaS
 * Copyright (C) 2012 Bull S.A.S.
 * Contact: jasmine@ow2.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * --------------------------------------------------------------------------
 * $Id$
 * --------------------------------------------------------------------------
 */


package org.ow2.jonas.jpaas.vm.configurator.providers.chef.manager.osgi;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Validate;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.util.encoders.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ow2.jonas.jpaas.vm.configurator.providers.chef.manager.api.ChefManagerException;
import org.ow2.jonas.jpaas.vm.configurator.providers.chef.manager.api.ChefManagerService;
import org.ow2.jonas.lib.bootstrap.JProp;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import javax.ws.rs.core.MultivaluedMap;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.Security;
import java.security.Signature;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The implementation of the ChefManagerService Interface
 *
 * @author David Richard
 */
@Component(propagation = true)
@Provides
@Instantiate
public class ChefManagerImpl implements ChefManagerService {

    /**
     * Logger
     */
    private static Log logger = LogFactory.getLog(ChefManagerImpl.class);

    /**
     * Characters limit to split authentication headers
     */
    private static final int AUTH_HEADER_CHARACTERS_LIMIT = 60;

    /**
     * ChefManager property file name
     */
    private static final String CHEF_MANAGER_PROPERTY_FILE_NAME = "chefmanager.properties";

    /**
     * Property name to define the Chef server url
     */
    private static final String CHEF_SERVER_URL_PROPERTY = "chef.server.url";

    /**
     * Property name to define the Chef server version
     */
    private static final String CHEF_SERVER_VERSION_PROPERTY = "chef.server.version";

    /**
     * Property name to define the sign version
     */
    private static final String SIGN_VERSION_PROPERTY = "sign.version";

    /**
     * Property name to define the user id
     */
    private static final String USER_ID_PROPERTY = "user.id";

    /**
     * Property name to define the private key location
     */
    private static final String PRIVATE_KEY_FILE_LOCATION_PROPERTY = "private.key.file.location";

    private String chefServerUrl;

    private String chefVersion;

    private String signVersion;

    private String userID;

    private String privateKeyFileLocation;

    private KeyPair privateKey;



    @Validate
    public void start() throws ChefManagerException {
        logger.info("Load default configuration");
        setChefServerUrl(getChefServerUrlProperty());
        logger.info("Chef Server url : " + getChefServerUrl());
        setChefVersion(getChefServerVersionProperty());
        logger.info("Chef Server version : " + getChefVersion());
        setSignVersion(getSignVersionProperty());
        logger.info("Sign version : " + getSignVersion());
        setUserID(getUserIdProperty());
        logger.info("User ID : " + getUserID());
        setPrivateKeyFileLocation(getPrivateKeyLocationProperty());
        logger.info("Private key file location : " + getPrivateKeyFileLocation());
        privateKey = loadKey(privateKeyFileLocation);

    }

    /**
     * Return a list of all the API clients
     *
     * @throws ChefManagerException if an error occurs
     * @return the response
     */
    public String getClientsList() throws ChefManagerException {
        logger.debug("getClientsList ()");
        WebResource.Builder builder = createRequest("GET","/clients", "");
        String response = builder.accept("application/json").get(String.class);
        return response;
    }

    /**
     * Create a new client
     *
     * @param name    the client name
     * @param isAdmin true if the client is an administrator
     * @throws ChefManagerException if an error occurs
     * @return the response
     */
    public String createClient(String name, boolean isAdmin) throws ChefManagerException {
        logger.debug("createClient (" + name + ", " + isAdmin + ")");
        String content = "{\"name\": \"" + name + "\", \"admin\": " + isAdmin + "}";
        WebResource.Builder builder = createRequest("POST","/clients", content);
        String response = builder.accept("application/json").type("application/json").post(String.class, content);
        return response;
    }

    /**
     * Return the client
     *
     * @param name the client's name
     * @throws ChefManagerException if an error occurs
     * @return the response
     */
    public String getClientInfo(String name) throws ChefManagerException {
        logger.debug("getClientInfo (" + name +")");
        WebResource.Builder builder = createRequest("GET","/clients/" + name, "");
        String response = builder.accept("application/json").get(String.class);
        return response;
    }

    /**
     * Update the client
     *
     * @param name          the client name
     * @param isAdmin       true if the client is an admin
     * @param regenerateKey true to regenerate the client key
     * @throws ChefManagerException if an error occurs
     * @return the response
     */
    public String updateClient(String name, boolean isAdmin, boolean regenerateKey) throws ChefManagerException {
        logger.debug("updateClient (" + name + "," + isAdmin + "," + regenerateKey + ")");
        String content = "{\"name\": \"" + name + "\", \"private_key\": " + regenerateKey + ", \"admin\": " + isAdmin + "}";
        WebResource.Builder builder = createRequest("PUT","/clients/" + name, content);
        String response = builder.accept("application/json").type("application/json").put(String.class, content);
        return response;
    }

    /**
     * Delete the client
     *
     * @param name the client name
     * @throws ChefManagerException if an error occurs
     * @return the response
     */
    public String deleteClient(String name) throws ChefManagerException {
        logger.debug("deleteClient (" + name +")");
        WebResource.Builder builder = createRequest("DELETE","/clients/" + name, "");
        String response = builder.accept("application/json").delete(String.class);
        return response;
    }

    /**
     * Return a list of all the cookbooks present on the Chef server.
     * @throws ChefManagerException if an error occurs
     * @return the response
     */
    public String getCookbooks() throws ChefManagerException {
        logger.debug("deleteClient ()");
        WebResource.Builder builder = createRequest("GET","/cookbooks", "");
        String response = builder.accept("application/json").get(String.class);
        return response;
    }

    /**
     * Return a listing of the versions of the cookbook
     *
     * @param name the cookbook name
     * @throws ChefManagerException if an error occurs
     * @return the response
     *
     */
    public String getCookbookVersions(String name) throws ChefManagerException {
        logger.debug("getCookbookVersions (" + name +")");
        WebResource.Builder builder = createRequest("GET","/cookbooks/" + name, "");
        String response = builder.accept("application/json").get(String.class);
        return response;
    }

    /**
     * Return a description of the cookbook
     *
     * @param name    the cookbook name
     * @param version the cookbook version
     * @throws ChefManagerException if an error occurs
     * @return the response
     *
     */
    public String getCookbookInfo(String name, String version) throws ChefManagerException {
        logger.debug("getCookbookInfo (" + name + ", " + version + ")");
        WebResource.Builder builder = createRequest("GET","/cookbooks/" + name + "/" + version, "");
        String response = builder.accept("application/json").get(String.class);
        return response;
    }

    /**
     * Create or update the cookbook version
     *
     * @param name    the cookbook name
     * @param version the cookbook version
     * @throws ChefManagerException if an error occurs
     * @return the response
     */
    public String updateCookbook(String name, String version, String content) throws ChefManagerException {
        logger.debug("updateCookbook (" + name + "," + version + "," + content + ")");
        WebResource.Builder builder = createRequest("PUT","/cookbooks/" + name + "/" + version, content);
        String response = builder.accept("application/json").type("application/json").put(String.class, content);
        return response;
    }

    /**
     * Delete the cookbook version
     *
     * @param name    the cookbook name
     * @param version the cookbook version
     * @throws ChefManagerException if an error occurs
     * @return the response
     */
    public String deleteCookbook(String name, String version) throws ChefManagerException {
        logger.debug("deleteCookbook (" + name + ", " + version + ")");
        WebResource.Builder builder = createRequest("DELETE","/cookbooks/" + name + "/" + version, "");
        String response = builder.accept("application/json").delete(String.class);
        return response;
    }

    /**
     * Return a hash of uri's for the nodes
     * @throws ChefManagerException if an error occurs
     * @return the response
     */
    public String getNodesList() throws ChefManagerException {
        logger.debug("getNodesList ()");
        WebResource.Builder builder = createRequest("GET","/nodes", "");
        String response = builder.accept("application/json").get(String.class);
        return response;
    }

    /**
     * Create a node
     *
     * @param content the node content
     * @throws ChefManagerException if an error occurs
     * @return the response
     */
    public String createNode(String content) throws ChefManagerException {
        logger.debug("createNode (" + content +")");
        WebResource.Builder builder = createRequest("POST","/nodes", content);
        String response = builder.accept("application/json").type("application/json").post(String.class, content);
        return response;
    }

    /**
     * Return the node
     *
     * @param name the node name
     * @throws ChefManagerException if an error occurs
     * @return the response
     */
    public String getNodeInfo(String name) throws ChefManagerException {
        logger.debug("getNodeInfo (" + name +")");
        WebResource.Builder builder = createRequest("GET","/nodes/" + name, "");
        String response = builder.accept("application/json").get(String.class);
        return response;
    }

    /**
     * Update the node
     *
     * @param name    the node name
     * @param content the new node content
     * @throws ChefManagerException if an error occurs
     * @return the response
     */
    public String updateNodeInfo(String name, String content) throws ChefManagerException {
        logger.debug("updateNodeInfo (" + name + ", " + content + ")");
        WebResource.Builder builder = createRequest("PUT", "/nodes/" + name, content);
        String response = builder.accept("application/json").type("application/json").put(String.class, content);
        return response;
    }

    /**
     * Update the node run list
     *
     * @param name    the node name
     * @param listContent the new node run list
     * @throws ChefManagerException if an error occurs
     * @return the response
     */
    public String updateNodeRunList(String name, String listContent) throws ChefManagerException {
        logger.debug("updateNodeRunList (" + name + ", " + listContent + ")");
        String nodeContent = getNodeInfo(name);
        JSONObject jsonNodeContent = null;
        try {
            jsonNodeContent = new JSONObject(nodeContent);
            jsonNodeContent = jsonNodeContent.put("run_list", new JSONArray(listContent));
            nodeContent = jsonNodeContent.toString();
        } catch (JSONException e) {
            throw new ChefManagerException("Error for matching the node run list", e);
        }
        WebResource.Builder builder = createRequest("PUT","/nodes/" + name, nodeContent);
        String response = builder.accept("application/json").type("application/json").put(String.class, nodeContent);
        return response;

    }

    /**
     * Delete the node
     *
     * @param name the node name
     * @throws ChefManagerException if an error occurs
     * @return the response
     */
    public String deleteNode(String name) throws ChefManagerException {
        logger.debug("deleteNode (" + name +")");
        WebResource.Builder builder = createRequest("DELETE","/nodes/" + name, "");
        String response = builder.accept("application/json").delete(String.class);
        return response;
    }

    /**
     * Return a hash of uri's for the roles
     * @throws ChefManagerException if an error occurs
     * @return the response
     */
    public String getRolesList() throws ChefManagerException {
        logger.debug("getRolesList ()");
        WebResource.Builder builder = createRequest("GET","/roles", "");
        String response = builder.accept("application/json").get(String.class);
        return response;
    }

    /**
     * Create a role
     *
     * @param content the role content
     * @throws ChefManagerException if an error occurs
     * @return the response
     */
    public String createRole(String content) throws ChefManagerException {
        logger.debug("createRole (" + content +")");
        WebResource.Builder builder = createRequest("POST","/roles", content);
        String response = builder.accept("application/json").type("application/json").post(String.class, content);
        return response;
    }

    /**
     * Return the role
     *
     * @param name the role name
     * @throws ChefManagerException if an error occurs
     * @return the response
     */
    public String getRoleInfo(String name) throws ChefManagerException {
        logger.debug("getRoleInfo (" + name +")");
        WebResource.Builder builder = createRequest("GET","/roles/" + name, "");
        String response = builder.accept("application/json").get(String.class);
        return response;
    }

    /**
     * Update the role
     *
     * @param name    the role name
     * @param content the role content
     * @throws ChefManagerException if an error occurs
     * @return the response
     */
    public String updateRole(String name, String content) throws ChefManagerException {
        logger.debug("updateRole (" + name + ", " + content + ")");
        WebResource.Builder builder = createRequest("PUT","/roles/" + name, content);
        String response = builder.accept("application/json").type("application/json").put(String.class, content);
        return response;
    }

    /**
     * Delete the role
     *
     * @param name    the role name
     * @throws ChefManagerException if an error occurs
     * @return the response
     */
    public String deleteRole(String name) throws ChefManagerException {
        logger.debug("deleteRole (" + name +")");
        WebResource.Builder builder = createRequest("DELETE","/roles/" + name, "");
        String response = builder.accept("application/json").delete(String.class);
        return response;
    }

    /**
     * Return the search indexes
     *
     * @throws ChefManagerException if an error occurs
     * @return the response
     */
    public String getSearchIndex() throws ChefManagerException {
        logger.debug("getSearchIndex ()");
        WebResource.Builder builder = createRequest("GET","/search", "");
        String response = builder.accept("application/json").get(String.class);
        return response;
    }

    /**
     * Search in a specific index
     *
     * @param name
     *            the index name
     *
     * @param q
     *            A valid search string
     *
     * @param start
     *            The result number to start from
     *
     * @param rows
     *            How many rows to return
     *
     * @param sort
     *            A sort string, such as 'name DESC
     *
     * @throws ChefManagerException if an error occurs
     *
     * @return the response
     */
    public String searchIndex(String name, String q, String start, String rows, String sort) throws ChefManagerException {
        logger.debug("searchIndex (" + name + ")");
        MultivaluedMap queryParams = new MultivaluedMapImpl();
        queryParams.add("q", q);
        queryParams.add("start", start);
        queryParams.add("rows", rows);
        queryParams.add("sort", sort);
        WebResource.Builder builder = createRequestWithQueryParameters("GET", "/search/" + name, "", queryParams);
        String response = builder.accept("application/json").get(String.class);
        return response;
    }


    /**
     * Return the node name
     *
     * @param address
     *            the node IP address
     *
     * @throws ChefManagerException if an error occurs
     *
     * @return the response
     */
    public String getIpNodeName(String address) throws ChefManagerException {
        logger.debug("getIpNodeName (" + address +")");

        try {
            String searchResult = searchIndex("node", "ipaddress%3A" + address, "0", "1000", "X_CHEF_id_CHEF_X asc");
            Pattern p=Pattern.compile(",\"fqdn\":\"(.*?)\",");
            Matcher m=p.matcher(searchResult);
            m.find();
            String nodeName = m.group(1);
            return nodeName;
        } catch (Exception e) {
            throw new ChefManagerException("Node not found", e);
        }
    }


    /**
     * Return the node
     *
     * @param address
     *            the node IP address
     *
     * @throws ChefManagerException if an error occurs
     *
     * @return the response
     */
    public String getIpNodeInfo(String address) throws ChefManagerException {
        logger.debug("getIpNodeInfo (" + address +")");

        String nodeName = getIpNodeName(address);
        WebResource.Builder builder = createRequest("GET","/nodes/" + nodeName, "");
        String response = builder.accept("application/json").get(String.class);
        return response;
    }

    /**
     * Update the node
     *
     * @param address
     *            the node IP address
     * @param content
     *            the new node content
     *
     * @throws ChefManagerException if an error occurs
     *
     * @return the response
     */
    public String updateIpNodeInfo(String address, String content) throws ChefManagerException {
        logger.debug("updateIpNodeInfo (" + address + ", " + content + ")");

        String nodeName = getIpNodeName(address);
        WebResource.Builder builder = createRequest("PUT", "/nodes/" + nodeName, content);
        String response = builder.accept("application/json").type("application/json").put(String.class, content);
        return response;
    }

    /**
     * Update the node run list
     *
     * @param address
     *            the node IP address
     * @param listContent
     *            the new node run
     *
     * @throws ChefManagerException if an error occurs
     *
     * @return the response
     */
    public String updateIpNodeRunList(String address, String listContent) throws ChefManagerException {
        logger.debug("updateIpNodeRunList (" + address + ", " + listContent + ")");

        String nodeName = getIpNodeName(address);
        String nodeContent = getNodeInfo(nodeName);
        JSONObject jsonNodeContent = null;
        try {
            jsonNodeContent = new JSONObject(nodeContent);
            jsonNodeContent = jsonNodeContent.put("run_list", new JSONArray(listContent));
            nodeContent = jsonNodeContent.toString();
        } catch (JSONException e) {
            throw new ChefManagerException("Error for matching the node run list", e);
        }
        WebResource.Builder builder = createRequest("PUT","/nodes/" + nodeName, nodeContent);
        String response = builder.accept("application/json").type("application/json").put(String.class, nodeContent);
        return response;
    }


    /**
     * Delete the node
     *
     * @param address
     *            the node IP address
     *
     * @throws ChefManagerException if an error occurs
     *
     * @return the response
     */
    public String deleteIpNode(String address) throws ChefManagerException {
        logger.debug("deleteIpNode (" + address +")");

        String nodeName = getIpNodeName(address);
        WebResource.Builder builder = createRequest("DELETE","/nodes/" + nodeName, "");
        String response = builder.accept("application/json").delete(String.class);
        return response;
    }

    /**
     * test the availability of a node
     *
     * @param address
     *            the node IP address
     *
     * @return true if the node is available
     */
    public boolean isNodeAvailable(String address) {
        logger.debug("isNodeAvailable (" + address +")");

        try {
            getIpNodeName(address);
            return true;
        } catch (Exception e) {
            return false;
        }
    }



    /**
     * Return a keyPair which contains the private key
     *
     * @param path
     *            the path of private key pem file
     * @throws ChefManagerException if an error occurs
     * @return the KeyPair containing the private key
     */
    private KeyPair loadKey(String path) throws ChefManagerException {
        String keyPath = path;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(keyPath));
            Security.addProvider(new BouncyCastleProvider());
            KeyPair kp = null;
            kp = (KeyPair) new PEMReader(br).readObject();
            return kp;
        } catch (FileNotFoundException e) {
            throw new ChefManagerException("Private key file not found", e);
        } catch (IOException e) {
            throw new ChefManagerException("Error while reading the key", e);
        }
    }

    /**
     * Encrypt a message with SHA algorithm and encode it in base64
     *
     * @param message
     *            the message to encrypt
     * @throws ChefManagerException if an error occurs
     *
     * @return the encrypted message
     */
    private String encrypt(String message) throws ChefManagerException {
        MessageDigest md;

        try {
            md = MessageDigest.getInstance("SHA");
            md.update(message.getBytes("UTF-8"));
            byte raw[] = md.digest();
            byte[] coded = Base64.encode(raw);
            String hash = new String(coded);
            return hash;
        } catch (Exception e) {
            throw new ChefManagerException("Error during encryption", e);
        }
    }


    /**
     * Sign a message with the private key and encode it in base64
     *
     * @param message
     *            the message to encrypt
     * @param privateKey
     *            the private key
     * @throws ChefManagerException if an error occurs
     * @return the signed message
     */
    private String sign(String message, KeyPair privateKey) throws ChefManagerException {
        try {
            Signature signature = null;
            signature = Signature.getInstance("NONEwithRSA");

            signature.initSign(privateKey.getPrivate());
            signature.update(message.getBytes());
            byte [] signatureBytes = signature.sign();
            byte[] coded = Base64.encode(signatureBytes);
            String hash = new String(coded);
            return hash;
        } catch (Exception e) {
            throw new ChefManagerException("Error during signature", e);
        }
    }

    /**
     * Return the actual UTC time in a specific format
     *
     */
    private String getTimestamp() {
        String DATE_FORMAT_NOW = "yyyy-MM-dd'T'HH:mm:ss'Z'" ;
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String date = sdf.format(cal.getTime());
        return date;
    }

    /**
     * Split a String in several segments according to a specific number of characters
     *
     * @param string
     *            the string to split
     * @param charactersLimit
     *            the characters limit for a segment
     */
    public List<String> splitString(String string, int charactersLimit) {
        List<String> lines = new LinkedList<String>();
        for (int i = 0; i < string.length(); i += charactersLimit) {
            String tmp = "";
            tmp = string.substring(i, Math.min(string.length(), i + charactersLimit));
            lines.add(tmp);

        }
        return lines;
    }

    /**
     * Create the REST request builder
     *
     * @param method
     *            the method of the request
     * @param path
     *            the path of the request
     * @param content
     *            the content of the request
     * @throws ChefManagerException if an error occurs
     *
     */
    private WebResource.Builder createRequest(String method, String path, String content) throws ChefManagerException {
        logger.debug("createRequest (" + method + "," + path + "," + content + ")");
        String contentHash = encrypt(content);
        String timestamp = getTimestamp();
        Client client = Client.create();
        WebResource webResource = client.resource(chefServerUrl + path);
        WebResource.Builder builder = webResource.getRequestBuilder();

        builder.header("X-Ops-Timestamp", timestamp);
        builder.header("X-Ops-Userid", userID);
        builder.header("X-Ops-Chef-Version", chefVersion);
        builder.header("X-Ops-Content-Hash", contentHash);
        builder.header("X-Ops-Sign", signVersion);

        String canonicalHeader = getCanonicalHeader(method, path, timestamp, content);
        String signedCanonicalHeader = sign(canonicalHeader, privateKey);
        List<String> listTmp = splitString(signedCanonicalHeader, AUTH_HEADER_CHARACTERS_LIMIT);
        for (int i=0; i<listTmp.size(); i++) {
            builder.header("X-Ops-Authorization-" + (i+1),listTmp.get(i));
        }
        return builder;
    }

    /**
     * Create a REST request builder with a query parameters
     *
     * @param method
     *            the method of the request
     * @param path
     *            the path of the request
     * @param content
     *            the content of the request
     * @param queryParams
     *            the query parameters
     * @throws ChefManagerException if an error occurs
     *
     */
    private WebResource.Builder createRequestWithQueryParameters(String method, String path, String content, MultivaluedMap queryParams) throws ChefManagerException {
        logger.debug("createRequest (" + method + "," + path + "," + content + ")");
        String contentHash = encrypt(content);
        String timestamp = getTimestamp();
        Client client = Client.create();
        WebResource webResource = client.resource(chefServerUrl + path);
        webResource = webResource.queryParams(queryParams);
        WebResource.Builder builder = webResource.getRequestBuilder();


        builder.header("X-Ops-Timestamp", timestamp);
        builder.header("X-Ops-Userid", userID);
        builder.header("X-Ops-Chef-Version", chefVersion);
        builder.header("X-Ops-Content-Hash", contentHash);
        builder.header("X-Ops-Sign", signVersion);

        String canonicalHeader = getCanonicalHeader(method, path, timestamp, content);
        String signedCanonicalHeader = sign(canonicalHeader, privateKey);
        List<String> listTmp = splitString(signedCanonicalHeader, AUTH_HEADER_CHARACTERS_LIMIT);
        for (int i=0; i<listTmp.size(); i++) {
            builder.header("X-Ops-Authorization-" + (i+1),listTmp.get(i));
        }
        return builder;
    }


    /**
     * Make the canonical header for a REST request
     *
     * @param method
     *            the method of the request
     * @param path
     *            the path of the request
     * @param timestamp
     *            the timestamp of the request
     * @param content
     *            the content of the request
     * @throws ChefManagerException if an error occurs
     *
     */
    private String getCanonicalHeader(String method, String path, String timestamp, String content) throws ChefManagerException {
        String pathHash = encrypt(path);
        String contentHash = encrypt(content);
        return "Method:" + method + "\nHashed Path:" + pathHash + "\nX-Ops-Content-Hash:" + contentHash
                + "\nX-Ops-Timestamp:" + timestamp + "\nX-Ops-UserId:" + userID;
    }



    /**
     * Get the property chef server url located in JONAS_BASE/conf/chefmanager.properties
     * with the key file.location
     *
     * @return the chef server url
     */
    private String getChefServerUrlProperty() {
        JProp prop = JProp.getInstance(CHEF_MANAGER_PROPERTY_FILE_NAME);
        return prop.getValue(CHEF_SERVER_URL_PROPERTY);
    }

    /**
     * Get the property chef server version located in JONAS_BASE/conf/chefmanager.properties
     * with the key file.location
     *
     * @return the chef server version
     */
    private String getChefServerVersionProperty() {
        JProp prop = JProp.getInstance(CHEF_MANAGER_PROPERTY_FILE_NAME);
        return prop.getValue(CHEF_SERVER_VERSION_PROPERTY);
    }

    /**
     * Get the property sign version located in JONAS_BASE/conf/chefmanager.properties
     * with the key file.location
     *
     * @return the sign version
     */
    private String getSignVersionProperty() {
        JProp prop = JProp.getInstance(CHEF_MANAGER_PROPERTY_FILE_NAME);
        return prop.getValue(SIGN_VERSION_PROPERTY);
    }

    /**
     * Get the property user ID located in JONAS_BASE/conf/chefmanager.properties
     * with the key file.location
     *
     * @return the user id to communicate with the Chef Server
     */
    private String getUserIdProperty() {
        JProp prop = JProp.getInstance(CHEF_MANAGER_PROPERTY_FILE_NAME);
        return prop.getValue(USER_ID_PROPERTY);
    }

    /**
     * Get the private key location located in JONAS_BASE/conf/chefmanager.properties
     * with the key file.location
     *
     * @return the private key location
     */
    private String getPrivateKeyLocationProperty() {
        JProp prop = JProp.getInstance(CHEF_MANAGER_PROPERTY_FILE_NAME);
        return prop.getValue(PRIVATE_KEY_FILE_LOCATION_PROPERTY);
    }

    public String getChefServerUrl() {
        return chefServerUrl;
    }

    public void setChefServerUrl(String chefServerUrl) {
        this.chefServerUrl = chefServerUrl;
    }

    public String getSignVersion() {
        return signVersion;
    }

    public void setSignVersion(String signVersion) {
        this.signVersion = signVersion;
    }

    public String getChefVersion() {
        return chefVersion;
    }

    public void setChefVersion(String chefVersion) {
        this.chefVersion = chefVersion;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPrivateKeyFileLocation() {
        return privateKeyFileLocation;
    }

    public void setPrivateKeyFileLocation(String privateKeyFileLocation) {
        this.privateKeyFileLocation = privateKeyFileLocation;
    }



}

