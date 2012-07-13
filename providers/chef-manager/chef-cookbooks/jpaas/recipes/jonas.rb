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
# Recipe:: jonas
#


#package "sun-java6-jdk" do
#  action :install
#end

package "ant" do
  action :install
end


if !node["jpaas"]["jonas"].nil? then

  node["jpaas"]["jonas"].each do |jonas_instance|

    jpaas_jonas jonas_instance[:id] do
      java_home "/usr/lib/jvm/java-6-sun/"
      host_name "localhost"
      jonas_base "/opt/jonas_base/"+jonas_instance[:id]
      #install_url "http://download.forge.objectweb.org/jonas/jonas-full-5.3.0-M7-bin.zip"
      install_url "http://repository.ow2.org/nexus/content/repositories/snapshots/org/ow2/jonas/assemblies/profiles/legacy/jonas-full/5.3.0-M7-SNAPSHOT/jonas-full-5.3.0-M7-20120427.082730-49-bin.zip"
      action [ :create, :start ]
    end
  end
  
end


