package org.pentaho.di.trans.steps.univariatestats.calculators;

import static org.junit.Assert.assertEquals;
import static org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueProducerMockUtil.mockProducer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueProcessor;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueProducer;
import org.pentaho.di.trans.steps.univariatestats.stats.calculators.MeanValueCalculator;
import org.pentaho.di.trans.steps.univariatestats.stats.processors.CountValueProcessor;
import org.pentaho.di.trans.steps.univariatestats.stats.processors.SumValueProcessor;

public class MeanCalculatorTest extends AbstractProducerTestBase {

  @Override
  protected UnivariateStatsValueProducer getProducer() {
    return new MeanValueCalculator();
  }

  @Test
  public void testCalculateMeanDev0Count() throws KettleValueException, KettlePluginException {
    MeanValueCalculator calc = new MeanValueCalculator();
    Map<String, UnivariateStatsValueProcessor> producerMap = new HashMap<String, UnivariateStatsValueProcessor>();
    producerMap.put( CountValueProcessor.ID, mockProducer( 0.0 ) );
    producerMap.put( SumValueProcessor.ID, mockProducer( 250.0 ) );
    calc.process( producerMap );
    assertEquals( Double.NaN, calc.getValue() );
  }

  @Test
  public void testCalculateMeanDev3Count() throws KettlePluginException, KettleValueException {
    MeanValueCalculator calc = new MeanValueCalculator();
    Map<String, UnivariateStatsValueProcessor> producerMap = new HashMap<String, UnivariateStatsValueProcessor>();
    producerMap.put( CountValueProcessor.ID, mockProducer( 3.0 ) );
    producerMap.put( SumValueProcessor.ID, mockProducer( 250.0 ) );
    calc.process( producerMap );
    assertEquals( 250.0 / 3.0, calc.getValue() );
  }

  @Test
  public void testGetValueMeta() throws KettlePluginException {
    MeanValueCalculator calc = new MeanValueCalculator();
    String origin = UUID.randomUUID().toString();
    calc.setOrigin( origin );
    assertEquals( origin + "(" + BaseMessages.getString( calc.getClass(), MeanValueCalculator.NAME ) + ")", calc
        .getOutputValueMeta().getName() );
    assertEquals( ValueMetaInterface.TYPE_NUMBER, calc.getOutputValueMeta().getType() );
  }
}
