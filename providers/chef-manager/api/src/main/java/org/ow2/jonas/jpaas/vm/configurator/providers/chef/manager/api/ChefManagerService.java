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


package org.ow2.jonas.jpaas.vm.configurator.providers.chef.manager.api;

/**
 * The interface describing the ChefManagerService to interact with a Chef Server.
 *
 * @author David Richard
 */
public interface ChefManagerService {

    /**
     * Return a list of all the API clients
     *
     * @throws ChefManagerException if an error occurs
     *
     * @return the response
     */
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
    public String createClient(String name, boolean isAdmin) throws ChefManagerException;


    /**
     * Return the client
     *
     * @param name
     *            the client's name
     *
     * @throws ChefManagerException if an error occurs
     * @return the response
     */
    public String getClientInfo(String name) throws ChefManagerException;


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
    public String updateClient(String name, boolean isAdmin, boolean regenerateKey) throws ChefManagerException;


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
    public String deleteClient(String name) throws ChefManagerException;


    /**
     * Return a list of all the cookbooks present on the Chef server.
     *
     * @throws ChefManagerException if an error occurs
     *
     * @return the response
     */
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
    public String getCookbookVersions(String name) throws ChefManagerException;


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
    public String getCookbookInfo(String name, String version) throws ChefManagerException;

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
    public String updateCookbook(String name, String version, String content) throws ChefManagerException;


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
    public String deleteCookbook(String name, String version) throws ChefManagerException;


    /**
     * Return a hash of uri's for the nodes
     *
     * @throws ChefManagerException if an error occurs
     *
     * @return the response
     */
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
    public String createNode(String content) throws ChefManagerException;


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
    public String getNodeInfo(String name) throws ChefManagerException;

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
    public String updateNodeInfo(String name, String content) throws ChefManagerException;

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
    public String updateNodeRunList(String name, String listContent) throws ChefManagerException;


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
    public String deleteNode(String name) throws ChefManagerException;


    /**
     * Return a hash of uri's for the roles
     *
     * @throws ChefManagerException if an error occurs
     *
     * @return the response
     */
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
    public String createRole(String content) throws ChefManagerException;

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
    public String getRoleInfo(String name) throws ChefManagerException;


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
    public String updateRole(String name, String content) throws ChefManagerException;


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
    public String deleteRole(String name) throws ChefManagerException;


    /**
     * Return the search indexes
     *
     * @throws ChefManagerException if an error occurs
     *
     * @return the response
     */
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
    public String searchIndex(String name, String q, String start, String rows, String sort)
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
    public String getIpNodeName(String address) throws ChefManagerException;



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
    public String getIpNodeInfo(String address) throws ChefManagerException;

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
    public String updateIpNodeInfo(String address, String content) throws ChefManagerException;

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
    public String updateIpNodeRunList(String address, String listContent) throws ChefManagerException;


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
    public String deleteIpNode(String address) throws ChefManagerException;


    /**
     * test the availability of a node
     *
     * @param address
     *            the node IP address
     *
     * @return true if the node is available
     */
    public boolean isNodeAvailable(String address);


}

