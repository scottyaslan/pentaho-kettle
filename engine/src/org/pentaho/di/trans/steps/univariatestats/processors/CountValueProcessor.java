package org.pentaho.di.trans.steps.univariatestats.processors;

import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueProcessor;

public class CountValueProcessor extends AbstractValueProcessor implements UnivariateStatsValueProcessor {
  private long count = 0;

  public CountValueProcessor( String origin ) {
    super( "N", origin, ValueMetaInterface.TYPE_INTEGER );
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
