##########################################################################
# Copyright 2016 ThoughtWorks, Inc.
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
##########################################################################

module NotLoadedByDefault
  module BaseHelperForAuthentication
    def verify_user_and_404
      return unless security_service.isSecurityEnabled()
      if current_user.try(:isAnonymous)
        Rails.logger.info("User '#{current_user.getUsername}' attempted to perform an unauthorized action!")
        yield
      end
    end

    def check_user_and_401
      return unless security_service.isSecurityEnabled()
      if current_user.try(:isAnonymous)
        Rails.logger.info("User '#{current_user.getUsername}' attempted to perform an unauthorized action!")
        yield
      end
    end
    def verify_user_can_see_pipeline
      return unless security_service.isSecurityEnabled()
      unless security_service.hasViewPermissionForPipeline(string_username, params[:pipeline_name])
        Rails.logger.info("User '#{current_user.getUsername}' attempted to perform an unauthorized action!")
        yield
      end
    end

    def verify_admin_user_and_401
      return unless security_service.isSecurityEnabled()
      unless security_service.isUserAdmin(current_user)
        Rails.logger.info("User '#{current_user.getUsername}' attempted to perform an unauthorized action!")
        yield
      end
    end
  end
end