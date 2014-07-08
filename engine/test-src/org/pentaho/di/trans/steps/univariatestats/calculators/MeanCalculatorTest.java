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
import org.pentaho.di.trans.steps.univariatestats.processors.SumValueProcessor;

public class MeanCalculatorTest {
  @Test
  public void testCalculateStdDev0Count() throws KettleValueException, KettlePluginException {
    MeanValueCalculator calc = new MeanValueCalculator( "test" );
    Map<Class<? extends UnivariateStatsValueProducer>, UnivariateStatsValueProducer> producerMap =
        new HashMap<Class<? extends UnivariateStatsValueProducer>, UnivariateStatsValueProducer>();
    producerMap.put( CountValueProcessor.class, mockProducer( 0.0 ) );
    producerMap.put( SumValueProcessor.class, mockProducer( 250.0 ) );
    calc.process( producerMap );
    assertEquals( Double.NaN, calc.getValue() );
  }

  @Test
  public void testCalculateStdDev3Count() throws KettlePluginException, KettleValueException {
    MeanValueCalculator calc = new MeanValueCalculator( "test" );
    Map<Class<? extends UnivariateStatsValueProducer>, UnivariateStatsValueProducer> producerMap =
        new HashMap<Class<? extends UnivariateStatsValueProducer>, UnivariateStatsValueProducer>();
    producerMap.put( CountValueProcessor.class, mockProducer( 3.0 ) );
    producerMap.put( SumValueProcessor.class, mockProducer( 250.0 ) );
    calc.process( producerMap );
    assertEquals( 250.0 / 3.0, calc.getValue() );
  }
}
