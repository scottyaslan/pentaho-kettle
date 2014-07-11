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
import org.pentaho.di.trans.steps.univariatestats.stats.processors.TDigestValueProcessor;

import com.clearspring.analytics.stream.quantile.TDigest;

@UnivariateValueCalculatorPlugin( id = TDigestQuantileValueCalculator.ID, name = TDigestQuantileValueCalculator.NAME,
    requiredProcessors = { TDigestValueProcessor.ID },
    parameterNames = { TDigestQuantileValueCalculator.PERCENTILE_NAME },
    parameterTypes = { TDigestQuantileValueCalculator.PERCENTILE_TYPE } )
public class TDigestQuantileValueCalculator extends AbstractValueProducer implements UnivariateStatsValueCalculator {
  public static final String ID = "T_DIGEST_PERCENTILE_VALUE_CALCULATOR";
  public static final String NAME = "percentile";

  public static final String PERCENTILE_NAME = "PercentileValueCalculator.Percentile.Name";
  public static final int PERCENTILE_TYPE = ValueMetaInterface.TYPE_NUMBER;

  double quantileResult = Double.NaN;

  public TDigestQuantileValueCalculator( ) {
    super( null /* PercentileValueCalculator.getName( quantile )*/, ValueMetaInterface.TYPE_NUMBER );
  }

  @Override
  public Object getValue() {
    return quantileResult;
  }

  @Override
  public void process( Map<String, UnivariateStatsValueProcessor> producerMap ) throws KettleValueException,
    KettlePluginException {
    UnivariateStatsValueProducer tdigestProcessor = producerMap.get( TDigestValueProcessor.ID );
    quantileResult = ( (TDigest) ( tdigestProcessor.getValue() ) ).quantile( .5 );
  }
}
