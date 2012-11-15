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
