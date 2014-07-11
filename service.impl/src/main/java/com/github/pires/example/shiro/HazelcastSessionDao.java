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

import com.hazelcast.config.Config;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MulticastConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.SSLConfig;
import com.hazelcast.config.TcpIpConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import java.io.Serializable;
import java.util.Collection;
import java.util.UUID;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data Access Object for Shiro {@link Session} persistence in Hazelcast.
 */
public class HazelcastSessionDao extends AbstractSessionDAO {

  private static final Logger log = LoggerFactory
      .getLogger(HazelcastSessionDao.class);

  private static final String HC_MAP = "be-sessions";
  private static final String HC_GROUP_NAME = "hc-example";
  private static final String HC_GROUP_PASSWORD = "123qwe";
  private static final int HC_PORT = 5701;
  private static final String HC_MULTICAST_GROUP = "224.2.2.3";
  private static final int HC_MULTICAST_PORT = 54327;

  private final String hcInstanceName;
  private final IMap<Serializable, Session> map;

  public HazelcastSessionDao() {
    log.info("Initializating Hazelcast Shiro session persistence..");

    // configure Hazelcast instance
    hcInstanceName = UUID.randomUUID().toString();
    Config cfg = new Config();
    cfg.setInstanceName(hcInstanceName);
    // group configuration
    cfg.setGroupConfig(new GroupConfig(HC_GROUP_NAME, HC_GROUP_PASSWORD));

    // network configuration initialization
    NetworkConfig netCfg = new NetworkConfig();
    netCfg.setPortAutoIncrement(true);
    netCfg.setPort(HC_PORT);
    // multicast
    MulticastConfig mcCfg = new MulticastConfig();
    mcCfg.setEnabled(true);
    mcCfg.setMulticastGroup(HC_MULTICAST_GROUP);
    mcCfg.setMulticastPort(HC_MULTICAST_PORT);
    // tcp
    TcpIpConfig tcpCfg = new TcpIpConfig();
    tcpCfg.setEnabled(false);
    // network join configuration
    JoinConfig joinCfg = new JoinConfig();
    joinCfg.setMulticastConfig(mcCfg);
    joinCfg.setTcpIpConfig(tcpCfg);
    netCfg.setJoin(joinCfg);
    // ssl
    netCfg.setSSLConfig(new SSLConfig().setEnabled(false));

    // get map
    map = Hazelcast.newHazelcastInstance(cfg).getMap(HC_MAP);

    log.info("Hazelcast Shiro session persistence initialized.");
  }

  @Override
  protected Serializable doCreate(Session session) {
    final Serializable sessionId = generateSessionId(session);
    assignSessionId(session, sessionId);
    map.set(session.getId(), session);
    return sessionId;
  }

  @Override
  protected Session doReadSession(Serializable sessionId) {
    return map.get(sessionId);
  }

  @Override
  public void update(Session session) throws UnknownSessionException {
    map.replace(session.getId(), session);
  }

  @Override
  public void delete(Session session) {
    map.remove(session.getId());
  }

  @Override
  public Collection<Session> getActiveSessions() {
    return map.values();
  }

  /**
   * Destroys currently allocated instance.
   */
  public void destroy() {
    log.info("Shutting down Hazelcast instance [{}]..", hcInstanceName);
    HazelcastInstance instance = Hazelcast
        .getHazelcastInstanceByName(hcInstanceName);
    if (instance != null) {
      instance.shutdown();
    }
  }

}
