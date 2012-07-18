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
# resource:: jonas.rb
#

actions :create, :provision, :start

attribute :java_home,	:kind_of => String, :required => true
attribute :host_name,	:regex => /^([a-z]|[A-Z]|[0-9]|_|-|.|:)+$/, :required => true
attribute :server_id,	:regex => /^([a-z]|[A-Z]|[0-9]|_|-)+$/, :name_attribute => true
attribute :jonas_home,	:kind_of => String, :default => "/opt/jonas"
attribute :domain_name,	:regex => /^([a-z]|[A-Z]|[0-9]|_|-)+$/, :default => "jonas"
attribute :jonas_base,	:kind_of => String
attribute :jrmp_port,	:kind_of => Integer, :default => 2099
attribute :http_port,	:kind_of => Integer, :default => 9010
attribute :internal_database_port,	:kind_of => Integer, :default => 9001
attribute :jonas_user,	:regex => /^([a-z]|[A-Z]|[0-9]|_|-)+$/, :default => "jonas"
attribute :jonas_group,	:regex => /^([a-z]|[A-Z]|[0-9]|_|-)+$/, :default => "jonas"
attribute :install_url,	:kind_of => String, :default => "http://download.forge.objectweb.org/jonas/jonas-full-5.3.0-M6-bin.zip"
