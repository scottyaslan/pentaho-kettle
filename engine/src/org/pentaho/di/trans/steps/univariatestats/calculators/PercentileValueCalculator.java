package org.pentaho.di.trans.steps.univariatestats.calculators;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueProducer;
import org.pentaho.di.trans.steps.univariatestats.processors.CacheingValueProcessor;
import org.pentaho.di.trans.steps.univariatestats.processors.CountValueProcessor;
import org.pentaho.di.trans.steps.univariatestats.processors.MaxValueProcessor;
import org.pentaho.di.trans.steps.univariatestats.processors.MinValueProcessor;

public class PercentileValueCalculator extends AbstractValueCalculator {
  private final double percentile;
  private final boolean interpolate;
  private double result;

  private static String getName( double percentile ) {
    if ( percentile == .5 ) {
      return "median";
    } else {
      NumberFormat pF = NumberFormat.getInstance();
      pF.setMaximumFractionDigits( 2 );
      String res = pF.format( percentile * 100 );
      return res + "th percentile";
    }
  }

  @SuppressWarnings( "unchecked" )
  public PercentileValueCalculator( String origin, double percentile, boolean interpolate ) {
    super( getName( percentile ), origin, ValueMetaInterface.TYPE_NUMBER,
        new HashSet<Class<? extends UnivariateStatsValueProducer>>( Arrays.asList( CountValueProcessor.class,
            MinValueProcessor.class, MaxValueProcessor.class, CacheingValueProcessor.class ) ) );
    this.percentile = percentile;
    this.interpolate = interpolate;
  }

  @Override
  public Object getValue() {
    return result;
  }

  @Override
  protected void process( Map<Class<? extends UnivariateStatsValueProducer>, UnivariateStatsValueProducer> producerMap )
    throws KettleValueException, KettlePluginException {
    UnivariateStatsValueProducer countProducer = producerMap.get( CountValueProcessor.class );
    UnivariateStatsValueProducer minProducer = producerMap.get( MinValueProcessor.class );
    UnivariateStatsValueProducer maxProducer = producerMap.get( MaxValueProcessor.class );
    UnivariateStatsValueProducer cacheProducer = producerMap.get( CacheingValueProcessor.class );

    double count = countProducer.getOutputValueMeta().getNumber( countProducer.getValue() ).doubleValue();
    double min = minProducer.getOutputValueMeta().getNumber( minProducer.getValue() ).doubleValue();
    double max = maxProducer.getOutputValueMeta().getNumber( maxProducer.getValue() ).doubleValue();
    @SuppressWarnings( "unchecked" )
    List<Double> cache = (List<Double>) cacheProducer.getValue();
    result = percentile( percentile, cache, interpolate, count, min, max );
  }

  /**
   * Compute a percentile. Can compute percentiles using interpolation or a simple method (see <a
   * href="http://www.itl.nist.gov/div898/handbook/prc/section2/prc252.htm" The Engineering Statistics Handbook</a> for
   * details).
   * 
   * 
   * @param p
   *          the percentile to compute (0 <= p <= 1)
   * @param vals
   *          a sorted array of values to compute the percentile from
   * @param interpolate
   *          true if interpolation is to be used
   * @return the percentile value
   */
  private double percentile( double p, List<Double> vals, boolean interpolate, double m_count, double m_min,
      double m_max ) {
    double n = m_count;

    // interpolation
    if ( interpolate ) {
      double i = p * ( n + 1 );
      // special cases
      if ( i <= 1 ) {
        return m_min;
      }
      if ( i >= n ) {
        return m_max;
      }
      double low_obs = Math.floor( i );
      double high_obs = low_obs + 1;

      double r1 = high_obs - i;
      double r2 = 1.0 - r1;

      double x1 = vals.get( (int) low_obs - 1 );
      double x2 = vals.get( (int) high_obs - 1 );

      return ( r1 * x1 ) + ( r2 * x2 );
    }

    // simple method
    double i = p * n;
    double res = 0;
    if ( i == 0 ) {
      return m_min;
    }
    if ( i == n ) {
      return m_max;
    }
    if ( i - Math.floor( i ) > 0 ) {
      i = Math.floor( i );
      res = vals.get( (int) i );
    } else {
      res = ( vals.get( (int) ( i - 1 ) ) + vals.get( (int) i ) ) / 2.0;
    }
    return res;
  }
}
