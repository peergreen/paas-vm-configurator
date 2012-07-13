template "etc/chef/summarize_handler.conf" do
  source "configfile.erb"
  mode "0600"
  owner 0
  group 0
  variables(
    :stomp_user => node["chef_handler"]["stomp_user"],
    :stomp_password => node["chef_handler"]["stomp_password"],
    :stomp_server => node["chef_handler"]["stomp_server"],
    :stomp_port => node["chef_handler"]["stomp_port"],
    :stomp_queue => node["chef_handler"]["stomp_queue"]
  )
end

chef_handler "Chef::Handler::Stomp::Summarize" do
  source "#{File.expand_path(File.join(Chef::Config[:file_cache_path],'..'))}/handlers/async_handler.rb"
  arguments :configfile => "etc/chef/summarize_handler.conf"
  action :nothing
end.run_action(:enable)

