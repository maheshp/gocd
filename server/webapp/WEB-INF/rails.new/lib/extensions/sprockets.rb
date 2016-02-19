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

#Hack to exclude old css from being autoprefixed

AutoprefixerRails::Sprockets.class_eval do
  def install(assets, opts = {})
    assets.register_postprocessor('text/css', :autoprefixer) do |context, css|
      css = process(context, css, opts) if new_css? context.pathname.to_s
      css
    end
  end

  private
  def new_css?(path)
    new_css_paths = ['pipeline_configs']

    new_css_paths.any? do |a|
      path.include? a
    end
  end
end
