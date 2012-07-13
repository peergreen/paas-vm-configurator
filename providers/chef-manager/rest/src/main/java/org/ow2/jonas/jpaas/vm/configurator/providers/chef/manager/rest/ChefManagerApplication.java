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

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.ipojo.annotations.Validate;
import org.ow2.jonas.jpaas.vm.configurator.providers.chef.manager.api.ChefManagerService;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * REST application to provide the REST implementation of our services
 *
 * @author David Richard
 */
@Component(name="ChefManagerApplication", immediate = true)
@Provides(specifications={Application.class})
@Instantiate
public class ChefManagerApplication extends Application {

    private Log logger = LogFactory.getLog(this.getClass());

    @Requires
    private ChefManagerService chefManagerService;

    @ServiceProperty(name="jonas.jaxrs.context-path", value="/chefmanager")
    private String chefManagerContextName;

    public ChefManagerApplication(){
    }

    @Override
    public Set<Class<?>> getClasses() {
        return null;
    }

    @Override
    public Set<Object> getSingletons() {
        Set<Object> objects = new HashSet<Object>();
        objects.add(new ChefManagerRest(chefManagerService));
        return objects;
    }

    @Validate
    public void start() {
        logger.debug("ChefManagerApplication started");
    }


    @Invalidate
    public void stop() {
        logger.debug("ChefManagerApplication stopped");
    }
}
