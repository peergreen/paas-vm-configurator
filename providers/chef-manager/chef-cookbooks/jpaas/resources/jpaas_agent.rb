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
# resource:: jpaas_agent.rb
#

actions :create, :start

attribute :agent_version, :kind_of => String, :default => "5.3.0-M7-SNAPSHOT"
attribute :repository_url, :kind_of => String, :default => "http://repository.ow2.org/nexus/service/local/repositories/"
attribute :metadata_url, :kind_of => String
attribute :metadata_xml, :kind_of => String
attribute :metadata, :kind_of => String
attribute :timpestamp, :kind_of => String
attribute :build_number, :kind_of => String
attribute :agent_home,	:kind_of => String, :default => "/opt/jpaas_agent"
attribute :jonas_user,	:regex => /^([a-z]|[A-Z]|[0-9]|_|-)+$/, :default => "jonas"
attribute :jonas_group,	:regex => /^([a-z]|[A-Z]|[0-9]|_|-)+$/, :default => "jonas"
attribute :install_url,	:kind_of => String, :default => "http://repository.ow2.org/nexus/content/repositories/snapshots/org/ow2/jonas/jpaas/agent/jpaas-agent/0.0.1-SNAPSHOT/jpaas-agent-0.0.1-20120710.115003-1.zip"



def isSnapshot?
  agent_version =~ /-SNAPSHOT$/
end

def download_metadata url
  cmd = "wget " + url + " -O /tmp/maven-metadata.xml --timeout=30 --tries=3"
  `#{cmd}`
end
