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


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ow2.easybeans.osgi.annotation.OSGiResource;
import org.ow2.jonas.jpaas.sr.facade.api.ISrIaasComputeFacade;
import org.ow2.jonas.jpaas.sr.facade.api.ISrPaasAgentFacade;
import org.ow2.jonas.jpaas.sr.facade.api.ISrPaasAgentIaasComputeLink;
import org.ow2.jonas.jpaas.sr.facade.vo.IaasComputeVO;
import org.ow2.jonas.jpaas.sr.facade.vo.PaasAgentVO;
import org.ow2.jonas.jpaas.vm.configurator.api.VmConfiguratorException;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * VmConfigurator Message Driven Bean
 * @author David Richard
 */
@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "sampleQueue"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class VmConfiguratorMdb implements MessageListener {

    @OSGiResource
    ISrIaasComputeFacade iSrIaasComputeFacade = null;

    @OSGiResource
    ISrPaasAgentFacade iSrPaasAgentFacade = null;

    @OSGiResource
    ISrPaasAgentIaasComputeLink iSrPaasAgentIaasComputeLink = null;

    /**
     * Logger
     */
    private static Log logger = LogFactory.getLog(VmConfiguratorMdb.class);

    @Override
    public void onMessage(Message message) {

        String txt = "Receiving a message named '" + message + "'.";
        if (message instanceof TextMessage) {
            try {
                String msgContent = ((TextMessage) message).getText();
                txt += " with the content '" + msgContent ;
                logger.debug("Message : " + txt);
                JSONObject jsonReport = new JSONObject(msgContent);
                if (jsonReport.getBoolean("success")) {
                    String nodeIp = jsonReport.getString("ipaddress");
                    JSONArray runlist = jsonReport.getJSONArray("run_list");
                    List<String> roleList = new ArrayList<String>();
                    for (int i = 0; i < runlist.length(); i++) {
                        String tmp = runlist.getString(i);
                        Pattern pattern = Pattern.compile("\\[(.*?)\\]");
                        Matcher matcher = pattern.matcher(tmp);
                        matcher.find();
                        String role = matcher.group(1);
                        roleList.add(role);

                        //Update the PaasAgent status if the role name is "Agent"
                        if (role.equalsIgnoreCase("Agent")) {
                            updatePaasAgentState("RUNNING", nodeIp);
                        }
                    }
                    //Update the IaasCompute roles list
                    updateIaasComputeRoles(roleList, nodeIp);
                }
            } catch (JMSException e) {
                System.err.println("Error while getting the content of the message");
                e.printStackTrace();
            } catch (IllegalStateException e) {
                System.err.println("Error while getting the content of the report");
                e.printStackTrace();
            } catch (JSONException e) {
                System.err.println("Error while getting the content of the report");
                e.printStackTrace();
            } catch (VmConfiguratorException e) {
                System.err.println("Error while updating the SR");
                e.printStackTrace();
            }

        }
    }

    /**
     * Update the IaasCompute roles list
     * @param roleList the new list of roles of the IaasCompute
     * @param iaasComputeIp The IaasCompute ip address
     * @throws VmConfiguratorException
     */
    private void updateIaasComputeRoles(List<String> roleList, String iaasComputeIp) throws VmConfiguratorException {
        logger.debug("updateIaasComputeRole(" + roleList.toString() + ", " + iaasComputeIp + ")");
        List<IaasComputeVO> iaasComputeList = iSrIaasComputeFacade.findIaasComputes();
        String computeId = null;
        for (IaasComputeVO tmp : iaasComputeList) {
            if (tmp.getIpAddress().equals(iaasComputeIp)) {
                computeId = tmp.getId();
                break;
            }
        }
        if (computeId == null) {
            throw new VmConfiguratorException("Cannot find the Compute with the ip " + iaasComputeIp);
        } else {
            IaasComputeVO iaasComputeVO = iSrIaasComputeFacade.getIaasCompute(computeId);
            iaasComputeVO.setRoles(roleList);
            iSrIaasComputeFacade.updateIaasCompute(iaasComputeVO);
        }
    }

    /**
     * Update the IaasCompute's PaasAgent state
     * @param state the new state of the PaasAgent
     * @param iaasComputeIp The IaasCompute ip address
     * @throws VmConfiguratorException
     */
    private void updatePaasAgentState(String state, String iaasComputeIp) throws VmConfiguratorException {
        logger.debug("updatePaasAgentState(" + state + ", " + iaasComputeIp + ")");
        List<IaasComputeVO> iaasComputeList = iSrIaasComputeFacade.findIaasComputes();
        String computeId = null;
        for (IaasComputeVO tmp : iaasComputeList) {
            if (tmp.getIpAddress().equals(iaasComputeIp)) {
                computeId = tmp.getId();
                break;
            }
        }
        if (computeId == null) {
            throw new VmConfiguratorException("Cannot find the Compute with the ip " + iaasComputeIp);
        } else {
            List<PaasAgentVO> paasAgentVOList = iSrPaasAgentIaasComputeLink.findPaasAgentsByIaasCompute(computeId);
            for (PaasAgentVO paasAgentVO : paasAgentVOList) {
                paasAgentVO.setState(state);
                iSrPaasAgentFacade.updateAgent(paasAgentVO);
            }
        }
    }
}
