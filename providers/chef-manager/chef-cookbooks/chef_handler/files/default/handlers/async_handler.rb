require "chef/handler"

module Stomp
  class Summarize < Chef::Handler
    attr_accessor :conf, :conffile, :previous_report_file

    def initialize(args)
      self.conffile = args[:configfile]
    end

    def report
      self.conf = YAML.load_file(self.conffile)
      self.previous_report_file = Chef::Config[:file_backup_path] + "/previous_report.json"
      begin
        require "rubygems"
        require "stomp"
        require "base64"
        require "json"

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


        summary = { :nodename => node.name.dup,
                    :ipaddress => node.ipaddress,
                    :success => run_status.success?,
                    :start_time => start_time,
                    :end_time => end_time,
                    :run_list => node.run_list,
        }
        msg = summary.to_json
        #send only if there is no previous report with the same run list and success status
        if File::exist?(self.previous_report_file)
          previous_report = File::open(self.previous_report_file, "r+")
          previous_summary = JSON::load(previous_report)
          previous_report.close
          if previous_summary["success"] == true and (previous_summary["run_list"].to_json == summary[:run_list].to_json)
            Chef::Log.debug("The report was not send : the result is the same as the previous report")
          else
            send_report(msg)
          end
        else
          require 'fileutils'
          FileUtils::mkdir_p(Chef::Config[:file_backup_path])
          send_report(msg)
        end
        #write the summary in a file
        previous_report = File::new(self.previous_report_file, "w+")
        JSON::dump(summary, previous_report)
        previous_report.close
      rescue Exception => e
        Chef::Log.error("Could not summarize and send back data for this run ! (#{e})")
      end
    end

    def send_report msg
      begin
        require "stomp"
        require "base64"
        cnx=Stomp::Client.new(self.conf[:stomp_user], self.conf[:stomp_password], self.conf[:stomp_server], self.conf[:stomp_port], true)
        cnx.publish(self.conf[:stomp_queue], msg, {:persistent => false, :suppress_content_length => true})
        Chef::Log.debug("Report is sent")
        cnx.close
      end
    end

  end # end of class Summarize
end
