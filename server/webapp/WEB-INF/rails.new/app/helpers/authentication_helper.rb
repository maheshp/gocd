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

module AuthenticationHelper
  include BaseAuthenticationHelper

  def check_user_and_404
    verify_user_and_404 do
      render_not_found_error
    end
  end

  def check_user_and_401
    verify_user_and_401 do
      render_unauthorized_error
    end
  end

  def check_user_can_see_pipeline
    verify_user_can_see_pipeline do
      render_unauthorized_error
    end
  end

  def check_admin_user_and_401
    verify_admin_user_and_401 do
      render_unauthorized_error
    end
  end

  def render_not_found_error
    render :json => {:message => 'Either the resource you requested was not found, or you are not authorized to perform this action.'}, :status => 404
  end

  def render_bad_request(exception)
    render :json => {:message => "Your request could not be processed. #{exception.message}"}, :status => 400
  end

  def render_unauthorized_error
    render :json => {:message => 'You are not authorized to perform this action.'}, :status => 401
  end
end