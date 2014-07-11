package org.pentaho.di.trans.steps.univariatestats.stats.calculators;

import java.util.Map;

import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueCalculator;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueProcessor;
import org.pentaho.di.trans.steps.univariatestats.stats.UnivariateValueCalculatorPlugin;
import org.pentaho.di.trans.steps.univariatestats.stats.processors.AbstractValueProducer;
import org.pentaho.di.trans.steps.univariatestats.stats.processors.CountValueProcessor;
import org.pentaho.di.trans.steps.univariatestats.stats.processors.SumValueProcessor;

@UnivariateValueCalculatorPlugin( id = MeanValueCalculator.ID, name = MeanValueCalculator.NAME, requiredProcessors = {
    CountValueProcessor.ID, SumValueProcessor.ID } )
public class MeanValueCalculator extends AbstractValueProducer implements UnivariateStatsValueCalculator {
  public static final String ID = "MEAN_VALUE_CALCULATOR";
  public static final String NAME = "mean";

  private double count;
  private double sum;

  public MeanValueCalculator( ) {
    super( NAME, ValueMetaInterface.TYPE_NUMBER );
  }

  @Override
  public Object getValue() {
    if ( count == 0 ) {
      return Double.NaN;
    }
    return sum / count;
  }

  @Override
  public void process( Map<String, UnivariateStatsValueProcessor> producerMap ) throws KettleValueException,
    KettlePluginException {
    UnivariateStatsValueProcessor countValueProcessor = producerMap.get( CountValueProcessor.ID );
    UnivariateStatsValueProcessor sumValueProcessor = producerMap.get( SumValueProcessor.ID );
    count = countValueProcessor.getOutputValueMeta().getNumber( countValueProcessor.getValue() ).doubleValue();
    sum = sumValueProcessor.getOutputValueMeta().getNumber( sumValueProcessor.getValue() ).doubleValue();
  }
}
