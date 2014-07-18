package org.pentaho.di.trans.steps.univariatestats.calculators;

import static org.junit.Assert.assertEquals;
import static org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueProducerMockUtil.mockProducer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueProcessor;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueProducer;
import org.pentaho.di.trans.steps.univariatestats.stats.calculators.PercentileValueCalculator;
import org.pentaho.di.trans.steps.univariatestats.stats.processors.CachingValueProcessor;
import org.pentaho.di.trans.steps.univariatestats.stats.processors.CountValueProcessor;
import org.pentaho.di.trans.steps.univariatestats.stats.processors.MaxValueProcessor;
import org.pentaho.di.trans.steps.univariatestats.stats.processors.MinValueProcessor;

public class PercentileValueCalculatorTest extends AbstractProducerTestBase {
  @Override
  protected UnivariateStatsValueProducer getProducer() {
    return new PercentileValueCalculator();
  }

  @SuppressWarnings( "unchecked" )
  @Override
  protected List<Map<String, Object>> getRoundTripParameters() {
    return Arrays.asList( new MapBuilder<String, Object>().put( PercentileValueCalculator.PERCENTILE_NAME, .5 ).put(
        PercentileValueCalculator.INTERPOLATE_NAME, false ).build(), new MapBuilder<String, Object>().put(
          PercentileValueCalculator.PERCENTILE_NAME, .54 ).put( PercentileValueCalculator.INTERPOLATE_NAME, true )
            .build() );
  }

  @Test
  public void testPercentileValueCalculatorReturnsMinNoValuesNoInterpolate() throws KettlePluginException,
    KettleValueException {
    PercentileValueCalculator calculator = new PercentileValueCalculator();
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put( PercentileValueCalculator.PERCENTILE_NAME, .5 );
    parameters.put( PercentileValueCalculator.INTERPOLATE_NAME, false );
    calculator.setParameters( parameters );
    Map<String, UnivariateStatsValueProcessor> producerMap = new HashMap<String, UnivariateStatsValueProcessor>();
    producerMap.put( CountValueProcessor.ID, mockProducer( 0.0 ) );
    producerMap.put( MinValueProcessor.ID, mockProducer( Double.MIN_VALUE ) );
    producerMap.put( MaxValueProcessor.ID, mockProducer( Double.MAX_VALUE ) );
    producerMap.put( CachingValueProcessor.ID, mockProducer( Arrays.<Double>asList() ) );
    calculator.process( producerMap );
    assertEquals( Double.MIN_VALUE, calculator.getValue() );
  }

  @Test
  public void testPercentileValueCalculatorReturnsMinNoValuesInterpolate() throws KettlePluginException,
    KettleValueException {
    PercentileValueCalculator calculator = new PercentileValueCalculator();
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put( PercentileValueCalculator.PERCENTILE_NAME, .5 );
    parameters.put( PercentileValueCalculator.INTERPOLATE_NAME, true );
    calculator.setParameters( parameters );
    Map<String, UnivariateStatsValueProcessor> producerMap = new HashMap<String, UnivariateStatsValueProcessor>();
    producerMap.put( CountValueProcessor.ID, mockProducer( 0.0 ) );
    producerMap.put( MinValueProcessor.ID, mockProducer( Double.MIN_VALUE ) );
    producerMap.put( MaxValueProcessor.ID, mockProducer( Double.MAX_VALUE ) );
    producerMap.put( CachingValueProcessor.ID, mockProducer( Arrays.<Double>asList() ) );
    calculator.process( producerMap );
    assertEquals( Double.MIN_VALUE, calculator.getValue() );
  }

  @Test
  public void testPercentileValueCalculatorReturnsMinOneValueNoInterpolate() throws KettlePluginException,
    KettleValueException {
    PercentileValueCalculator calculator = new PercentileValueCalculator();
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put( PercentileValueCalculator.PERCENTILE_NAME, .5 );
    parameters.put( PercentileValueCalculator.INTERPOLATE_NAME, false );
    calculator.setParameters( parameters );
    Map<String, UnivariateStatsValueProcessor> producerMap = new HashMap<String, UnivariateStatsValueProcessor>();
    producerMap.put( CountValueProcessor.ID, mockProducer( 1.0 ) );
    producerMap.put( MinValueProcessor.ID, mockProducer( 250.0 ) );
    producerMap.put( MaxValueProcessor.ID, mockProducer( 250.0 ) );
    producerMap.put( CachingValueProcessor.ID, mockProducer( Arrays.<Double>asList( 250.0 ) ) );
    calculator.process( producerMap );
    assertEquals( 250.0, calculator.getValue() );
  }

  @Test
  public void testPercentileValueCalculatorReturnsMinOneValueInterpolate() throws KettlePluginException,
    KettleValueException {
    PercentileValueCalculator calculator = new PercentileValueCalculator();
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put( PercentileValueCalculator.PERCENTILE_NAME, .5 );
    parameters.put( PercentileValueCalculator.INTERPOLATE_NAME, true );
    calculator.setParameters( parameters );
    Map<String, UnivariateStatsValueProcessor> producerMap = new HashMap<String, UnivariateStatsValueProcessor>();
    producerMap.put( CountValueProcessor.ID, mockProducer( 1.0 ) );
    producerMap.put( MinValueProcessor.ID, mockProducer( 250.0 ) );
    producerMap.put( MaxValueProcessor.ID, mockProducer( 250.0 ) );
    producerMap.put( CachingValueProcessor.ID, mockProducer( Arrays.<Double>asList( 250.0 ) ) );
    calculator.process( producerMap );
    assertEquals( 250.0, calculator.getValue() );
  }

  @Test
  public void testPercentileValueCalculatorReturnsMiddleThreeValueNoInterpolate() throws KettlePluginException,
    KettleValueException {
    PercentileValueCalculator calculator = new PercentileValueCalculator();
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put( PercentileValueCalculator.PERCENTILE_NAME, .5 );
    parameters.put( PercentileValueCalculator.INTERPOLATE_NAME, false );
    calculator.setParameters( parameters );
    Map<String, UnivariateStatsValueProcessor> producerMap = new HashMap<String, UnivariateStatsValueProcessor>();
    producerMap.put( CountValueProcessor.ID, mockProducer( 3.0 ) );
    producerMap.put( MinValueProcessor.ID, mockProducer( 100.0 ) );
    producerMap.put( MaxValueProcessor.ID, mockProducer( 200.0 ) );
    producerMap.put( CachingValueProcessor.ID, mockProducer( Arrays.<Double>asList( 100.0, 125.0, 200.0 ) ) );
    calculator.process( producerMap );
    assertEquals( 125.0, calculator.getValue() );
  }

  @Test
  public void testPercentileValueCalculatorReturnsMiddleThreeValueInterpolate() throws KettlePluginException,
    KettleValueException {
    PercentileValueCalculator calculator = new PercentileValueCalculator();
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put( PercentileValueCalculator.PERCENTILE_NAME, .5 );
    parameters.put( PercentileValueCalculator.INTERPOLATE_NAME, true );
    calculator.setParameters( parameters );
    Map<String, UnivariateStatsValueProcessor> producerMap = new HashMap<String, UnivariateStatsValueProcessor>();
    producerMap.put( CountValueProcessor.ID, mockProducer( 3.0 ) );
    producerMap.put( MinValueProcessor.ID, mockProducer( 100.0 ) );
    producerMap.put( MaxValueProcessor.ID, mockProducer( 200.0 ) );
    producerMap.put( CachingValueProcessor.ID, mockProducer( Arrays.<Double>asList( 100.0, 125.0, 200.0 ) ) );
    calculator.process( producerMap );
    assertEquals( 125.0, calculator.getValue() );
  }

  @Test
  public void testPercentileValueCalculatorReturnsMiddleFourValueNoInterpolate() throws KettlePluginException,
    KettleValueException {
    PercentileValueCalculator calculator = new PercentileValueCalculator();
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put( PercentileValueCalculator.PERCENTILE_NAME, .5 );
    parameters.put( PercentileValueCalculator.INTERPOLATE_NAME, false );
    calculator.setParameters( parameters );
    Map<String, UnivariateStatsValueProcessor> producerMap = new HashMap<String, UnivariateStatsValueProcessor>();
    producerMap.put( CountValueProcessor.ID, mockProducer( 4.0 ) );
    producerMap.put( MinValueProcessor.ID, mockProducer( 100.0 ) );
    producerMap.put( MaxValueProcessor.ID, mockProducer( 200.0 ) );
    producerMap.put( CachingValueProcessor.ID, mockProducer( Arrays.<Double>asList( 100.0, 125.0, 126.0, 200.0 ) ) );
    calculator.process( producerMap );
    assertEquals( 125.5, calculator.getValue() );
  }

  @Test
  public void testPercentileValueCalculatorReturnsMiddleFourValueInterpolate() throws KettlePluginException,
    KettleValueException {
    PercentileValueCalculator calculator = new PercentileValueCalculator();
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put( PercentileValueCalculator.PERCENTILE_NAME, .5 );
    parameters.put( PercentileValueCalculator.INTERPOLATE_NAME, true );
    calculator.setParameters( parameters );
    Map<String, UnivariateStatsValueProcessor> producerMap = new HashMap<String, UnivariateStatsValueProcessor>();
    producerMap.put( CountValueProcessor.ID, mockProducer( 4.0 ) );
    producerMap.put( MinValueProcessor.ID, mockProducer( 100.0 ) );
    producerMap.put( MaxValueProcessor.ID, mockProducer( 200.0 ) );
    producerMap.put( CachingValueProcessor.ID, mockProducer( Arrays.<Double>asList( 100.0, 125.0, 126.0, 200.0 ) ) );
    calculator.process( producerMap );
    assertEquals( 125.5, calculator.getValue() );
  }

  @Test
  public void testPercentileValueMedianOutputMeta() throws KettlePluginException {
    PercentileValueCalculator calculator = new PercentileValueCalculator();
    String origin = UUID.randomUUID().toString();
    calculator.setOrigin( origin );
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put( PercentileValueCalculator.PERCENTILE_NAME, .5 );
    parameters.put( PercentileValueCalculator.INTERPOLATE_NAME, true );
    calculator.setParameters( parameters );
    assertEquals( origin + "(" + BaseMessages.getString( calculator.getClass(), PercentileValueCalculator.MEDIAN_NAME )
        + ")", calculator.getOutputValueMeta().getName() );
    assertEquals( ValueMetaInterface.TYPE_NUMBER, calculator.getOutputValueMeta().getType() );
  }

  @Test
  public void testPercentileValueNonMedianOutputMeta() throws KettlePluginException {
    PercentileValueCalculator calculator = new PercentileValueCalculator();
    String origin = UUID.randomUUID().toString();
    calculator.setOrigin( origin );
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put( PercentileValueCalculator.PERCENTILE_NAME, .55 );
    parameters.put( PercentileValueCalculator.INTERPOLATE_NAME, true );
    calculator.setParameters( parameters );
    assertEquals( origin + "("
        + BaseMessages.getString( calculator.getClass(), PercentileValueCalculator.TH_PERCENTILE_NAME, 55 ) + ")",
        calculator.getOutputValueMeta().getName() );
    assertEquals( ValueMetaInterface.TYPE_NUMBER, calculator.getOutputValueMeta().getType() );
  }
}
