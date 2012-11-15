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
