package com.pmobrien.rest.services.impl;

import com.cleo.graph.pojo.GraphEntityFactory;
import com.cleo.graph.pojo.OmniUser;
import com.cleo.graph.pojo.api.Collection;
import com.pmobrien.rest.neo.NeoConnector;
import com.pmobrien.rest.services.IOmniUsersService;
import java.util.UUID;

public class OmniUsersService implements IOmniUsersService {
  
  @Override
  public Collection<OmniUser> getAll() {
    return Collection.<OmniUser>of(
        NeoConnector.getInstance().returningSessionOperation(session -> {
          return session.loadAll(OmniUser.class);
        })
    );
  }

  @Override
  public OmniUser get(String uuid) {
    return NeoConnector.getInstance().returningSessionOperation(session -> {
      return session.load(OmniUser.class, UUID.fromString(uuid));
    });
  }

  @Override
  public OmniUser post(OmniUser user) {
    OmniUser save = GraphEntityFactory.create(OmniUser.class)
        .setUserId(user.getUserId())
        .setVlId(user.getVlId());
    
    NeoConnector.getInstance().sessionOperation(session ->
        session.save(save)
    );
    
    return get(save.getUuid().toString());
  }

  @Override
  public void delete(String uuid) {
    NeoConnector.getInstance().sessionOperation(session ->
        session.delete(session.load(OmniUser.class, UUID.fromString(uuid)))
    );
  }
}
