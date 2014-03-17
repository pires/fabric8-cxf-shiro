/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.github.pires.example.filter;

import com.github.pires.example.auth.AuthenticationService;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Configurable;
import javax.ws.rs.ext.Provider;
import org.apache.shiro.authz.annotation.RequiresAuthentication;

/**
 * Sometimes you want a filter or interceptor to only run for a specific
 * resource method. <p> You can do this by registering an implementation of
 * {@link DynamicFeature}.<br /> The DynamicFeature interface is executed at
 * deployment time for each resource method. You just use the
 * {@link FeatureContext} interface to register the filters and interceptors you
 * want for the specific resource method.
 */
@Provider
public class SecurityFeatureProvider implements DynamicFeature {

  private AuthenticationService authService;

  /**
   * TODO uncomment this when CXF 3.0.0 is released.
   */
  // public void configure(ResourceInfo resourceInfo, FeatureContext context) {
  // if (resourceInfo.getResourceMethod().isAnnotationPresent(
  // RequiresAuthentication.class)) {
  // context.register(new SecurityFilter(authService));
  // }
  // }
  /**
   * We need this implementation because of
   * https://issues.apache.org/jira/browse/CXF-5252 <p> TODO remove when CXF
   * 3.0.0 is released.
   */
  public void configure(ResourceInfo resourceInfo, Configurable context) {
    if (resourceInfo.getResourceMethod().isAnnotationPresent(
        RequiresAuthentication.class)) {
      context.register(new SecurityFilter(authService));
    }
  }

  public AuthenticationService getAuthService() {
    return authService;
  }

  public void setAuthService(AuthenticationService authService) {
    this.authService = authService;
  }

}
