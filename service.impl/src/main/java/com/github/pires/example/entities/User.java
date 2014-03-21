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
package com.github.pires.example.entities;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * This class represents an user account.
 */
@Entity
@Table(name = "USERS")
public class User {

  @Id
  @Column(length = 100)
  private String email;

  @Version
  private Long version;

  @Column(length = 100, nullable = false)
  private char[] password;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "USER_ROLES", joinColumns = { @JoinColumn(name = "email") })
  private Set<Role> roles;

  public User() {
    this.roles = new HashSet<Role>();
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Long getVersion() {
    return version;
  }

  public void setVersion(Long version) {
    this.version = version;
  }

  public char[] getPassword() {
    return password;
  }

  public void setPassword(char[] password) {
    this.password = password;
  }

  public Set<Role> getRoles() {
    return roles;
  }

  public void setRoles(Set<Role> roles) {
    this.roles = roles;
  }

  public void addRole(Role role) {
    this.roles.add(role);
  }

  public boolean removeRole(Role role) {
    return this.roles.remove(role);
  }

  /**
   * Compares two {@link User} instances based on: <ul> <li>Email</li> </ul>
   * 
   * @param obj
   *          - the object to compare this object to.
   * @return true if both objects are equal, false otherwise.
   */
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof User) {
      User that = (User) obj;
      return this.email.equals(that.email);
    }
    return false;
  }

}
