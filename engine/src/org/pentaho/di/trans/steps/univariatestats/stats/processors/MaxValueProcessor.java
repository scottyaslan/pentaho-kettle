package org.pentaho.di.trans.steps.univariatestats.stats.processors;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueProcessor;
import org.pentaho.di.trans.steps.univariatestats.stats.UnivariateValueProcessorPlugin;

@UnivariateValueProcessorPlugin( id = MaxValueProcessor.ID, name = MaxValueProcessor.NAME )
public class MaxValueProcessor extends AbstractValueProducer implements UnivariateStatsValueProcessor {
  public static final String ID = "MAX_VALUE";
  public static final String NAME = "MaxValueProcessor.Name";

  private double max = Double.MIN_VALUE;

  public MaxValueProcessor( ) {
    super( NAME, ValueMetaInterface.TYPE_NUMBER );
  }

  @Override
  public void process( ValueMetaInterface inputMeta, Object input ) throws KettleException {
    if ( input != null ) {
      try {
        max = Math.max( max, inputMeta.getNumber( input ).doubleValue() );
      } catch ( KettleValueException e ) {
        // Ignore unparseable numbers
      }
    }
  }

  @Override
  public Object getValue() {
    return max;
  }

}
