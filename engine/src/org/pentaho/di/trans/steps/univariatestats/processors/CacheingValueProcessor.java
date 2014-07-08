package org.pentaho.di.trans.steps.univariatestats.processors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueProcessor;

public class CacheingValueProcessor extends AbstractValueProcessor implements UnivariateStatsValueProcessor {
  private final List<Double> cache;
  private boolean sorted = false;

  public CacheingValueProcessor( String origin ) {
    super( "cache", origin, ValueMetaInterface.TYPE_NONE );
    cache = new ArrayList<Double>();
  }

  @Override
  public void process( ValueMetaInterface inputMeta, Object input ) throws KettleException {
    if ( input != null ) {
      cache.add( inputMeta.getNumber( input ).doubleValue() );
      sorted = false;
    }
  }

  @Override
  public Object getValue() {
    if ( !sorted ) {
      Collections.sort( cache, new Comparator<Double>() {

        @Override
        public int compare( Double o1, Double o2 ) {
          return o1.compareTo( o2 );
        }
      } );
      sorted = true;
    }
    return cache;
  }
}
