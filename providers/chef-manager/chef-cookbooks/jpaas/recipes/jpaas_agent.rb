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
# Recipe:: jpaas_agent
#

    jpaas_jpaas_agent "jpaas_agent" do
      agent_version "0.0.1-SNAPSHOT"
      agent_home "/opt/jpaas_agent"
      #Use local repository instead of external url
=begin
      if isSnapshot?
        require 'rexml/document'
        metadata_url = repository_url + "snapshots/content/org/ow2/jonas/jpaas/agent/jpaas-agent/" + agent_version + "/maven-metadata.xml"
        download_metadata metadata_url
        metadata_xml = File.read("/tmp/maven-metadata.xml")
        metadata = REXML::Document.new(metadata_xml).root
        timestamp = REXML::XPath.first(metadata, '//timestamp').text
        build_number = REXML::XPath.first(metadata, '//buildNumber').text
        install_url repository_url + "snapshots/content/org/ow2/jonas/jpaas/agent/jpaas-agent/" + agent_version + "/jpaas-agent-" + agent_version.split("-SNAPSHOT").first + "-" + timestamp + "-" + build_number +".zip"
      end
=end
        #Set the url of jpaas-agent binary
        install_url "http://10.197.180.20:8080/rep-app/private/binaries/jpaas-agent-0.0.1-SNAPSHOT.zip"
        action [ :create, :start ]
    end
