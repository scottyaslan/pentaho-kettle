package org.pentaho.di.trans.steps.univariatestats.stats.processors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueProcessor;
import org.pentaho.di.trans.steps.univariatestats.stats.UnivariateValueProcessorPlugin;

@UnivariateValueProcessorPlugin( id = CacheingValueProcessor.ID, name = CacheingValueProcessor.NAME )
public class CacheingValueProcessor extends AbstractValueProducer implements UnivariateStatsValueProcessor {
  public static final String ID = "CACHE_AND_SORT";
  public static final String NAME = "Cache and sort";
  private final List<Double> cache;
  private boolean sorted = false;

  public CacheingValueProcessor( ) {
    super( NAME, ValueMetaInterface.TYPE_NONE );
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
