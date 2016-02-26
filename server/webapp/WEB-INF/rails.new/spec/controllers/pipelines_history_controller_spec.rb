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

require 'spec_helper'

describe PipelinesHistoryController do
  describe :index do
    before(:each) do
      controller.stub(:populate_health_messages)
      controller.stub(:populate_config_validity)
      @go_config_service = stub_service(:go_config_service)
    end

    it 'should assign the pipeline name from config' do
      pipeline_name_in_request_different_case = 'BUILD_LINUX'
      pipeline_name_in_config = 'Build_linux'

      pipeline_config = PipelineConfigMother.pipelineConfig(pipeline_name_in_config)

      @go_config_service.should_receive(:pipelineConfigNamed)
          .with(CaseInsensitiveString.new(pipeline_name_in_request_different_case)).and_return(pipeline_config)

      get :index, :pipeline_name => pipeline_name_in_request_different_case

      expect(response).to be_ok
      expect(assigns[:pipeline_name]).to eq(pipeline_config.name())
    end

    it 'should fail if pipeline does not exist' do
      @go_config_service.should_receive(:pipelineConfigNamed).with(anything).and_raise(PipelineNotFoundException.new('pipeline_does_not_exist'))

      get :index, :pipeline_name => 'pipeline_does_not_exist'

      expect(response.status).to be(404)
    end
  end
end