set :application, "registry-harvester"
set :repo_url, "https://github.com/sul-dlss/registry-harvester.git"
set :github_token, `git config --get github.token | tr -d '\n'`
set :user, "sirsi"

# Default branch is :master
ask :branch, `git rev-parse --abbrev-ref HEAD`.chomp

# Default deploy_to directory is /var/www/my_app_name
# set :deploy_to, "/var/www/my_app_name"
set :deploy_to, "/s/SUL/Harvester"

# Default value for :format is :airbrussh.
# set :format, :airbrussh

# You can configure the Airbrussh format using :format_options.
# These are the defaults.
# set :format_options, command_output: true, log_file: "log/capistrano.log", color: :auto, truncate: :auto

# Default value for :pty is false
# set :pty, true

# Default value for :linked_files is []
# append :linked_files, "config/database.yml", "config/secrets.yml"

# Default value for linked_dirs is []
# append :linked_dirs, "log", "tmp/pids", "tmp/cache", "tmp/sockets", "public/system"
set :linked_dirs, %w(Course/src/main/resources Person/src/main/resources etc certs log out course_files jar lib WebLogic_lib)

# Default value for default_env is {}
# set :default_env, { path: "/opt/ruby/bin:$PATH" }

# Default value for local_user is ENV['USER']
# set :local_user, -> { `git config user.name`.chomp }

# Default value for keep_releases is 5
set :keep_releases, 3

namespace :deploy do
  task :jars do
    desc 'Copy the JARs only'
      on roles(:app) do
         %w[ Course Person ].each do |mod|
            upload! "#{mod}/target/#{mod}-jar-with-dependencies.jar" , "#{release_path}/lib"
         end
      end
  end

  # task :folio do
  #   desc 'deploy the folio api client as a submodule'
  #   on roles(:all) do
  #     with fetch(:git_environmental_variables) do
  #       within repo_path do
  #         # execute :git, :clone, '-b', fetch(:branch), '--recursive', '.', "#{release_path}/folio_api_client"
  #         # execute :git, :clone, '--recursive', "https://#{fetch(:github_token)}@github.com/sul-dlss-labs/folio_api_client.git", " #{release_path}"
  #         execute :git, :
  #       end
  #     end
  #   end
  # end
end
