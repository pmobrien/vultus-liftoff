package com.pmobrien.vultus.liftoff.neo;

import com.google.common.base.Strings;
import com.google.common.base.Suppliers;
import java.util.Optional;
import java.util.function.Supplier;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

public class NeoConnector {
  
  public static final String NEO_STORE = "neo-store";
  public static final String NEO_CREDENTIALS = "neo-credentials";
  
  private static final String POJO_PACKAGE = "com.pmobrien.vultus.liftoff.neo.pojo";
  
  private static final NeoConnector INSTANCE = new NeoConnector();
  private static final Supplier<SessionFactory> SESSION_FACTORY = Suppliers.memoize(() -> initializeSessionFactory());
  
  private NeoConnector() {}
  
  public static NeoConnector getInstance() {
    return INSTANCE;
  }
  
  protected Session newSession() {
    return SESSION_FACTORY.get().openSession();
  }
  
  private static SessionFactory initializeSessionFactory() {
    Configuration configuration = isBolt()
        ? new Configuration.Builder()
            .credentials(username(), password())
            .uri(uri())
            .build()
        : new Configuration.Builder()
            .uri(uri())
            .build();

    return new SessionFactory(configuration, POJO_PACKAGE);
  }

  private static String uri() {
    if(Strings.isNullOrEmpty(System.getProperty(NEO_STORE))) {
      throw new RuntimeException(String.format("%s property must be set.", NEO_STORE));
    }
    
    return isBolt()
        ? System.getProperty(NEO_STORE)
        : String.format("file://%s", System.getProperty(NEO_STORE));
  }
  
  private static boolean isBolt() {
    return Optional.ofNullable(System.getProperty(NEO_STORE)).orElse("").startsWith("bolt");
  }
  
  private static String username() {
    return System.getProperty(NEO_CREDENTIALS).split(":")[0];
  }
  
  private static String password() {
    return System.getProperty(NEO_CREDENTIALS).split(":")[1];
  }
}
