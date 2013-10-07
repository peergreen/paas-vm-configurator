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
