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
package com.github.pires.example.mapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.apache.shiro.ShiroException;

/**
 * When an {@link ShiroException} occurs, the client *SHALL NOT* receive a
 * <i>Code 500: Server Internal Error</i> response, but <i>Code 401:
 * Unauthorized</i> instead.
 */
@Provider
public class ShiroExceptionMapper implements ExceptionMapper<ShiroException> {

  @Override
  public Response toResponse(ShiroException exception) {
    return Response.status(Response.Status.UNAUTHORIZED).build();
  }

}
