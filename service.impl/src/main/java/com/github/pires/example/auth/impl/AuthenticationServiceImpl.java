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
package com.github.pires.example.auth.impl;

import com.github.pires.example.auth.AuthenticationService;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.UnauthenticatedException;

/**
 * Implementation of {@link AuthenticationService}.
 */
public class AuthenticationServiceImpl implements AuthenticationService {

  private Map<String, String> tokens = new HashMap<>();

  public String login(final UsernamePasswordToken credentials)
      throws UnauthenticatedException {
    // TODO authenticate somewhere
    final String token = UUID.randomUUID().toString();
    tokens.put(token, credentials.getUsername());
    return token;
  }

  public boolean isAuthenticated(final String token) {
    return tokens.containsKey(token);
  }

  public String getUsername(final String token) {
    return tokens.get(token);
  }

  public Map<String, String> getTokens() {
    return tokens;
  }

}
