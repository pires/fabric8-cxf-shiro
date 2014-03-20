package com.github.pires.example.shiro;

import com.hazelcast.config.ClasspathXmlConfig;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.IMap;
import java.io.Serializable;
import java.util.Collection;
import java.util.UUID;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.SessionDAO;

/**
 * Data Access Object for Shiro {@link Session} persistence in Hazelcast.
 */
public class HazelcastSessionDao implements SessionDAO {

  private static final String HC_MAP = "be-sessions";

  private final IMap<Serializable, Session> map;

  public HazelcastSessionDao() {
    Config cfg = new ClasspathXmlConfig("hazelcast.xml");
    map = Hazelcast.newHazelcastInstance(cfg).getMap(HC_MAP);
  }

  public Serializable create(Session session) {
    final UUID sessionId = UUID.randomUUID();
    map.put(sessionId, session);
    return sessionId;
  }

  public Session readSession(Serializable sessionId)
      throws UnknownSessionException {
    return map.get(sessionId);
  }

  public void update(Session session) throws UnknownSessionException {
    map.replace(session.getId(), session);
  }

  public void delete(Session session) {
    map.remove(session.getId());
  }

  public Collection<Session> getActiveSessions() {
    return map.values();
  }

}
