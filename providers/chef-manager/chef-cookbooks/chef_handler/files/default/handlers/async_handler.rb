require "chef/handler"

module Stomp
  class Summarize < Chef::Handler
    attr_accessor :conf, :conffile

    def initialize(args)
      self.conffile = args[:configfile]
    end

    def report
      self.conf = YAML.load_file(self.conffile)
      begin
        require "rubygems"
        require "stomp"
        require "base64"

        ignored_resources = [ "/etc/chef/ohai_plugins", "/var/cache/chef/handlers", "/var/cache/chef/backup/handlers" ]
        report_resources= []
        updated_resources.each do |r|
          if !ignored_resources.include?(r.name) then
            report_resources.push r.name
          end
        end

        diffs = {}
        require 'find'
        report_resources.each do |r|
          # does it look like a diff-able file ?
          if r.match(/.conf/) then
            # find the latest backup
            bkps = Dir.glob(Chef::Config[:file_backup_path]+r+".chef-*")
            # found !
            unless bkps.empty?
              last_bkp = bkps[-1]
              diff_cmd = "diff -Nru #{last_bkp} #{r}"
              diffs[r] = %x(#{diff_cmd})
            end
          end
        end

        cnx=Stomp::Client.new(self.conf[:stomp_user], self.conf[:stomp_password], self.conf[:stomp_server], self.conf[:stomp_port], true)
        msg="<report><nodename>" + node.name.dup + "</nodename>" + "<ipaddress>" + node.ipaddress.to_s + "</ipaddress>" + "<success>" + run_status.success?.to_s + "</success>" + "<start_time>" + start_time.to_s + "</start_time>" + "<run_list>" + node.run_list.to_s + "</run_list>" + "</report>" 
        cnx.publish(self.conf[:stomp_queue], msg, {:persistent => false, :suppress_content_length => true})
        Chef::Log.debug("Report is sent")
		 
        cnx.close
      rescue Exception => e
        Chef::Log.error("Could not summarize and send back data for this run ! (#{e})")
      end
    end

  end # end of class Summarize
end
