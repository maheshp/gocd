/*
 * Copyright 2019 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.thoughtworks.go.config;

import com.thoughtworks.go.domain.config.ConfigurationKey;
import com.thoughtworks.go.domain.config.ConfigurationProperty;
import com.thoughtworks.go.domain.config.ConfigurationValue;
import com.thoughtworks.go.domain.config.EncryptedConfigurationValue;
import com.thoughtworks.go.plugin.access.secrets.SecretsMetadataStore;
import com.thoughtworks.go.plugin.api.info.PluginDescriptor;
import com.thoughtworks.go.plugin.domain.common.Metadata;
import com.thoughtworks.go.plugin.domain.common.PluggableInstanceSettings;
import com.thoughtworks.go.plugin.domain.common.PluginConfiguration;
import com.thoughtworks.go.plugin.domain.secrets.SecretsPluginInfo;
import org.junit.After;
import org.junit.Test;

import java.util.ArrayList;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class SecretConfigTest extends PluginProfileTest{
    private SecretsMetadataStore store = SecretsMetadataStore.instance();

    @After
    public void tearDown() throws Exception {
        store.clear();
    }

    @Test
    public void addConfigurations_shouldAddConfigurationsWithValue() {
        ConfigurationProperty property = new ConfigurationProperty(new ConfigurationKey("username"), new ConfigurationValue("some_name"));

        SecretConfig secretConfig = new SecretConfig("id", "plugin_id", property);

        assertThat(secretConfig.size(), is(1));
        assertThat(secretConfig, contains(new ConfigurationProperty(new ConfigurationKey("username"), new ConfigurationValue("some_name"))));
    }

    @Test
    public void addConfigurations_shouldAddConfigurationsWithEncryptedValue() {
        ConfigurationProperty property = new ConfigurationProperty(new ConfigurationKey("username"), new EncryptedConfigurationValue("some_name"));

        SecretConfig secretConfig = new SecretConfig("id", "plugin_id", property);

        assertThat(secretConfig.size(), is(1));
        assertThat(secretConfig, contains(new ConfigurationProperty(new ConfigurationKey("username"), new EncryptedConfigurationValue("some_name"))));
    }

    @Test
    public void addConfiguration_shouldEncryptASecureVariable() {
        PluggableInstanceSettings securityConfigSettings = new PluggableInstanceSettings(asList(new PluginConfiguration("password", new Metadata(true, true))));
        SecretsPluginInfo pluginInfo = new SecretsPluginInfo(pluginDescriptor("plugin_id"), securityConfigSettings, null);

        store.setPluginInfo(pluginInfo);
        SecretConfig secretConfig = new SecretConfig("id", "plugin_id");
        secretConfig.addConfigurations(asList(new ConfigurationProperty(new ConfigurationKey("password"), new ConfigurationValue("pass"))));

        assertThat(secretConfig.size(), is(1));
        assertTrue(secretConfig.first().isSecure());
    }

    @Test
    public void addConfiguration_shouldIgnoreEncryptionInAbsenceOfCorrespondingConfigurationInStore() {
        SecretsPluginInfo pluginInfo = new SecretsPluginInfo(pluginDescriptor("plugin_id"), new PluggableInstanceSettings(new ArrayList<>()), null);

        store.setPluginInfo(pluginInfo);
        SecretConfig secretConfig = new SecretConfig("id", "plugin_id",
                new ConfigurationProperty(new ConfigurationKey("password"), new ConfigurationValue("pass")));

        assertThat(secretConfig.size(), is(1));
        assertFalse(secretConfig.first().isSecure());
        assertThat(secretConfig, contains(new ConfigurationProperty(new ConfigurationKey("password"), new ConfigurationValue("pass"))));
    }

    @Test
    public void postConstruct_shouldEncryptSecureConfigurations() {
        PluggableInstanceSettings secretsConfigSettings = new PluggableInstanceSettings(asList(new PluginConfiguration("password", new Metadata(true, true))));
        SecretsPluginInfo pluginInfo = new SecretsPluginInfo(pluginDescriptor("plugin_id"), secretsConfigSettings, null);

        store.setPluginInfo(pluginInfo);
        SecretConfig secretConfig = new SecretConfig("id", "plugin_id",
                new ConfigurationProperty(new ConfigurationKey("password"), new ConfigurationValue("pass")));

        secretConfig.encryptSecureConfigurations();

        assertThat(secretConfig.size(), is(1));
        assertTrue(secretConfig.first().isSecure());
    }

    @Test
    public void postConstruct_shouldIgnoreEncryptionIfPluginInfoIsNotDefined() {
        SecretConfig secretConfig = new SecretConfig("id", "plugin_id", new ConfigurationProperty(new ConfigurationKey("password"), new ConfigurationValue("pass")));

        secretConfig.encryptSecureConfigurations();

        assertThat(secretConfig.size(), is(1));
        assertFalse(secretConfig.first().isSecure());
    }

    private PluginDescriptor pluginDescriptor(String pluginId) {
        return new PluginDescriptor() {
            @Override
            public String id() {
                return pluginId;
            }

            @Override
            public String version() {
                return null;
            }

            @Override
            public About about() {
                return null;
            }
        };
    }

    @Override
    protected PluginProfile pluginProfile(String id, String pluginId, ConfigurationProperty... configurationProperties) {
        return new SecretConfig(id, pluginId, configurationProperties);
    }

    @Override
    protected String getObjectDescription() {
        return "Secret configuration";
    }
}
