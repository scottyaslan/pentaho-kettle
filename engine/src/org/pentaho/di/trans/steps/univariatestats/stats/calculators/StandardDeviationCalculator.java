package org.pentaho.di.trans.steps.univariatestats.stats.calculators;

import java.util.Map;

import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueCalculator;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueProcessor;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueProducer;
import org.pentaho.di.trans.steps.univariatestats.stats.UnivariateValueCalculatorPlugin;
import org.pentaho.di.trans.steps.univariatestats.stats.processors.AbstractValueProducer;
import org.pentaho.di.trans.steps.univariatestats.stats.processors.CountValueProcessor;
import org.pentaho.di.trans.steps.univariatestats.stats.processors.SumOfSquaresValueProcessor;
import org.pentaho.di.trans.steps.univariatestats.stats.processors.SumValueProcessor;

@UnivariateValueCalculatorPlugin( id = StandardDeviationCalculator.ID, name = StandardDeviationCalculator.NAME,
    requiredProcessors = { CountValueProcessor.ID, SumValueProcessor.ID, SumOfSquaresValueProcessor.ID } )
public class StandardDeviationCalculator extends AbstractValueProducer implements UnivariateStatsValueCalculator {
  public static final String ID = "STANDARD_DEVIATION_VALUE_CALCULATOR";
  public static final String NAME = "StandardDeviationCalculator.Name";

  private double stdDev = Double.NaN;

  public StandardDeviationCalculator( ) {
    super( NAME, ValueMetaInterface.TYPE_NUMBER );
  }

  @Override
  public Object getValue() {
    return stdDev;
  }

  @Override
  public void process( Map<String, UnivariateStatsValueProcessor> producerMap ) throws KettleValueException,
    KettlePluginException {
    UnivariateStatsValueProducer countValueProducer = producerMap.get( CountValueProcessor.ID );
    double count = countValueProducer.getOutputValueMeta().getNumber( countValueProducer.getValue() ).doubleValue();
    if ( count > 0 ) {
      stdDev = Double.POSITIVE_INFINITY;
      if ( count > 1 ) {
        UnivariateStatsValueProducer sumSquarsValueProducer = producerMap.get( SumOfSquaresValueProcessor.ID );
        UnivariateStatsValueProducer sumValueProducer = producerMap.get( SumValueProcessor.ID );
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
