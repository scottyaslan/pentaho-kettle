package org.pentaho.di.trans.steps.univariatestats.stats.processors;

import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueProcessor;
import org.pentaho.di.trans.steps.univariatestats.stats.UnivariateValueProcessorPlugin;

@UnivariateValueProcessorPlugin( id = MinValueProcessor.ID, name = MinValueProcessor.NAME )
public class MinValueProcessor extends AbstractValueProducer implements UnivariateStatsValueProcessor {
  public static final String ID = "MIN_VALUE";
  public static final String NAME = "MinValueProcessor.Name";

  private double min = Double.MAX_VALUE;

  public MinValueProcessor() {
    super( NAME, ValueMetaInterface.TYPE_NUMBER );
  }

  @Override
  public void process( ValueMetaInterface inputMeta, Object input ) throws KettleValueException {
    if ( input != null ) {
      try {
        min = Math.min( min, inputMeta.getNumber( input ).doubleValue() );
      } catch ( KettleValueException e ) {
        // Ignore unparseable numbers
      }
    }
  }

  @Override
  public Object getValue() {
    return min;
  }
}