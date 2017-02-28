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

package com.thoughtworks.go.server.security;

import com.thoughtworks.go.config.SecurityAuthConfig;
import com.thoughtworks.go.plugin.access.authorization.AuthorizationExtension;
import com.thoughtworks.go.server.security.tokens.PreAuthenticatedAuthenticationToken;
import com.thoughtworks.go.server.service.GoConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.AuthenticationManager;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.ui.preauth.AbstractPreAuthenticatedProcessingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PreAuthenticatedRequestsProcessingFilter extends AbstractPreAuthenticatedProcessingFilter {
    private static final Pattern GRANT_ACCESS_REQUEST_PATTERN = Pattern.compile("^/go/plugin/([^\\s]+)/access$");
    private final AuthorizationExtension authorizationExtension;
    private final GoConfigService configService;
    private AuthenticationManager authenticationManager = null;

    @Autowired
    public PreAuthenticatedRequestsProcessingFilter(AuthorizationExtension authorizationExtension, GoConfigService configService) {
        this.authorizationExtension = authorizationExtension;
        this.configService = configService;
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        return null;
    }

    @Override
    protected Map<String, String> getPreAuthenticatedCredentials(HttpServletRequest request) {
        String pluginId = pluginId(request);
        List<SecurityAuthConfig> authConfigs = configService.security().securityAuthConfigs().findByPluginId(pluginId);

        return authorizationExtension.grantAccess(pluginId, getParameterMap(request), authConfigs);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void doFilterHttp(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (requiresAuthentication(request)) {
            doAuthenticate(request, response);
        }

        filterChain.doFilter(request, response);
    }

    private void doAuthenticate(HttpServletRequest request, HttpServletResponse response) {
        try {
            PreAuthenticatedAuthenticationToken authRequest =
                    new PreAuthenticatedAuthenticationToken(null, getPreAuthenticatedCredentials(request), pluginId(request));
            Authentication authResult = authenticationManager.authenticate(authRequest);
            successfulAuthentication(request, response, authResult);
        } catch (AuthenticationException failed) {
            unsuccessfulAuthentication(request, response, failed);
        }
    }

    @Override
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    private boolean requiresAuthentication(HttpServletRequest request) {
        return SecurityContextHolder.getContext().getAuthentication() == null &&
                GRANT_ACCESS_REQUEST_PATTERN.matcher(request.getRequestURI()).matches();
    }

    private String pluginId(HttpServletRequest request) {
        Matcher matcher = GRANT_ACCESS_REQUEST_PATTERN.matcher(request.getRequestURI());
        matcher.matches();

        return matcher.group(1);
    }

    //    TODO: see if this can be refactored
    private Map<String, String> getParameterMap(HttpServletRequest request) {
        Map<String, String[]> springParameterMap = request.getParameterMap();
        Map<String, String> pluginParameterMap = new HashMap<>();
        for (String parameterName : springParameterMap.keySet()) {
            String[] values = springParameterMap.get(parameterName);
            if (values != null && values.length > 0) {
                pluginParameterMap.put(parameterName, values[0]);
            } else {
                pluginParameterMap.put(parameterName, null);
            }
        }
        return pluginParameterMap;
    }
}
