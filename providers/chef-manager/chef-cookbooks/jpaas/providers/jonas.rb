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
# provider:: jonas.rb


#Max retry for a Rest request
@@rest_max_retry = 5

#Timeout for a Rest request
@@rest_request_timeout = 300

#Sleep time before retrying a REST request
@@retrying_rest_sleep_time = 5

#Provision server specific time
@@provision_sleep_time = 5

#Start server specific time
@@start_sleep_time = 15



#library to make REST request
require 'net/http'

#library to parse XML
require 'rexml/document'


action :create do
  if !binary_exists? then

    remote_file "/tmp/jonas-full.zip" do
      source new_resource.install_url
      :create
    end

    #bash "download-jonas" do
    #  code "wget " + new_resource.install_url + " -O /tmp/jonas-full.zip"
    # end

    package "unzip" do
      action :install
    end

    execute "unzip-jonas-binary" do
      command "unzip -o /tmp/jonas-full.zip"
      cwd "/opt"
    end

    link new_resource.jonas_home do
      to "/opt/"+::File.basename(new_resource.install_url).split(/-bin.zip$/)[0]
      link_type :symbolic
    end

  end

  if !instance_exists? then
    user new_resource.jonas_user do
      system true
      comment "Service user for JonAS application server"
    end

    group new_resource.jonas_group do
      members "jonas"
    end


    directory new_resource.jonas_base do
      #owner new_resource.jonas_user
      #group new_resource.jonas_group
      #mode 0755
      recursive true
    end



    # create topology xml
    template "/tmp/topology.xml" do
      source "topology.xml.erb"
      variables(
          :domain_name => new_resource.domain_name,
          :jonas_home => new_resource.jonas_home,
          :java_home => new_resource.java_home,
          :server_name => new_resource.server_id,
          :jonas_base => new_resource.jonas_base,
          :host_name => new_resource.host_name,
          :jrmp_port => new_resource.jrmp_port,
          :http_port => new_resource.http_port,
          :internal_database_port => new_resource.internal_database_port
      )
    end


    #provision the JOnAS server
    ruby_block "provision server" do
      block do
        data = ::File.read('/tmp/topology.xml')
        #Rest request to provision
        url = URI.parse("http://localhost:" + node["jpaas"]["jpaas_agent_port"] + "/jonas-api/server/" + new_resource.server_id)
        http = Net::HTTP.new(url.host, url.port)
        http.read_timeout = @@rest_request_timeout

        task_is_created = false
        retry_number = 0
        #Retry to be sure that the required service is started in the JOnAS agent
        begin
          while task_is_created == false && retry_number < @@rest_max_retry
            Chef::Log.debug("ruby_block[provision server] : retry number = " + retry_number.to_s)
            response = http.put(url.path, data, {'Content-type'=>'application/xml;charset=utf-8'})
            Chef::Log.debug("ruby_block[provision server] : Request return code value = " + response.code)
            if response.code == "202" then
              task_is_created = true
              xmlResponse = REXML::Document.new(response.body).root
              task_id = xmlResponse.attributes.get_attribute("id").value
              Chef::Log.debug("ruby_block[provision server] : task id = " + task_id.to_s)
              Chef::Log.info("ruby_block[provision server] : provision task created ")
            else
              sleep @@retrying_rest_sleep_time
            end
            retry_number += 1
          end
            #Avoid Errno::ECONNREFUSED when REST server is not started
        rescue Exception
          sleep @@retrying_rest_sleep_time
          retry_number += 1
          retry
        end
        if retry_number >= @@rest_max_retry then
          Chef::Log.error("ruby_block[provision server] : Max retries reached")
          raise "Max retries reached : JOnAS agent service not available"
        else  #Wait the SUCCESS of the provisioning task
              #Rest request to get specific task status
          url = URI.parse("http://localhost:" + node["jpaas"]["jpaas_agent_port"] + "/jonas-api/task/" + task_id )
          http = Net::HTTP.new(url.host, url.port)
          http.read_timeout = @@rest_request_timeout

          retry_number = 0
          is_provisioned = false
          #wait the end of the provisioning task
          while is_provisioned == false && retry_number < @@rest_max_retry
            Chef::Log.debug("ruby_block[provision server] : retry number = " + retry_number.to_s)
            request = Net::HTTP::Get.new(url.path)
            response = http.request(request)
            if response.code == "200" then
              xmlResponse = REXML::Document.new(response.body).root
              task_status = xmlResponse.attributes.get_attribute("status").value

              Chef::Log.debug("ruby_block[provision server] : task status = " + task_status.to_s)
              if task_status.to_s == "SUCCESS"
                is_provisioned = true
                Chef::Log.info("ruby_block[provision server] : server provisioned ")
              elsif task_status == "RUNNING"
                sleep @@provision_sleep_time
              elsif task_status.to_s == "ERROR"
                Chef::Log.error("ruby_block[provision server] : error in server provisioning")
                raise "Error in server provisioning"
              end

            else
              sleep @@retrying_rest_sleep_time
            end
            retry_number += 1
          end
          if retry_number >= @@rest_max_retry then
            Chef::Log.error("ruby_block[provision server] : Max retries reached")
            raise "Max retries reached : Server too long to be provisioned"
          end
        end
      end
    end

  end

end #end of create

action :start do


  #start the JOnAS server
  ruby_block "start server" do
    block do
      #if !node.attribute?("jonas_test") then

      #Rest request to start the server
      url = URI.parse("http://localhost:" + node["jpaas"]["jpaas_agent_port"] + "/jonas-api/server/" + new_resource.server_id + "/action/start")
      http = Net::HTTP.new(url.host, url.port)
      http.read_timeout = @@rest_request_timeout

      task_is_created = false
      retry_number = 0
      #Retry to be sure that the required service is started in the JOnAS agent
      begin
        while task_is_created == false && retry_number < @@rest_max_retry
          Chef::Log.debug("ruby_block[start server] : retry number = " + retry_number.to_s)
          request = Net::HTTP::Post.new(url.path)
          response = http.request(request)
          Chef::Log.debug("ruby_block[start server] : Request return code value = " + response.code)
          if response.code == "202" then
            task_is_created = true
            xmlResponse = REXML::Document.new(response.body).root
            task_id = xmlResponse.attributes.get_attribute("id").value
            Chef::Log.debug("ruby_block[provision server] : task id = " + task_id.to_s)
            Chef::Log.info("ruby_block[start server] : start task created")
          else
            sleep @@retrying_rest_sleep_time
          end
          retry_number += 1
        end
          #Avoid Errno::ECONNREFUSED when REST server is not started
      rescue Exception
        sleep @@retrying_rest_sleep_time
        retry_number += 1
        retry
      end
      if retry_number >= @@rest_max_retry then
        Chef::Log.error("ruby_block[start server] : Max retries reached")
        raise "Max retries reached : JOnAS agent service not available"
      else  #Wait the SUCCESS of the start task
            #Rest request to get specific task status
        url = URI.parse("http://localhost:" + node["jpaas"]["jpaas_agent_port"] + "/jonas-api/task/" + task_id )
        http = Net::HTTP.new(url.host, url.port)
        http.read_timeout = @@rest_request_timeout

        retry_number = 0
        is_started = false
        #wait the end of the start task
        while is_started == false && retry_number < @@rest_max_retry
          Chef::Log.debug("ruby_block[start server] : retry number = " + retry_number.to_s)
          request = Net::HTTP::Get.new(url.path)
          response = http.request(request)
          if response.code == "200" then
            xmlResponse = REXML::Document.new(response.body).root
            task_status = xmlResponse.attributes.get_attribute("status").value

            Chef::Log.debug("ruby_block[start server] : task status = " + task_status.to_s)
            if task_status.to_s == "SUCCESS"
              is_started = true
              #Do not work with Chef-Solo
              #sometimes do not work well (not saved instantly)
              node.normal[new_resource.server_id + "_started"] = true
              node.save
              Chef::Log.info("ruby_block[start server] : server started ")
            elsif task_status == "RUNNING"
              sleep @@start_sleep_time
            elsif task_status.to_s == "ERROR"
              Chef::Log.error("ruby_block[start server] : error to start the server")
              raise "Error to start the server"
            end

          else
            sleep @@retrying_rest_sleep_time
          end
          retry_number += 1
        end
        if retry_number >= @@rest_max_retry then
          Chef::Log.error("ruby_block[start server] : Max retries reached")
          raise "Max retries reached : Server too long to start"
        end
      end
    end
    #Do not execute this block if this attribute exists
    not_if { node.attribute?(new_resource.server_id + "_started") }
  end

end



def instance_exists?
  ::File.exist?(new_resource.jonas_base + "/deploy")
end

def binary_exists?
  ::File.exist?(new_resource.jonas_home+"/bin/jonas")
end






