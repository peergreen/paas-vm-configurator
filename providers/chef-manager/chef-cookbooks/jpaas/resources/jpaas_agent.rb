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
# resource:: jpaas_agent.rb
#

actions :create, :start, :stop, :undeploy

attribute :agent_version, :kind_of => String, :default => "5.3.0-M7-SNAPSHOT"
attribute :repository_url, :kind_of => String, :default => "http://repository.ow2.org/nexus/service/local/repositories/"
attribute :agent_home, :kind_of => String, :default => "/opt/jpaas_agent"
attribute :install_url, :kind_of => String, :default => "http://repository.ow2.org/nexus/content/repositories/snapshots/org/ow2/jonas/jpaas/agent/jpaas-agent/0.0.1-SNAPSHOT/jpaas-agent-0.0.1-20120726.145212-15.zip"


def isSnapshot?
  agent_version =~ /-SNAPSHOT$/
end

def download_metadata url
  cmd = "wget " + url + " -O /tmp/maven-metadata.xml --timeout=30 --tries=3"
  `#{cmd}`
end
