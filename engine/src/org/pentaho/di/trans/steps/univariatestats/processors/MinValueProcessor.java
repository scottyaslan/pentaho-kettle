package org.pentaho.di.trans.steps.univariatestats.processors;

import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueProcessor;

public class MinValueProcessor extends AbstractValueProcessor implements UnivariateStatsValueProcessor {
  private double min = Double.MAX_VALUE;

  public MinValueProcessor( String origin ) {
    super( "min", origin, ValueMetaInterface.TYPE_NUMBER );
  }

  @Override
  public void process( ValueMetaInterface inputMeta, Object input ) throws KettleValueException {
    if ( input != null ) {
      min = Math.min( min, inputMeta.getNumber( input ).doubleValue() );
    }
  }

  @Override
  public Object getValue() {
    return min;
  }
}
