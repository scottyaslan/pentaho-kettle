package org.pentaho.di.trans.steps.univariatestats.stats.processors;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueProcessor;
import org.pentaho.di.trans.steps.univariatestats.stats.UnivariateValueProcessorPlugin;

@UnivariateValueProcessorPlugin( id = SumValueProcessor.ID, name = SumValueProcessor.NAME )
public class SumValueProcessor extends AbstractValueProducer implements UnivariateStatsValueProcessor {
  public static final String ID = "SUM";
  public static final String NAME = "SumValueProcessor.Name";

  private double sum = 0;

  public SumValueProcessor( ) {
    super( NAME, ValueMetaInterface.TYPE_NUMBER );
  }

  @Override
  public Object getValue() {
    return sum;
  }

  @Override
  public void process( ValueMetaInterface inputMeta, Object input ) throws KettleException {
    if ( input != null ) {
      sum += inputMeta.getNumber( input ).doubleValue();
    }
  }
}
