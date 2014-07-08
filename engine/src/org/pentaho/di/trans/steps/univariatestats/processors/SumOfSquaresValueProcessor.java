package org.pentaho.di.trans.steps.univariatestats.processors;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueProcessor;

public class SumOfSquaresValueProcessor extends AbstractValueProcessor implements UnivariateStatsValueProcessor {
  private double sumSq = 0;

  public SumOfSquaresValueProcessor( String origin ) {
    super( "sumOfSquares", origin, ValueMetaInterface.TYPE_NUMBER );
  }

  @Override
  public void process( ValueMetaInterface inputMeta, Object input ) throws KettleException {
    if ( input != null ) {
      double value = inputMeta.getNumber( input ).doubleValue();
      sumSq += ( value * value );
    }
  }

  @Override
  public Object getValue() {
    return sumSq;
  }

}
