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

package org.ow2.jonas.jpaas.vm.configurator.bean;


import org.json.JSONException;
import org.json.JSONObject;
import org.ow2.easybeans.osgi.annotation.OSGiResource;
import org.ow2.jonas.jpaas.catalog.api.IPaasCatalogFacade;
import org.ow2.jonas.jpaas.catalog.api.PaasCatalogException;
import org.ow2.jonas.jpaas.catalog.api.PaasConfiguration;
import org.ow2.jonas.jpaas.sr.facade.api.ISrIaasComputeFacade;
import org.ow2.jonas.jpaas.sr.facade.vo.IaasComputeVO;
import org.ow2.jonas.jpaas.vm.configurator.api.IVmConfigurator;
import org.ow2.jonas.jpaas.vm.configurator.api.VmConfiguratorException;
import org.ow2.jonas.jpaas.vm.configurator.providers.chef.manager.api.ChefManagerException;
import org.ow2.jonas.jpaas.vm.configurator.providers.chef.manager.api.ChefManagerService;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * VmConfigurator Bean
 * @author David Richard
 */
@Stateless(mappedName = "VmConfiguratorBean")
@Local(IVmConfigurator.class)
@Remote(IVmConfigurator.class)
public class VmConfiguratorBean implements IVmConfigurator {

    @OSGiResource
    ChefManagerService chefManagerService = null;

    @OSGiResource
    ISrIaasComputeFacade iSrIaasComputeFacade = null;

    @OSGiResource
    IPaasCatalogFacade iPaasCatalogFacade = null;

    /**
     * Logger
     */
    private static Log logger = LogFactory.getLog(VmConfiguratorBean.class);

    /**
     * Install an Agent on an Iaas Compute
     *
     * @param agentName   the name of the Agent
     * @param computeName the name of the IaaS Compute
     * @return the PaasConfiguration
     */
    @Override
    public Future<IaasComputeVO> installAgent(String agentName, String computeName) throws VmConfiguratorException {
        logger.debug("installAgent(" + agentName + "," + computeName + ")");
        IaasComputeVO iaasComputeVO = iSrIaasComputeFacade.getIaasCompute(getComputeIdByName(computeName));
        try {
            if (!roleIsPresent("Handler", iaasComputeVO.getRoles())) {
                chefManagerService.addRoleToIpNodeRunListBeginning(iaasComputeVO.getIpAddress(), "Handler");
            }
            if (!roleIsPresent("Agent", iaasComputeVO.getRoles())) {
                chefManagerService.addRoleToIpNodeRunListEnd(iaasComputeVO.getIpAddress(), "Agent");
            } else {
                throw new VmConfiguratorException("jPaaS Agent is already installed.");
            }
        } catch (ChefManagerException e) {
            throw new VmConfiguratorException("ChefManager has encountered a problem.", e.getCause());
        }
        return null;
    }

    /**
     * Install a Software on an Iaas Compute
     *
     * @param computeName           the name of the Iaas Compute
     * @param paasConfigurationName the name of the PaaS configuration
     * @return the PaasConfiguration
     */
    @Override
    public Future installSoftware(String computeName, String paasConfigurationName) throws VmConfiguratorException {
        logger.debug("installSoftware(" + computeName + "," + paasConfigurationName + ")");
        IaasComputeVO iaasComputeVO = iSrIaasComputeFacade.getIaasCompute(getComputeIdByName(computeName));
        try {
            PaasConfiguration paasConfiguration = iPaasCatalogFacade.getPaasConfiguration(paasConfigurationName);
            Element devopsConf = loadSpecificConfig(paasConfiguration.getDevopsConf());
            String chefRole = devopsConf.getElementsByTagName("chef-role").item(0).getFirstChild().getNodeValue();
            String extendRole = devopsConf.getElementsByTagName("extend-role").item(0).getFirstChild().getNodeValue();
            //If an extended role needs to be created
            if (extendRole.equalsIgnoreCase("true")) {
                String chefParentRole = devopsConf.getElementsByTagName("chef-parent-role").item(0).
                        getFirstChild().getNodeValue();
                if (chefManagerService.roleExists(chefRole)) {
                    throw new VmConfiguratorException("Conflict : a Chef role named " + chefRole + " already exists.");
                }
                if (!chefManagerService.roleExists(chefParentRole)) {
                    throw new VmConfiguratorException("The Chef parent role " + chefParentRole + " does not exist.");
                }
                //Get override attributes
                Map<String,String> overrideAttributes = getChefAttributes(devopsConf, "chef-override-attributes");
                //Get default attributes
                Map<String,String> defaultAttributes = getChefAttributes(devopsConf, "chef-default-attributes");
                //Create role
                createRole(chefRole, chefParentRole, overrideAttributes, defaultAttributes);
            } else {
                if (!chefManagerService.roleExists(chefRole)) {
                    throw new VmConfiguratorException("The Chef role " + chefRole + " does not exist.");
                }
            }
            if (!roleIsPresent("Handler", iaasComputeVO.getRoles())) {
                chefManagerService.addRoleToIpNodeRunListBeginning(iaasComputeVO.getIpAddress(), "Handler");
            }
            if (!roleIsPresent(chefRole, iaasComputeVO.getRoles())) {
                chefManagerService.addRoleToIpNodeRunListEnd(iaasComputeVO.getIpAddress(), chefRole);
            } else {
                throw new VmConfiguratorException("The role " + chefRole + " was already executed on the compute" +
                        " named " + iaasComputeVO.getName() + ".");
            }
        } catch (org.w3c.dom.DOMException e) {
            throw new VmConfiguratorException("Error during configuration loading - " + e.getMessage(), e.getCause());
        } catch (ChefManagerException e) {
            throw new VmConfiguratorException("ChefManager has encountered a problem.", e.getCause());
        } catch (PaasCatalogException e) {
            throw new VmConfiguratorException("Error to find the PaaS Configuration named " +
                    paasConfigurationName + ".", e.getCause());
        }
        return null;
    }

    /**
     * Return true if the role is present in the role list
     * @param roleName the name of the role
     * @param roleList the role list
     * @return true if the is present
     */
    private boolean roleIsPresent(String roleName, List<String> roleList) {
        logger.debug("roleIsPresent(" + roleName + ", " + roleList.toString() + ")");
        boolean result = false;
        for (String tmp : roleList) {
            if (tmp.equals(roleName)) {
                result = true;
                break;
            }
        }
        return result;
    }


    /**
     * Get the Id of a Iaas Compute
     * @param computeName the name of the IaaS Compute
     * @return the Compute Id
     * @throws VmConfiguratorException
     */
    private String getComputeIdByName(String computeName) throws VmConfiguratorException {
        List<IaasComputeVO> iaasComputeList = iSrIaasComputeFacade.findIaasComputes();
        String computeId = null;
        for (IaasComputeVO tmp : iaasComputeList) {
            if (tmp.getName().equals(computeName)) {
                computeId = tmp.getId();
                break;
            }
        }
        if (computeId == null) {
            throw new VmConfiguratorException("Cannot find the Compute named " + computeName);
        } else {
            return computeId;
        }
    }


    private Map<String,String> getChefAttributes(Element devopsConf, String tagName){
        Map<String,String> attributesMap = new HashMap<java.lang.String, java.lang.String>();
        if (devopsConf.getElementsByTagName(tagName).getLength() > 0) {
            NodeList list = devopsConf.getElementsByTagName(tagName).item(0).getChildNodes();
            for (int i=0; i < list.getLength(); i++) {
                Node subnode = list.item(i);
                if (subnode.getNodeType() == Node.ELEMENT_NODE) {
                    attributesMap.put(subnode.getNodeName(), subnode.getFirstChild().getNodeValue());
                }
            }
        }
        return attributesMap;
    }

    /**
     * Create the Chef Role
     * @param roleName name of the role
     * @param parentRole name of the parent role
     * @param overrideAttributes the override attributes of the role
     * @param defaultAttributes  the default attributes of the role
     * @throws VmConfiguratorException
     */
    private void createRole(String roleName, String parentRole, Map<String,String> overrideAttributes,
            Map<String,String> defaultAttributes) throws VmConfiguratorException {
        JSONObject jsonOverride = new JSONObject();
        JSONObject jsonDefault = new JSONObject();
        JSONObject jsonRoleContent = new JSONObject();
        try {
            //Creation of the JSON override attributes object
            if (!overrideAttributes.isEmpty()) {
                jsonOverride.put("jpaas", overrideAttributes);
            }
            //Creation of the JSON default attributes object
            if (!defaultAttributes.isEmpty()) {
                jsonOverride.put("jpaas", defaultAttributes);
            }
            //Creation of the JSON role content object
            jsonRoleContent.accumulate("name", roleName);
            jsonRoleContent.accumulate("chef_type", "role");
            jsonRoleContent.accumulate("json_class", "Chef::Role");
            jsonRoleContent.accumulate("description", "Child of the " + parentRole + " role.");
            jsonRoleContent.accumulate("override_attributes", jsonOverride);
            jsonRoleContent.accumulate("default_attributes", jsonDefault);
            jsonRoleContent.accumulate("env_run_lists", new JSONObject());
            jsonRoleContent.append("run_list", "role[" + parentRole + "]");
            //Use ChefManager to create the role
            chefManagerService.createRole(jsonRoleContent.toString());
        } catch (JSONException e) {
            throw new VmConfiguratorException("Error during the creation of the role's JSON content.", e.getCause());
        } catch (ChefManagerException e) {
            throw new VmConfiguratorException("ChefManager has encountered a problem.", e.getCause());
        }

    }

    /**
     * Load a configuration file
     * @param file path of the configuration file
     * @return the XML Root Element of the configuration file
     */
    private Element loadSpecificConfig(String file) throws VmConfiguratorException {
        Element result = null;
        try {
            File f = new File(file);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(f);
            result = document.getDocumentElement();
        } catch (IOException e) {
            throw new VmConfiguratorException("Cannot open the file " + file, e.getCause());
        } catch (ParserConfigurationException e) {
            throw new VmConfiguratorException("Cannot parse the file " + file, e.getCause());
        } catch (SAXException e) {
            throw new VmConfiguratorException("Cannot parse the file " + file, e.getCause());
        }
        return result;
    }

}
