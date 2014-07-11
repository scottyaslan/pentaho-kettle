package org.pentaho.di.trans.steps.univariatestats;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class UnivariateStatsValueConfig {
  private final String id;

  private final Map<String, Object> parameters;

  public UnivariateStatsValueConfig( String id ) {
    this.id = id;
    this.parameters = new HashMap<String, Object>();
  }

  public String getId() {
    return id;
  }

  public Map<String, Object> getParameters() {
    return Collections.unmodifiableMap( parameters );
  }

  public Object setParameter( String key, Object value ) {
    return parameters.put( key, value );
  }

  @Override
  public String toString() {
    return "UnivariateStatsValueConfig [id=" + id + ", parameters=" + parameters + "]";
  }
}
