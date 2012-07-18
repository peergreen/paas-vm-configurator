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


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ow2.easybeans.osgi.annotation.OSGiResource;
import org.ow2.jonas.jpaas.sr.facade.api.ISrIaasComputeFacade;
import org.ow2.jonas.jpaas.sr.facade.vo.IaasComputeVO;
import org.ow2.jonas.jpaas.vm.configurator.api.VmConfiguratorException;
import org.ow2.jonas.jpaas.vm.configurator.providers.chef.manager.api.ChefManagerException;
import org.ow2.jonas.jpaas.vm.configurator.providers.chef.manager.api.ChefManagerService;
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
                    }
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
}
