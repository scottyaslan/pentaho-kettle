package org.pentaho.di.trans.steps.univariatestats;

public class UnivariateStatsValueParameter {
  private final String key;

  private final Object value;

  public UnivariateStatsValueParameter( String key, Object value ) {
    this.key = key;
    this.value = value;
  }

  public String getKey() {
    return key;
  }

  public Object getValue() {
    return value;
  }

  @Override
  public String toString() {
    return "UnivariateStatsValueParameter [key=" + key + ", value=" + value + "]";
  }
}
