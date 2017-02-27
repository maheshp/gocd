/*
 * Copyright 2017 ThoughtWorks, Inc.
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

package com.thoughtworks.go.server.security.providers;

import com.thoughtworks.go.plugin.access.authorization.AuthorizationExtension;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.providers.preauth.PreAuthenticatedAuthenticationToken;

import static org.mockito.Mockito.mock;

public class WebPluginAuthenticationProviderTest {
    private PreAuthenticatedAuthenticationToken authenticationToken;
    private AuthorizationExtension authorizationExtension;

    @Before
    public void setUp() throws Exception {
        authenticationToken = new PreAuthenticatedAuthenticationToken("principal", "credentials");
        authorizationExtension = mock(AuthorizationExtension.class);
    }

    @Test
    public void shouldLoadUserDetailsOnAuthentication() throws Exception {


    }
}

