package org.pentaho.di.trans.steps.univariatestats.processors;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueProcessor;

public class SumValueProcessor extends AbstractValueProcessor implements UnivariateStatsValueProcessor {
  private double sum = 0;

  public SumValueProcessor( String origin ) {
    super( "sum", origin, ValueMetaInterface.TYPE_NUMBER );
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
