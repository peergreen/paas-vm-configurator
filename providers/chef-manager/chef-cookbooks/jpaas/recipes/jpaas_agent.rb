# ---------------------------------------------------------------------------
# JPaaS
# Copyright (C) 2011-2012 Bull S.A.S.
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
        install_url "http://10.197.180.20/rep-app/private/binaries/jpaas-agent-0.0.1-SNAPSHOT.zip"
        action [ :create, :start ]
    end