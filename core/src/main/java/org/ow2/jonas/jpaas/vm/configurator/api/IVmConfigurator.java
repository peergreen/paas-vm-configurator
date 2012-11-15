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
package org.ow2.jonas.jpaas.vm.configurator.api;


import java.util.concurrent.Future;

/**
 * Interface for the VmConfigurator.
 * @author David Richard
 */
public interface IVmConfigurator {

    /**
     * Install an Agent on an Iaas Compute
     *
     * @param agentName the name of the Agent
     * @param computeName the name of the IaaS Compute
     * @return the PaasConfiguration
     */
    public Future installAgent(String agentName, String computeName) throws VmConfiguratorException;

    /**
     * Install a Software on an Iaas Compute
     *
     * @param computeName the name of the Iaas Compute
     * @param paasConfigurationName the name of the PaaS configuration
     * @return the PaasConfiguration
     */
    public Future installSoftware(String computeName, String paasConfigurationName) throws VmConfiguratorException;


    /**
     * Release Chef resource : remove the IaaS Compute information on the Chef Server
     *
     * @param computeName the name of the Iaas Compute
     */
    public void releaseResource(String computeName) throws VmConfiguratorException;

}
