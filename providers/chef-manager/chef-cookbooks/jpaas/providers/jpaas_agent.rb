#
# JPaaS
# Copyright 2012 Bull S.A.S.
# Contact: jonas@ow2.org
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# $Id:$

#
# Cookbook Name:: jpaas
# provider:: jpaas_agent.rb

#Max retry for a Rest request
@@rest_max_retry = 10

#Timeout for a Rest request
@@rest_request_timeout = 300

#Sleep time before retrying a REST request
@@retrying_rest_sleep_time = 5


action :create do
  unless binary_exists? then

    remote_file "/tmp/jpaas-agent.zip" do
      source new_resource.install_url
      :create
    end

    package "unzip" do
      action :install
    end

    directory "/opt/jpaas-agent-" + new_resource.agent_version do
      action :create
    end

    execute "decompress-jpaas-agent-binary" do
      command "unzip -o /tmp/jpaas-agent.zip"
      cwd "/opt"
    end

    link new_resource.agent_home do
      to "/opt/jpaas-agent-" + new_resource.agent_version
      link_type :symbolic
    end

  end


end

action :start do
  unless jpaas_agent_already_started? then
    execute "start-jpaas-agent" do
      command "export JPAAS_ROOT=" + new_resource.agent_home + ";" + new_resource.agent_home + "/jpaas-agent.sh start"
    end

    ruby_block "test REST service" do
      block do
        url = URI.parse("http://localhost:" + node["jpaas"]["jpaas_agent_port"] + "/jonas-api/server")
        http = Net::HTTP.new(url.host, url.port)
        http.read_timeout = @@rest_request_timeout

        testIsValid = false
        retry_number = 0
        #Retry to be sure that the required service is started in the JOnAS agent
        begin
          while testIsValid == false && retry_number < @@rest_max_retry
            Chef::Log.debug("ruby_block[test REST service] : retry number = " + retry_number.to_s)
            response = http.get(url.path)
            Chef::Log.debug("ruby_block[test REST service] : Request return code value = " + response.code)
            if response.code == "200" then
              testIsValid = true
              Chef::Log.info("ruby_block[test REST service] : REST service started")
            else
              sleep @@retrying_rest_sleep_time
            end
            retry_number += 1
          end
            #Avoid Errno::ECONNREFUSED when REST server is not started
        rescue Exception
          sleep @@retrying_rest_sleep_time
          retry_number += 1
          retry
        end
      end
    end

  end
end

action :stop do
  if jpaas_agent_already_started? then
    execute "stop-jpaas-agent" do
      returns 2
      command "export JONAS_BASE= ; export JONAS_ROOT=" + new_resource.agent_home + ";" + new_resource.agent_home + "/bin/jpaas stop -n jpaas-agent"
    end
  end

  directory new_resource.agent_home + "/work" do
    recursive true
    action :delete
  end

end

action :undeploy do
  if binary_exists? then
    Directory "/opt/jpaas-agent-" + new_resource.agent_version do
      recursive true
      action :delete
    end

    link new_resource.agent_home do
      action :delete
    end
  end
end

def binary_exists?
  ::File.exist?(new_resource.agent_home + "/bin/jonas")
end

def jpaas_agent_already_started?
  cmd = new_resource.agent_home + "/bin/jonas admin -n jpaas-agent -ping -timeout 5000; echo $? > /tmp/test_jpaas_agent"
  value = `#{cmd}`
  value = `cat /tmp/test_jpaas_agent`
  return Integer(value) == 0
end
