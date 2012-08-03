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
