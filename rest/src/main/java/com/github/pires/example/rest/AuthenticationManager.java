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
package com.github.pires.example.rest;

import com.github.pires.example.auth.AuthenticationService;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/auth")
public class AuthenticationManager {

  private static final Logger log = LoggerFactory
      .getLogger(AuthenticationManager.class);

  private static final String tokenHeader = "be-token";

  private AuthenticationService authService;

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public Response login(final UsernamePasswordToken credentials) {
    assert credentials != null && credentials.getUsername().isEmpty()
        && credentials.getPassword().length > 0;
    log.info("Performing authentication for username [{}]",
        credentials.getUsername());
    try {
      final String token = authService.login(credentials);
      return Response.ok().header(tokenHeader, token).build();
    } catch (UnauthenticatedException e) {
      return Response.status(Response.Status.BAD_REQUEST).build();
    }
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @RequiresAuthentication
  public Response viewMyProfile(@HeaderParam("be-token") final String token) {
    log.info("Is authenticated?: {}", authService.isAuthenticated(token));
    return Response.ok(authService.getUsername(token)).build();
  }

  public AuthenticationService getAuthService() {
    return authService;
  }

  public void setAuthService(AuthenticationService authService) {
    this.authService = authService;
  }

}
