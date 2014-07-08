package org.pentaho.di.trans.steps.univariatestats.calculators;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueProducer;
import org.pentaho.di.trans.steps.univariatestats.processors.CountValueProcessor;
import org.pentaho.di.trans.steps.univariatestats.processors.SumOfSquaresValueProcessor;
import org.pentaho.di.trans.steps.univariatestats.processors.SumValueProcessor;

public class StandardDeviationCalculator extends AbstractValueCalculator {
  private double stdDev = Double.NaN;

  @SuppressWarnings( "unchecked" )
  public StandardDeviationCalculator( String origin ) {
    super( "stdDev", origin, ValueMetaInterface.TYPE_NUMBER,
        new HashSet<Class<? extends UnivariateStatsValueProducer>>( Arrays.asList( CountValueProcessor.class,
            SumValueProcessor.class, SumOfSquaresValueProcessor.class ) ) );
  }

  @Override
  public Object getValue() {
    return stdDev;
  }

  @Override
  protected void process( Map<Class<? extends UnivariateStatsValueProducer>, UnivariateStatsValueProducer> producerMap )
    throws KettleValueException, KettlePluginException {
    UnivariateStatsValueProducer countValueProducer = producerMap.get( CountValueProcessor.class );
    double count = countValueProducer.getOutputValueMeta().getNumber( countValueProducer.getValue() ).doubleValue();
    if ( count > 0 ) {
      stdDev = Double.POSITIVE_INFINITY;
      if ( count > 1 ) {
        UnivariateStatsValueProducer sumSquarsValueProducer = producerMap.get( SumOfSquaresValueProcessor.class );
        UnivariateStatsValueProducer sumValueProducer = producerMap.get( SumValueProcessor.class );
        double sumSq =
            sumSquarsValueProducer.getOutputValueMeta().getNumber( sumSquarsValueProducer.getValue() ).doubleValue();
        double sum = sumValueProducer.getOutputValueMeta().getNumber( sumValueProducer.getValue() ).doubleValue();
        stdDev = sumSq - ( sum * sum ) / count;
        stdDev /= ( count - 1 );
        if ( stdDev < 0 ) {
          stdDev = 0;
        }
        stdDev = Math.sqrt( stdDev );
      }
    }
  }
}
