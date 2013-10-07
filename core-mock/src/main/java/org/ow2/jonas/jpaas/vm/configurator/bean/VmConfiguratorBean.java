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
package org.ow2.jonas.jpaas.vm.configurator.bean;


import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.ow2.jonas.jpaas.catalog.api.IPaasCatalogFacade;
import org.ow2.jonas.jpaas.catalog.api.PaasCatalogException;
import org.ow2.jonas.jpaas.catalog.api.PaasConfiguration;
import org.ow2.jonas.jpaas.sr.facade.api.ISrIaasComputeFacade;
import org.ow2.jonas.jpaas.sr.facade.api.ISrPaasAgentFacade;
import org.ow2.jonas.jpaas.sr.facade.api.ISrPaasAgentIaasComputeLink;
import org.ow2.jonas.jpaas.sr.facade.vo.IaasComputeVO;
import org.ow2.jonas.jpaas.sr.facade.vo.PaasAgentVO;
import org.ow2.jonas.jpaas.vm.configurator.api.IVmConfigurator;
import org.ow2.jonas.jpaas.vm.configurator.api.VmConfiguratorException;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;


@Component
@Provides
@Instantiate
public class VmConfiguratorBean implements IVmConfigurator {

    @Requires
    ISrIaasComputeFacade iSrIaasComputeFacade = null;

    @Requires
    IPaasCatalogFacade iPaasCatalogFacade = null;

    @Requires
    ISrPaasAgentFacade iSrPaasAgentFacade = null;

    @Requires
    ISrPaasAgentIaasComputeLink iSrPaasAgentIaasComputeLink = null;

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


            if (!roleIsPresent("Agent", iaasComputeVO.getRoles())) {
                //add Agent role at the end of the run list
                //chefManagerService.addRoleToIpNodeRunListEnd(iaasComputeVO.getIpAddress(), "Agent");

                //create agent in SR
                PaasAgentVO paasAgentVO = new PaasAgentVO() ;
                paasAgentVO.setState("RUNNING");
                paasAgentVO.setName(agentName);
                //ToDO Non hard-coded port
                paasAgentVO.setApiUrl("http://" + iaasComputeVO.getIpAddress() + ":9000");
                paasAgentVO = iSrPaasAgentFacade.createAgent(paasAgentVO);

                //create the link between the Agent and the IaasCompute
                iSrPaasAgentIaasComputeLink.addPaasAgentIaasComputeLink(paasAgentVO.getId(), iaasComputeVO.getId());

            } else {
                throw new VmConfiguratorException("jPaaS Agent is already installed.");
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

            List<String> roles = iaasComputeVO.getRoles();
            roles.add(chefRole);
            iaasComputeVO.setRoles(roles);
            iSrIaasComputeFacade.updateIaasCompute(iaasComputeVO);


        } catch (PaasCatalogException e) {
            throw new VmConfiguratorException("Error to find the PaaS Configuration named " +
                    paasConfigurationName + ".", e);
        }
        return null;
    }

    /**
     * Release Chef resource : unregister the IaaS Compute on the Chef Server
     *
     * @param computeName the name of the Iaas Compute
     */
    @Override
    public void releaseResource(String computeName) throws VmConfiguratorException {
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
            throw new VmConfiguratorException("Cannot open the file " + file, e);
        } catch (ParserConfigurationException e) {
            throw new VmConfiguratorException("Cannot parse the file " + file, e);
        } catch (SAXException e) {
            throw new VmConfiguratorException("Cannot parse the file " + file, e);
        }
        return result;
    }

}
