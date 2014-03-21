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
package com.github.pires.example.shiro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.ConfigurationException;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import static org.apache.shiro.realm.jdbc.JdbcRealm.SaltStyle.COLUMN;
import static org.apache.shiro.realm.jdbc.JdbcRealm.SaltStyle.CRYPT;
import static org.apache.shiro.realm.jdbc.JdbcRealm.SaltStyle.EXTERNAL;
import static org.apache.shiro.realm.jdbc.JdbcRealm.SaltStyle.NO_SALT;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.JdbcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This realm allows a user to authenticate based on its <i>email address</i>.
 */
public class SMRealm extends JdbcRealm {

  private static final Logger logger = LoggerFactory.getLogger(SMRealm.class);

  @Override
  protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
      throws AuthenticationException {
    UsernamePasswordToken upToken = (UsernamePasswordToken) token;
    final String email = upToken.getUsername();

    // null email is invalid
    if (email == null) {
      throw new AccountException("Null email is not allowed by this realm.");
    }

    Connection conn = null;
    SimpleAuthenticationInfo info = null;
    try {
      conn = dataSource.getConnection();
      String password = null;
      String salt = null;
      switch (saltStyle) {
      case NO_SALT:
        password = getPasswordForUser(conn, email)[0];
        break;
      case CRYPT:
        // TODO: separate password and hash from getPasswordForUser[0]
        throw new ConfigurationException("Not implemented yet");
        // break;
      case COLUMN:
        String[] queryResults = getPasswordForUser(conn, email);
        password = queryResults[0];
        salt = queryResults[1];
        break;
      case EXTERNAL:
        password = getPasswordForUser(conn, email)[0];
        salt = getSaltForUser(email);
      }

      if (password == null) {
        throw new UnknownAccountException(
            "No account found for user identified by [" + email + "]");
      }
      info = new SimpleAuthenticationInfo(email, password.toCharArray(),
          getName());
      if (salt != null) {
        info.setCredentialsSalt(ByteSource.Util.bytes(salt));
      }
    } catch (SQLException e) {
      final String message = "There was a SQL error while authenticating user identified by ["
          + email + "]";
      logger.error(message, e);
      // rethrow any SQL errors as an authentication exception
      throw new AuthenticationException(message, e);
    } finally {
      JdbcUtils.closeConnection(conn);
    }

    return info;
  }

  private String[] getPasswordForUser(Connection conn, String email)
      throws SQLException {
    String[] result;
    boolean returningSeparatedSalt = false;
    switch (saltStyle) {
    case NO_SALT:
    case CRYPT:
    case EXTERNAL:
      result = new String[1];
      break;
    default:
      result = new String[2];
      returningSeparatedSalt = true;
    }

    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = conn.prepareStatement(authenticationQuery);
      ps.setString(1, email); // set email address

      // Loop over results - although we are only expecting one result,
      // since usernames should be unique
      rs = ps.executeQuery();
      boolean foundResult = false;
      while (rs.next()) {
        // Check to ensure only one row is processed
        if (foundResult) {
          throw new AuthenticationException(
              "More than one user row found for user [" + email
                  + "]. Emails must be unique.");
        }

        result[0] = rs.getString(1);
        if (returningSeparatedSalt) {
          result[1] = rs.getString(2);
        }
        foundResult = true;
      }
    } finally {
      JdbcUtils.closeResultSet(rs);
      JdbcUtils.closeStatement(ps);
    }

    return result;
  }

}
