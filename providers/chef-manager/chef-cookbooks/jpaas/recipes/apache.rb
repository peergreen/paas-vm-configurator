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
# Recipe:: apache
#



package "apache2" do
  action :install
end

package "apache2-mpm-prefork" do
  action :install
end

package "libapache2-mod-jk" do
  action :install
end


def mod_jk_enable?
  ::File.exist?("etc/apache2/mods-enabled/jk.load")
end

def rewrite_enable?
  ::File.exist?("etc/apache2/mods-enabled/rewrite.load")
end

def proxy_enable?
  ::File.exist?("etc/apache2/mods-enabled/proxy.load")
end

def proxy_http_enable?
  ::File.exist?("etc/apache2/mods-enabled/proxy_http.load")
end

def proxy_ajp_enable?
  ::File.exist?("etc/apache2/mods-enabled/proxy_ajp.load")
end

def proxy_balancer_enable?
  ::File.exist?("etc/apache2/mods-enabled/proxy_balancer.load")
end

if !rewrite_enable? then
  bash "enable rewrite" do
    code "a2enmod rewrite"
  end
end

if !proxy_enable? then
  bash "enable proxy" do
    code "a2enmod proxy"
  end
end

if !proxy_http_enable? then
  bash "enable proxy_http" do
    code "a2enmod proxy_http"
  end
end

if !proxy_ajp_enable? then
  bash "enable proxy_ajp" do
    code "a2enmod proxy_ajp"
  end
end

if !proxy_balancer_enable? then
  bash "enable proxy_balancer" do
    code "a2enmod proxy_balancer"
  end
end

if !mod_jk_enable? then
  bash "enable mod_jk" do
    code "a2enmod jk; mkdir /etc/apache2/jk; > /etc/apache2/jk/workers.properties"
  end
  service "apache2" do
    action :restart
  end
end
