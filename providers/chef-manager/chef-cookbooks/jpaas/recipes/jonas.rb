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

if !node["jpaas"]["jonas"]["apply_to"].nil? then

  node["jpaas"]["jonas"]["apply_to"].each do |jonas_instance|

    jpaas_jonas jonas_instance do
      java_home "/usr/lib/jvm/java-6-sun/"
      host_name "localhost"
      jonas_base "/opt/jonas_base/"+jonas_instance
      server_id jonas_instance
      install_url node["jpaas"]["jonas_download_url"]
      action [ :create ]
    end
  end

end