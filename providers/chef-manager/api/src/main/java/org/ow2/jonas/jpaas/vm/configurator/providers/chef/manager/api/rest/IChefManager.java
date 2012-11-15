/**
 * JPaaS
 * Copyright 2012 Bull S.A.S.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * $Id:$
 */

package org.ow2.jonas.jpaas.vm.configurator.providers.chef.manager.api.rest;

import org.ow2.jonas.jpaas.vm.configurator.providers.chef.manager.api.ChefManagerException;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

/**
 * ChefManager Rest Interface.
 *
 * @author David Richard
 */
@Path("/")
public interface IChefManager {


    /**
     * Return a list of all the API clients
     *
     * @throws ChefManagerException if an error occurs
     *
     * @return the response
     */
    @GET
    @Path("/clients")
    public String getClientsList() throws ChefManagerException;


    /**
     * Create a new client
     *
     * @param name
     *            the client name
     * @param isAdmin
     *            true if the client is an administrator
     *
     * @throws ChefManagerException if an error occurs
     * @return the response
     */
    @POST
    @Path("/clients")
    public String createClient(@FormParam("name") String name,
            @FormParam("admin") boolean isAdmin) throws ChefManagerException;


    /**
     * Return the client
     *
     * @param name
     *            the client's name
     *
     * @throws ChefManagerException if an error occurs
     * @return the response
     */
    @GET
    @Path("/clients/{name}")
    public String getClientInfo(@PathParam("name") String name) throws ChefManagerException;


    /**
     * Update the client
     *
     * @param name
     *            the client name
     * @param isAdmin
     *            true if the client is an admin
     * @param regenerateKey
     *            true to regenerate the client key
     *
     * @throws ChefManagerException if an error occurs
     *
     * @return the response
     */
    @PUT
    @Path("/clients/{name}")
    public String updateClient(@PathParam("name") String name,
            @FormParam("admin") boolean isAdmin,
            @FormParam("key") boolean regenerateKey) throws ChefManagerException;


    /**
     * Delete the client
     *
     * @param name
     *            the client name
     *
     * @throws ChefManagerException if an error occurs
     *
     * @return the response
     */
    @DELETE
    @Path("/clients/{name}")
    public String deleteClient(@PathParam("name") String name) throws ChefManagerException;


    /**
     * Return a list of all the cookbooks present on the Chef server.
     *
     * @throws ChefManagerException if an error occurs
     *
     * @return the response
     */
    @GET
    @Path("/cookbooks")
    public String getCookbooks() throws ChefManagerException;


    /**
     * Return a listing of the versions of the cookbook
     *
     * @param name
     *            the cookbook name
     *
     * @throws ChefManagerException if an error occurs
     *
     * @return the response
     */
    @GET
    @Path("/cookbooks/{name}")
    public String getCookbookVersions(@PathParam("name") String name) throws ChefManagerException;


    /**
     * Return a description of the cookbook
     *
     * @param name
     *            the cookbook name
     * @param version
     *            the cookbook version
     *
     * @throws ChefManagerException if an error occurs
     *
     * @return the response
     */
    @GET
    @Path("/cookbooks/{name}/{version}")
    public String getCookbookInfo(@PathParam("name") String name,
            @PathParam("version") String version) throws ChefManagerException;


    /**
     * Create or update the cookbook version
     *
     * @param name
     *            the cookbook name
     * @param version
     *            the cookbook version
     * @param content
     *            the cookbook content
     * @throws ChefManagerException if an error occurs
     *
     * @return the response
     */
    @PUT
    @Path("/cookbooks/{name}/{version}")
    public String updateCookbook(@PathParam("name") String name,
            @PathParam("version") String version,
            @FormParam("content") String content) throws ChefManagerException;


    /**
     * Delete the cookbook version
     *
     * @param name
     *            the cookbook name
     * @param version
     *            the cookbook version
     *
     * @throws ChefManagerException if an error occurs
     *
     * @return the response
     */
    @DELETE
    @Path("/cookbooks/{name}/{version}")
    public String deleteCookbook(@PathParam("name") String name,
            @PathParam("version") String version) throws ChefManagerException;


    /**
     * Return a hash of uri's for the nodes
     *
     * @throws ChefManagerException if an error occurs
     *
     * @return the response
     */
    @GET
    @Path("/nodes")
    public String getNodesList() throws ChefManagerException;


    /**
     * Create a node
     *
     * @param content
     *            the node content
     *
     * @throws ChefManagerException if an error occurs
     *
     * @return the response
     */
    @POST
    @Path("/nodes")
    public String createNode(@FormParam("content") String content) throws ChefManagerException;


    /**
     * Return the node
     *
     * @param name
     *            the node name
     *
     * @throws ChefManagerException if an error occurs
     *
     * @return the response
     */
    @GET
    @Path("/nodes/{name}")
    public String getNodeInfo(@PathParam("name") String name) throws ChefManagerException;


    /**
     * Update the node
     *
     * @param name
     *            the node name
     * @param content
     *            the new node content
     *
     * @throws ChefManagerException if an error occurs
     *
     * @return the response
     */
    @PUT
    @Path("/nodes/{name}")
    public String updateNodeInfo(@PathParam("name") String name,
            @FormParam("content") String content) throws ChefManagerException;


    /**
     * Update the node run list
     *
     * @param name
     *            the node name
     * @param listContent
     *            the new node run list
     *
     * @throws ChefManagerException if an error occurs
     *
     * @return the response
     */
    @PUT
    @Path("/nodes/{name}/runlist")
    public String updateNodeRunList(@PathParam("name") String name,
            @FormParam("listcontent") String listContent) throws ChefManagerException;


    /**
     * Delete the node
     *
     * @param name
     *            the node name
     *
     * @throws ChefManagerException if an error occurs
     *
     * @return the response
     */
    @DELETE
    @Path("/nodes/{name}")
    public String deleteNode(String name) throws ChefManagerException;


    /**
     * Return a hash of uri's for the roles
     *
     * @throws ChefManagerException if an error occurs
     *
     * @return the response
     */
    @GET
    @Path("/roles")
    public String getRolesList() throws ChefManagerException;


    /**
     * Create a role
     *
     * @param content
     *            the role content
     *
     * @throws ChefManagerException if an error occurs
     *
     * @return the response
     */
    @POST
    @Path("/roles")
    public String createRole(@FormParam("content") String content) throws ChefManagerException;


    /**
     * Return the role
     *
     * @param name
     *            the role name
     *
     * @throws ChefManagerException if an error occurs
     *
     * @return the response
     */
    @GET
    @Path("/roles/{name}")
    public String getRoleInfo(@PathParam("name") String name) throws ChefManagerException;


    /**
     * Update the role
     *
     * @param name
     *            the role name
     * @param content
     *            the role content
     *
     * @throws ChefManagerException if an error occurs
     *
     * @return the response
     */
    @PUT
    @Path("/roles/{name}")
    public String updateRole(@PathParam("name") String name,
            @FormParam("content") String content) throws ChefManagerException;


    /**
     * Delete the role
     *
     * @param name
     *            the role name
     *
     * @throws ChefManagerException if an error occurs
     *
     * @return the response
     */
    @DELETE
    @Path("/roles/{name}")
    public String deleteRole(@PathParam("name") String name) throws ChefManagerException;


    /**
     * Return the search indexes
     *
     * @throws ChefManagerException if an error occurs
     *
     * @return the response
     */
    @GET
    @Path("/search")
    public String getSearchIndex() throws ChefManagerException;


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
    @GET
    @Path("/search/{name}")
    public String searchIndex(@PathParam("name") String name,
            @DefaultValue("") @QueryParam("q") String q,
            @DefaultValue("0") @QueryParam("start") String start,
            @DefaultValue("1000") @QueryParam("rows") String rows,
            @DefaultValue("X_CHEF_id_CHEF_X asc") @QueryParam("sort") String sort)
            throws ChefManagerException;


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
    @GET
    @Path("/nodes/ip/{address}/name")
    public String getIpNodeName(@PathParam("address") String address) throws ChefManagerException;



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
    @GET
    @Path("/nodes/ip/{address}")
    public String getIpNodeInfo(@PathParam("address") String address) throws ChefManagerException;

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
    @PUT
    @Path("/nodes/ip/{address}")
    public String updateIpNodeInfo(@PathParam("address") String address,
            @FormParam("content") String content) throws ChefManagerException;

    /**
     * Update the node run list
     *
     * @param address
     *            the node IP address
     * @param listContent
     *            the new node run list
     *
     * @throws ChefManagerException if an error occurs
     *
     * @return the response
     */
    @PUT
    @Path("/nodes/ip/{address}/runlist")
    public String updateIpNodeRunList(@PathParam("address") String address,
            @FormParam("listcontent") String listContent) throws ChefManagerException;


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
    @DELETE
    @Path("/nodes/ip/{address}")
    public String deleteIpNode(@PathParam("address") String address) throws ChefManagerException;

    /**
     * test the availability of a node
     *
     * @param address
     *            the node IP address
     *
     * @return true if the node is available
     */
    @GET
    @Path("/nodes/ip/{address}/available")
    public boolean isNodeAvailable(@PathParam("address") String address);


}
