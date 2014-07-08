package org.pentaho.di.trans.steps.univariatestats.calculators;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueProducer;
import org.pentaho.di.trans.steps.univariatestats.processors.CountValueProcessor;
import org.pentaho.di.trans.steps.univariatestats.processors.SumValueProcessor;

public class MeanValueCalculator extends AbstractValueCalculator {
  private double count;
  private double sum;

  @SuppressWarnings( "unchecked" )
  public MeanValueCalculator( String origin ) {
    super( "mean", origin, ValueMetaInterface.TYPE_NUMBER, new HashSet<Class<? extends UnivariateStatsValueProducer>>(
        Arrays.asList( CountValueProcessor.class, SumValueProcessor.class ) ) );
  }

  @Override
  public Object getValue() {
    if ( count == 0 ) {
      return Double.NaN;
    }
    return sum / count;
  }

  @Override
  protected void process( Map<Class<? extends UnivariateStatsValueProducer>, UnivariateStatsValueProducer> producerMap )
    throws KettleValueException, KettlePluginException {
    UnivariateStatsValueProducer countValueProcessor = producerMap.get( CountValueProcessor.class );
    UnivariateStatsValueProducer sumValueProcessor = producerMap.get( SumValueProcessor.class );
    count = countValueProcessor.getOutputValueMeta().getNumber( countValueProcessor.getValue() ).doubleValue();
    sum = sumValueProcessor.getOutputValueMeta().getNumber( sumValueProcessor.getValue() ).doubleValue();
  }
}
