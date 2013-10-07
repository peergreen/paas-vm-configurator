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
