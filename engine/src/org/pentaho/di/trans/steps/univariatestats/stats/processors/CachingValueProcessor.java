package org.pentaho.di.trans.steps.univariatestats.stats.processors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueProcessor;
import org.pentaho.di.trans.steps.univariatestats.stats.UnivariateValueProcessorPlugin;

@UnivariateValueProcessorPlugin( id = CachingValueProcessor.ID, name = CachingValueProcessor.NAME, hidden = true )
public class CachingValueProcessor extends AbstractValueProducer implements UnivariateStatsValueProcessor {
  public static final String ID = "CACHE_AND_SORT";
  public static final String NAME = "CachingValueProcessor.Name";
  private final List<Double> cache;
  private boolean sorted = false;

  public CachingValueProcessor() {
    super( NAME, ValueMetaInterface.TYPE_NONE );
    cache = new ArrayList<Double>();
  }

  @Override
  public void process( ValueMetaInterface inputMeta, Object input ) throws KettleException {
    if ( input != null ) {
      try {
        cache.add( inputMeta.getNumber( input ).doubleValue() );
      } catch ( KettleValueException e ) {
        // Ignore values that cannot be turned into doubles
      }
      sorted = false;
    }
  }

  @Override
  public Object getValue() {
    if ( !sorted ) {
      Collections.sort( cache );
      sorted = true;
    }
    return cache;
  }
}
