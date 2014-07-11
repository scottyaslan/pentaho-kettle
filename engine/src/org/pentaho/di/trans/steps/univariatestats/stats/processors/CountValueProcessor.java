package org.pentaho.di.trans.steps.univariatestats.stats.processors;

import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueProcessor;
import org.pentaho.di.trans.steps.univariatestats.stats.UnivariateValueProcessorPlugin;

@UnivariateValueProcessorPlugin( id = CountValueProcessor.ID, name = CountValueProcessor.NAME )
public class CountValueProcessor extends AbstractValueProducer implements UnivariateStatsValueProcessor {
  public static final String ID = "COUNT";
  public static final String NAME = "N";
  private long count = 0;

  public CountValueProcessor( ) {
    super( NAME, ValueMetaInterface.TYPE_INTEGER );
  }

  @Override
  public void process( ValueMetaInterface inputMeta, Object input ) {
    if ( input != null ) {
      count++;
    }
  }

  @Override
  public Object getValue() {
    return count;
  }
}
