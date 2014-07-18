package org.pentaho.di.trans.steps.univariatestats.calculators;

import static org.junit.Assert.assertEquals;
import static org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueProducerMockUtil.mockProducer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaPluginType;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueProcessor;
import org.pentaho.di.trans.steps.univariatestats.stats.calculators.StandardDeviationCalculator;
import org.pentaho.di.trans.steps.univariatestats.stats.processors.CountValueProcessor;
import org.pentaho.di.trans.steps.univariatestats.stats.processors.SumOfSquaresValueProcessor;
import org.pentaho.di.trans.steps.univariatestats.stats.processors.SumValueProcessor;

public class StandardDeviationCalculatorTest {

  @BeforeClass
  public static void beforeClass() throws KettlePluginException {
    ValueMetaPluginType.getInstance().searchPlugins();
  }

  @Test
  public void testCalculateStdDev0Count() throws KettleValueException, KettlePluginException {
    StandardDeviationCalculator calc = new StandardDeviationCalculator();
    Map<String, UnivariateStatsValueProcessor> producerMap = new HashMap<String, UnivariateStatsValueProcessor>();
    producerMap.put( CountValueProcessor.ID, mockProducer( 0.0 ) );
    calc.process( producerMap );
    assertEquals( Double.NaN, calc.getValue() );
  }

  @Test
  public void testCalculateStdDev1Count() throws KettlePluginException, KettleValueException {
    StandardDeviationCalculator calc = new StandardDeviationCalculator();
    Map<String, UnivariateStatsValueProcessor> producerMap = new HashMap<String, UnivariateStatsValueProcessor>();
    producerMap.put( CountValueProcessor.ID, mockProducer( 1.0 ) );
    calc.process( producerMap );
    assertEquals( Double.POSITIVE_INFINITY, calc.getValue() );
  }

  @Test
  public void testCalculateStdDev3Count() throws KettlePluginException, KettleValueException {
    StandardDeviationCalculator calc = new StandardDeviationCalculator();
    Map<String, UnivariateStatsValueProcessor> producerMap = new HashMap<String, UnivariateStatsValueProcessor>();
    producerMap.put( CountValueProcessor.ID, mockProducer( 3.0 ) );
    producerMap.put( SumValueProcessor.ID, mockProducer( 250.0 ) );
    producerMap.put( SumOfSquaresValueProcessor.ID, mockProducer( 35000.3 ) );
    calc.process( producerMap );
    assertEquals( Math.sqrt( ( 35000.3 - ( 250.0 * 250.0 ) / 3.0 ) / ( 3.0 - 1 ) ), calc.getValue() );
  }

  @Test
  public void testCalculateStdDev3CountNegative() throws KettlePluginException, KettleValueException {
    StandardDeviationCalculator calc = new StandardDeviationCalculator();
    Map<String, UnivariateStatsValueProcessor> producerMap = new HashMap<String, UnivariateStatsValueProcessor>();
    producerMap.put( CountValueProcessor.ID, mockProducer( 3.0 ) );
    producerMap.put( SumValueProcessor.ID, mockProducer( 250.0 ) );
    producerMap.put( SumOfSquaresValueProcessor.ID, mockProducer( 35.3 ) );
    calc.process( producerMap );
    assertEquals( 0.0, calc.getValue() );
  }

  @Test
  public void testGetValueMeta() throws KettlePluginException {
    StandardDeviationCalculator calc = new StandardDeviationCalculator();
    String origin = UUID.randomUUID().toString();
    calc.setOrigin( origin );
    assertEquals( origin + "(" + BaseMessages.getString( calc.getClass(), StandardDeviationCalculator.NAME ) + ")",
        calc.getOutputValueMeta().getName() );
    assertEquals( ValueMetaInterface.TYPE_NUMBER, calc.getOutputValueMeta().getType() );
  }
}
