package org.pentaho.di.trans.steps.univariatestats.processors;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueProcessor;

public class MaxValueProcessor extends AbstractValueProcessor implements UnivariateStatsValueProcessor {
  private double max = Double.MIN_VALUE;

  public MaxValueProcessor( String origin ) {
    super( "max", origin, ValueMetaInterface.TYPE_NUMBER );
  }

  @Override
  public void process( ValueMetaInterface inputMeta, Object input ) throws KettleException {
    if ( input != null ) {
      max = Math.max( max, inputMeta.getNumber( input ).doubleValue() );
    }
  }

  @Override
  public Object getValue() {
    return max;
  }

}
