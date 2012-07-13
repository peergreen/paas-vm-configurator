# ---------------------------------------------------------------------------
# JPaaS
# Copyright (C) 2011 Bull S.A.S.
# Contact: jasmine@ow2.org
#
# This library is free software; you can redistribute it and/or
# modify it under the terms of the GNU Lesser General Public
# License as published by the Free Software Foundation; either
# version 2.1 of the License, or any later version.
#
# This library is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
# Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public
# License along with this library; if not, write to the Free Software
# Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
# USA
#
#
# ---------------------------------------------------------------------------
# $Id$
# ---------------------------------------------------------------------------

#
# Cookbook Name:: jpaas
# provider:: jpaas_agent.rb

action :create do
  if !binary_exists? then

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
  if !jpaas_agent_already_started? then
    execute "start-jpaas-agent" do
      #user new_resource.jonas_user
      #group new_resource.jonas_group
      command "export JPAAS_ROOT=JONAS_BASE=JONAS_ROOT=" + new_resource.agent_home + ";" + new_resource.agent_home + "/bin/jonas start -n jpaas-agent -clean"
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
