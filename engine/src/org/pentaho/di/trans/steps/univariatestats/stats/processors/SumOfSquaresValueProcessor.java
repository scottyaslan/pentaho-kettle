package org.pentaho.di.trans.steps.univariatestats.stats.processors;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueProcessor;
import org.pentaho.di.trans.steps.univariatestats.stats.UnivariateValueProcessorPlugin;

@UnivariateValueProcessorPlugin( id = SumOfSquaresValueProcessor.ID, name = SumOfSquaresValueProcessor.NAME )
public class SumOfSquaresValueProcessor extends AbstractValueProducer implements UnivariateStatsValueProcessor {
  public static final String ID = "SUM_OF_SQUARES";
  public static final String NAME = "SumOfSquaresValueProcessor.Name";

  private double sumSq = 0;

  public SumOfSquaresValueProcessor( ) {
    super( NAME, ValueMetaInterface.TYPE_NUMBER );
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
