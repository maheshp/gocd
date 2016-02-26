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
    describe :as_user do
      before(:each) do
        login_as_user
        controller.stub(:populate_health_messages)
        controller.stub(:populate_config_validity)

        @go_config_service = stub_service(:go_config_service)
      end

      it 'should assign the pipeline name from config' do
        pipeline_name_in_request_different_case = 'BUILD_LINUX'
        pipeline_name_in_config = 'Build_linux'

        pipeline_config = PipelineConfigMother.pipelineConfig(pipeline_name_in_config)

        allow_current_user_to_access_pipeline(pipeline_name_in_request_different_case)
        @go_config_service.should_receive(:pipelineConfigNamed)
            .with(CaseInsensitiveString.new(pipeline_name_in_request_different_case)).and_return(pipeline_config)

        get :index, :pipeline_name => pipeline_name_in_request_different_case

        expect(response).to be_ok
        expect(assigns[:pipeline_name]).to eq(pipeline_config.name())
      end

      it 'should fail if pipeline does not exist' do
        allow_current_user_to_access_pipeline('pipeline_does_not_exist')
        @go_config_service.should_receive(:pipelineConfigNamed).with(anything).and_raise(PipelineNotFoundException.new('pipeline_does_not_exist'))

        get :index, :pipeline_name => 'pipeline_does_not_exist'

        expect(response.status).to be(404)
      end

      it 'should verify if user has permission to view the pipeline history' do
        allow_current_user_to_not_access_pipeline('pipeline_name')

        get :index, :pipeline_name => 'pipeline_name'

        expect(response.code).to eq('401')
      end
    end

    describe :as_anonymous_user do
      before(:each) do
        enable_security
        login_as_anonymous
      end

      it 'should return 404' do
        get :index, :pipeline_name => 'pipeline_name'

        expect(response.code).to eq('404')
      end
    end
  end

  describe :details do
    describe :as_user do
      before(:each) do
        login_as_user
        @per_page = '10'
        @start = '1'
        @pipeline_history_json = '"{\"pipelineName\":\"admin\",\"paused\":\"false\",\"pauseCause\":\"\",\"pauseBy\":\"\",\"canForce\":\"true\"}'

        controller.stub(:populate_health_messages)
        controller.stub(:populate_config_validity)

        @pipeline_history_service = stub_service(:pipeline_history_service)
      end

      it 'should return pipeline history as json' do
        result = double('result', :isSuccessful => true)

        allow_current_user_to_access_pipeline('pipeline_name')
        HttpLocalizedOperationResult.stub(:new).and_return(result)
        @pipeline_history_service.should_receive(:pipelineHistoryJson).with('pipeline_name', @per_page.to_i, @start.to_i, result).and_return(@pipeline_history_json)

        get :details, :pipeline_name => 'pipeline_name', :perPage => @per_page, :start => @start

        expect(response).to be_ok
        expect(response.body).to eq(@pipeline_history_json)
      end

      it 'should fail if history service returns a error result' do
        allow_current_user_to_access_pipeline('pipeline_name')

        error_result = double('result', :isSuccessful => false, :httpCode => 404, :message => 'error')
        HttpLocalizedOperationResult.stub(:new).and_return(error_result)
        @pipeline_history_service.should_receive(:pipelineHistoryJson).with('pipeline_name', @per_page.to_i, @start.to_i, error_result).and_return(nil)

        get :details, :pipeline_name => 'pipeline_name', :perPage => @per_page, :start => @start

        expect(response.code).to eq('404')
      end
    end

    describe :as_anonymous_user do
      before(:each) do
        enable_security
        login_as_anonymous
      end

      it 'should return 404' do
        get :details, :pipeline_name => 'pipeline_name'

        expect(response.code).to eq('404')
      end
    end
  end
end