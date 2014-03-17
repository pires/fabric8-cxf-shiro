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
package com.github.pires.example.auth;

import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.UnauthenticatedException;

/**
 * Authentication service declaration.
 */
public interface AuthenticationService {

  /**
   * Tries to authenticate an user based on parameterized credentials.
   * 
   * @param credentials
   *          the authentication credentials
   * @return a generated token to be used for identifying the current session.
   * @throws UnauthenticatedException
   *           if the credentials didn't match any active user.
   */
  public String login(UsernamePasswordToken credentials)
      throws UnauthenticatedException;

  public boolean isAuthenticated(String token);

  public String getUsername(String token);

  public int countTokens();

}
