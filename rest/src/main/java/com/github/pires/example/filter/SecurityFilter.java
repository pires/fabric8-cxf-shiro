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
import java.io.IOException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * Filter all incoming requests and handle requests that need authentication &
 * authorization verification.
 */
public class SecurityFilter implements ContainerRequestFilter,
    ContainerResponseFilter {

  private static final String tokenHeader = "be-token";

  private final AuthenticationService authService;

  public SecurityFilter(AuthenticationService authService) {
    this.authService = authService;
  }

  public void filter(ContainerRequestContext requestContext) throws IOException {
    final String token = requestContext.getHeaderString(tokenHeader);
    if (token == null || !authService.isAuthenticated(token)) {
      requestContext.abortWith(Response.status(Status.UNAUTHORIZED).build());
    }
  }

  public void filter(ContainerRequestContext requestContext,
      ContainerResponseContext responseContext) throws IOException {
    final String token = requestContext.getHeaderString(tokenHeader);
    if (token != null && !token.isEmpty()) {
      responseContext.getHeaders().add(tokenHeader, token);
    }
  }

}
