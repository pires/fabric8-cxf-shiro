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
import com.github.pires.example.daos.RoleDao;
import com.github.pires.example.daos.UserDao;
import com.github.pires.example.entities.Role;
import com.github.pires.example.entities.User;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashingPasswordService;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;

/**
 * Implementation of {@link AuthenticationService}.
 */
public class AuthenticationServiceImpl implements AuthenticationService {

  private RoleDao roleDao;
  private UserDao userDao;

  private SecurityManager securityManager;
  private HashingPasswordService passwordService;

  public String login(final UsernamePasswordToken credentials)
      throws UnauthenticatedException {
    Subject newSubject = new Subject.Builder(securityManager).buildSubject();
    newSubject.login(credentials);
    return newSubject.getSession().getId().toString();
  }

  public boolean isAuthenticated(final String token) {
    Subject requestSubject = new Subject.Builder(securityManager).sessionId(
        token).buildSubject();
    return requestSubject.isAuthenticated();
  }

  public String getUsername(final String token) {
    Subject requestSubject = new Subject.Builder(securityManager).sessionId(
        token).buildSubject();
    return requestSubject.getPrincipal().toString();
  }

  public void initializeTestScenario() {
    // create roles
    Role role1 = new Role();
    role1.setRoleName("ADMIN");
    role1.setDescription("Administrative user role");
    roleDao.persist(role1);

    Role role2 = new Role();
    role2.setRoleName("REGULAR");
    role2.setDescription("Regular user role");
    roleDao.persist(role2);

    // create administrative user
    User adminUser = new User();
    adminUser.setEmail("admin@example.com");
    // clear password
    final char[] pwd1 = { '1', '2', '3' };
    // hash password
    final String adminUserPassword = passwordService.encryptPassword(pwd1);
    adminUser.setPassword(adminUserPassword.toCharArray());
    userDao.persist(adminUser);

    // create regular user
    User regularUser = new User();
    regularUser.setEmail("regular@example.com");
    // clear password
    final char[] pwd2 = { '4', '5', '6' };
    // hash password
    final String regularUserPassword = passwordService.encryptPassword(pwd2);
    regularUser.setPassword(regularUserPassword.toCharArray());
    userDao.persist(regularUser);
  }

  public RoleDao getRoleDao() {
    return roleDao;
  }

  public void setRoleDao(RoleDao roleDao) {
    this.roleDao = roleDao;
  }

  public UserDao getUserDao() {
    return userDao;
  }

  public void setUserDao(UserDao userDao) {
    this.userDao = userDao;
  }

  public SecurityManager getSecurityManager() {
    return securityManager;
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public HashingPasswordService getPasswordService() {
    return passwordService;
  }

  public void setPasswordService(HashingPasswordService passwordService) {
    this.passwordService = passwordService;
  }

}
