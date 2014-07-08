package org.pentaho.di.trans.steps.univariatestats.calculators;

import static org.junit.Assert.assertEquals;
import static org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueProducerMockUtil.mockProducer;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueProducer;
import org.pentaho.di.trans.steps.univariatestats.processors.CountValueProcessor;
import org.pentaho.di.trans.steps.univariatestats.processors.SumOfSquaresValueProcessor;
import org.pentaho.di.trans.steps.univariatestats.processors.SumValueProcessor;

public class StandardDeviationCalculatorTest {
  @Test
  public void testCalculateStdDev0Count() throws KettleValueException, KettlePluginException {
    StandardDeviationCalculator calc = new StandardDeviationCalculator( "test" );
    Map<Class<? extends UnivariateStatsValueProducer>, UnivariateStatsValueProducer> producerMap =
        new HashMap<Class<? extends UnivariateStatsValueProducer>, UnivariateStatsValueProducer>();
    producerMap.put( CountValueProcessor.class, mockProducer( 0.0 ) );
    calc.process( producerMap );
    assertEquals( Double.NaN, calc.getValue() );
  }

  @Test
  public void testCalculateStdDev1Count() throws KettlePluginException, KettleValueException {
    StandardDeviationCalculator calc = new StandardDeviationCalculator( "test" );
    Map<Class<? extends UnivariateStatsValueProducer>, UnivariateStatsValueProducer> producerMap =
        new HashMap<Class<? extends UnivariateStatsValueProducer>, UnivariateStatsValueProducer>();
    producerMap.put( CountValueProcessor.class, mockProducer( 1.0 ) );
    calc.process( producerMap );
    assertEquals( Double.POSITIVE_INFINITY, calc.getValue() );
  }

  @Test
  public void testCalculateStdDev3Count() throws KettlePluginException, KettleValueException {
    StandardDeviationCalculator calc = new StandardDeviationCalculator( "test" );
    Map<Class<? extends UnivariateStatsValueProducer>, UnivariateStatsValueProducer> producerMap =
        new HashMap<Class<? extends UnivariateStatsValueProducer>, UnivariateStatsValueProducer>();
    producerMap.put( CountValueProcessor.class, mockProducer( 3.0 ) );
    producerMap.put( SumValueProcessor.class, mockProducer( 250.0 ) );
    producerMap.put( SumOfSquaresValueProcessor.class, mockProducer( 35000.3 ) );
    calc.process( producerMap );
    assertEquals( Math.sqrt( ( 35000.3 - ( 250.0 * 250.0 ) / 3.0 ) / ( 3.0 - 1 ) ), calc.getValue() );
  }

  @Test
  public void testCalculateStdDev3CountNegative() throws KettlePluginException, KettleValueException {
    StandardDeviationCalculator calc = new StandardDeviationCalculator( "test" );
    Map<Class<? extends UnivariateStatsValueProducer>, UnivariateStatsValueProducer> producerMap =
        new HashMap<Class<? extends UnivariateStatsValueProducer>, UnivariateStatsValueProducer>();
    producerMap.put( CountValueProcessor.class, mockProducer( 3.0 ) );
    producerMap.put( SumValueProcessor.class, mockProducer( 250.0 ) );
    producerMap.put( SumOfSquaresValueProcessor.class, mockProducer( 35.3 ) );
    calc.process( producerMap );
    assertEquals( 0.0, calc.getValue() );
  }
}
