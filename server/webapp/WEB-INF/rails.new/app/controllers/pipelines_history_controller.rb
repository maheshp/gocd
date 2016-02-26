##########################GO-LICENSE-START################################
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
##########################GO-LICENSE-END##################################

class PipelinesHistoryController < ApplicationController
  # before_filter :check_user_and_404
  # before_filter :check_user_can_see_pipeline

  def index
    @pipeline_name = go_config_service.pipelineConfigNamed(CaseInsensitiveString.new(params[:pipeline_name])).name()
    @pipeline_comment_feature_enabled = Toggles.isToggleOn(Toggles.PIPELINE_COMMENT_FEATURE_TOGGLE_KEY)
  rescue PipelineNotFoundException => e
    render_error_template(e.getMessage(), 404)
  end
end