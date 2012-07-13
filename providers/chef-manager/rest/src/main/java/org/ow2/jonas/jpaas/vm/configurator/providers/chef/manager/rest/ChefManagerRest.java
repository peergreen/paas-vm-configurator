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


package org.ow2.jonas.jpaas.vm.configurator.providers.chef.manager.rest;


import org.ow2.jonas.jpaas.vm.configurator.providers.chef.manager.api.ChefManagerException;
import org.ow2.jonas.jpaas.vm.configurator.providers.chef.manager.api.ChefManagerService;
import org.ow2.jonas.jpaas.vm.configurator.providers.chef.manager.api.rest.IChefManager;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

/**
 * The implementation of the IChefManager Interface
 *
 * @author David Richard
 */
public class ChefManagerRest implements IChefManager {

    private ChefManagerService chefManagerService;

    /**
     * The logger.
     */
    private Log logger = LogFactory.getLog(this.getClass());

    public ChefManagerRest(ChefManagerService chefManagerService) {
        this.chefManagerService=chefManagerService;
    }

    public String getClientsList() throws ChefManagerException {
        logger.debug("Retrieving clients list.");
        return chefManagerService.getClientsList();
    }

    public String createClient(String name, boolean isAdmin) throws ChefManagerException {
        logger.debug("Creating client (name = '" + name + "', admin = " + isAdmin + ").");
        return chefManagerService.createClient(name, isAdmin);
    }

    public String getClientInfo(String name) throws ChefManagerException {
        logger.debug("Retrieving client (name = '" + name + ").");
        return chefManagerService.getClientInfo(name);
    }

    public String updateClient(String name, boolean isAdmin, boolean regenerateKey) throws ChefManagerException {
        logger.debug("Updating client (name = '" + name + "', admin = " + isAdmin + ", RegenerateKey = "
                + regenerateKey + ").");
        return chefManagerService.updateClient(name,isAdmin, regenerateKey);
    }

    public String deleteClient(String name) throws ChefManagerException {
        logger.debug("Deleting client (name = '" + name + ").");
        return chefManagerService.deleteClient(name);
    }

    public String getCookbooks() throws ChefManagerException {
        logger.debug("Retrieving cookbooks list.");
        return chefManagerService.getCookbooks();
    }

    public String getCookbookVersions(String name) throws ChefManagerException {
        logger.debug("Retrieving cookbook version (name = '" + name + ").");
        return chefManagerService.getCookbookVersions(name);
    }

    public String getCookbookInfo(String name, String version) throws ChefManagerException {
        logger.debug("Retrieving cookbook (name = '" + name + "', version = '" + version + "').");
        return chefManagerService.getCookbookInfo(name, version);
    }

    public String updateCookbook(String name, String version, String content) throws ChefManagerException {
        logger.debug("Updating cookbook (name = '" + name + "', version = '" + version + "', content = "
                + content + ").");
        return chefManagerService.updateCookbook(name, version, content);
    }

    public String deleteCookbook(String name, String version) throws ChefManagerException {
        logger.debug("Deleting cookbook (name = '" + name + "', version = '" + version + "').");
        return chefManagerService.deleteCookbook(name, version);
    }

    public String getNodesList() throws ChefManagerException {
        logger.debug("Retrieving nodes list.");
        return chefManagerService.getNodesList();
    }

    public String createNode(String content) throws ChefManagerException {
        logger.debug("Creating node (content = " + content + ").");
        return chefManagerService.createNode(content);
    }

    public String getNodeInfo(String name) throws ChefManagerException {
        logger.debug("Retrieving node (name = '" + name + ").");
        return chefManagerService.getNodeInfo(name);
    }

    public String updateNodeInfo(String name, String content) throws ChefManagerException {
        logger.debug("Updating node run list (name = '" + name + "', content = " + content + ").");
        return chefManagerService.updateNodeInfo(name, content);
    }

    public String updateNodeRunList(String name, String listContent) throws ChefManagerException {
        logger.debug("Updating node (name = '" + name + "', content = " + listContent + ").");
        return chefManagerService.updateNodeRunList(name, listContent);
    }

    public String deleteNode(String name) throws ChefManagerException {
        logger.debug("Deleting node (name = '" + name + ").");
        return chefManagerService.deleteNode(name);
    }

    public String getRolesList() throws ChefManagerException {
        logger.debug("Retrieving roles list.");
        return chefManagerService.getRolesList();
    }

    public String createRole(String content) throws ChefManagerException {
        logger.debug("Creating role (content = " + content + ").");
        return chefManagerService.createRole(content);
    }

    public String getRoleInfo(String name) throws ChefManagerException {
        logger.debug("Retrieving role (name = '" + name + ").");
        return chefManagerService.getRoleInfo(name);
    }

    public String updateRole(String name, String content) throws ChefManagerException {
        logger.debug("Updating role (name = '" + name + "', content = " + content + ").");
        return chefManagerService.updateRole(name, content);
    }

    public String deleteRole(String name) throws ChefManagerException {
        logger.debug("Deleting role (name = '" + name + ").");
        return chefManagerService.deleteRole(name);
    }

    public String getSearchIndex() throws ChefManagerException {
        logger.debug("Retrieving search indexes.");
        return chefManagerService.getSearchIndex();
    }

    public String searchIndex(String name, String q, String start, String rows, String sort) throws ChefManagerException {
        logger.debug("Retrieving search (name = '" + name + ", q = " + q +
                ", start = " + start + ", rows = " + rows + ", sort = " + sort + ").");
        return chefManagerService.searchIndex(name, q, start, rows, sort);
    }

    public String getIpNodeName(String address) throws ChefManagerException {
        logger.debug("Retrieving node name (address = '" + address + ").");
        return chefManagerService.getIpNodeName(address);
    }

    public String getIpNodeInfo(String address) throws ChefManagerException {
        logger.debug("Retrieving node (address = '" + address + ").");
        return chefManagerService.getIpNodeInfo(address);
    }

    public String updateIpNodeInfo(String address, String content) throws ChefManagerException {
        logger.debug("Updating node run list (address = '" + address + "', content = " + content + ").");
        return chefManagerService.updateIpNodeInfo(address, content);
    }

    public String updateIpNodeRunList(String address, String listContent) throws ChefManagerException {
        logger.debug("Updating node (address = '" + address + "', content = " + listContent + ").");
        return chefManagerService.updateIpNodeRunList(address, listContent);
    }

    public String deleteIpNode(String address) throws ChefManagerException {
        logger.debug("Deleting node (address = '" + address + ").");
        return chefManagerService.deleteIpNode(address);
    }

    public boolean isNodeAvailable(String address) {
        logger.debug("is node available? (address = '" + address + ").");
        return chefManagerService.isNodeAvailable(address);
    }
}
