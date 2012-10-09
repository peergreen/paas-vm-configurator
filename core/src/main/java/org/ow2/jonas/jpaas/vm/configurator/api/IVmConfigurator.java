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
