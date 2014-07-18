package org.pentaho.di.trans.steps.univariatestats.calculators;

import java.util.HashMap;
import java.util.Map;

public class MapBuilder<K, V> {
  private final Map<K, V> map;

  public MapBuilder() {
    map = new HashMap<K, V>();
  }

  public MapBuilder<K, V> put( K key, V value ) {
    map.put( key, value );
    return this;
  }

  public Map<K, V> build() {
    return new HashMap<K, V>( map );
  }
}
