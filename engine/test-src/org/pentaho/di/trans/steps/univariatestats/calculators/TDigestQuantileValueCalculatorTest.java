package org.pentaho.di.trans.steps.univariatestats.calculators;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueProducerMockUtil.mockProducer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueProducer;
import org.pentaho.di.trans.steps.univariatestats.stats.calculators.PercentileValueCalculator;
import org.pentaho.di.trans.steps.univariatestats.stats.calculators.TDigestQuantileValueCalculator;
import org.pentaho.di.trans.steps.univariatestats.stats.processors.TDigestValueProcessor;

import com.clearspring.analytics.stream.quantile.TDigest;

public class TDigestQuantileValueCalculatorTest extends AbstractProducerTestBase {

  @BeforeClass
  public static void beforeClass() throws KettlePluginException {
    ValueMetaPluginType.getInstance().searchPlugins();
  }

  @Override
  protected UnivariateStatsValueProducer getProducer() {
    return new TDigestQuantileValueCalculator();
  }

  @SuppressWarnings( "unchecked" )
  @Override
  protected List<Map<String, Object>> getRoundTripParameters() {
    return Arrays.asList(
        new MapBuilder<String, Object>().put( PercentileValueCalculator.PERCENTILE_NAME, .5 ).build(),
        new MapBuilder<String, Object>().put( PercentileValueCalculator.PERCENTILE_NAME, .54 ).build() );
  }

  @Test
  public void testTDigestQuantileValueCalculatorReturnsTDigestQuantile() throws KettlePluginException, KettleValueException {
    TDigestQuantileValueCalculator calculator = new TDigestQuantileValueCalculator();
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put( PercentileValueCalculator.PERCENTILE_NAME, .555 );
    calculator.setParameters( parameters );
    Map<String, UnivariateStatsValueProcessor> producerMap = new HashMap<String, UnivariateStatsValueProcessor>();
    TDigest mockTDigest = mock( TDigest.class );
    when( mockTDigest.quantile( .555 ) ).thenReturn( .75 );
    producerMap.put( TDigestValueProcessor.ID, mockProducer( mockTDigest ) );
    calculator.process( producerMap );
    assertEquals( .75, calculator.getValue() );
  }

  @Test
  public void testPercentileValueMedianOutputMeta() throws KettlePluginException {
    TDigestQuantileValueCalculator calculator = new TDigestQuantileValueCalculator();
    String origin = UUID.randomUUID().toString();
    calculator.setOrigin( origin );
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put( PercentileValueCalculator.PERCENTILE_NAME, .5 );
    calculator.setParameters( parameters );
    assertEquals( origin + "(" + BaseMessages.getString( calculator.getClass(), PercentileValueCalculator.MEDIAN_NAME )
        + ")", calculator.getOutputValueMeta().getName() );
    assertEquals( ValueMetaInterface.TYPE_NUMBER, calculator.getOutputValueMeta().getType() );
  }

  @Test
  public void testPercentileValueNonMedianOutputMeta() throws KettlePluginException {
    TDigestQuantileValueCalculator calculator = new TDigestQuantileValueCalculator();
    String origin = UUID.randomUUID().toString();
    calculator.setOrigin( origin );
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put( PercentileValueCalculator.PERCENTILE_NAME, .55 );
    calculator.setParameters( parameters );
    assertEquals( origin + "("
        + BaseMessages.getString( calculator.getClass(), PercentileValueCalculator.TH_PERCENTILE_NAME, 55 ) + ")",
        calculator.getOutputValueMeta().getName() );
    assertEquals( ValueMetaInterface.TYPE_NUMBER, calculator.getOutputValueMeta().getType() );
  }
}
