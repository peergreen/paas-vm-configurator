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
end








