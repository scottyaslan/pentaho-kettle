package org.pentaho.di.trans.steps.univariatestats.stats.calculators;

import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueCalculator;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueProcessor;
import org.pentaho.di.trans.steps.univariatestats.stats.UnivariateValueCalculatorPlugin;
import org.pentaho.di.trans.steps.univariatestats.stats.processors.AbstractValueProducer;
import org.pentaho.di.trans.steps.univariatestats.stats.processors.CachingValueProcessor;
import org.pentaho.di.trans.steps.univariatestats.stats.processors.CountValueProcessor;
import org.pentaho.di.trans.steps.univariatestats.stats.processors.MaxValueProcessor;
import org.pentaho.di.trans.steps.univariatestats.stats.processors.MinValueProcessor;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;

@UnivariateValueCalculatorPlugin(
    id = PercentileValueCalculator.ID,
    name = PercentileValueCalculator.NAME,
    requiredProcessors = { CountValueProcessor.ID, MinValueProcessor.ID, MaxValueProcessor.ID, CachingValueProcessor.ID },
    parameterNames = { PercentileValueCalculator.PERCENTILE_NAME, PercentileValueCalculator.INTERPOLATE_NAME },
    parameterTypes = { PercentileValueCalculator.PERCENTILE_TYPE, PercentileValueCalculator.INTERPOLATE_TYPE },
    provides = PercentileValueCalculator.PROVIDES )
public class PercentileValueCalculator extends AbstractValueProducer implements UnivariateStatsValueCalculator {
  private static final Class<?> PKG = PercentileValueCalculator.class;
  public static final String ID = "PERCENTILE_VALUE_CALCULATOR";
  public static final String NAME = "PercentileValueCalculator.Name";
  public static final String MEDIAN_NAME = "PercentileValueCalculator.Median.Name";
  public static final String TH_PERCENTILE_NAME = "PercentileValueCalculator.ThPercentile.Name";
  public static final String PROVIDES = "PercentileValueCalculator.Provides";

  public static final String PERCENTILE_NAME = "PercentileValueCalculator.Percentile.Name";
  public static final int PERCENTILE_TYPE = ValueMetaInterface.TYPE_NUMBER;

  public static final String INTERPOLATE_NAME = "PercentileValueCalculator.Interpolate.Name";
  public static final int INTERPOLATE_TYPE = ValueMetaInterface.TYPE_BOOLEAN;

  private double percentile;
  private boolean interpolate;
  private double result;

  public static String getName( double percentile ) {
    if ( percentile == .5 ) {
      return BaseMessages.getString( PKG, MEDIAN_NAME );
    } else {
      NumberFormat pF = NumberFormat.getInstance();
      pF.setMaximumFractionDigits( 2 );
      String res = pF.format( percentile * 100 );
      return BaseMessages.getString( PKG, TH_PERCENTILE_NAME, res );
    }
  }

  @Override
  public String getName() {
    return getName( percentile );
  }

  public PercentileValueCalculator() {
    super( null, ValueMetaInterface.TYPE_NUMBER );
  }

  @Override
  public void setParameters( Map<String, Object> parameters ) {
    super.setParameters( parameters );
    percentile = (Double) parameters.get( PERCENTILE_NAME );
    setName( PercentileValueCalculator.getName( percentile ) );
    if ( parameters.containsKey( INTERPOLATE_NAME ) ) {
      interpolate = (Boolean) parameters.get( INTERPOLATE_NAME );
    } else {
      interpolate = false;
    }
  }

  @Override
  public Map<String, Object> getParameters() {
    Map<String, Object> result = super.getParameters();
    result.put( PERCENTILE_NAME, percentile );
    result.put( INTERPOLATE_NAME, interpolate );
    return result;
  }

  @Override
  public Object getValue() {
    return result;
  }

  @Override
  public void process( Map<String, UnivariateStatsValueProcessor> producerMap ) throws KettleValueException,
    KettlePluginException {
    UnivariateStatsValueProcessor countProducer = producerMap.get( CountValueProcessor.ID );
    UnivariateStatsValueProcessor minProducer = producerMap.get( MinValueProcessor.ID );
    UnivariateStatsValueProcessor maxProducer = producerMap.get( MaxValueProcessor.ID );
    UnivariateStatsValueProcessor cacheProducer = producerMap.get( CachingValueProcessor.ID );

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

  public boolean isInterpolate() {
    return interpolate;
  }

  public double getPercentile() {
    return percentile;
  }

  @Override
  public void readRep( Repository rep, IMetaStore metaStore, ObjectId id_step, List<DatabaseMeta> databases, int nr,
      String prefix ) throws KettleException {
    super.readRep( rep, metaStore, id_step, databases, nr, prefix );
    interpolate = rep.getStepAttributeBoolean( id_step, nr, prefix + "-interpolate", false );
    String percentileString = Const.NVL( rep.getStepAttributeString( id_step, nr, prefix + "-percentile" ), "0" );
    percentile = Double.parseDouble( percentileString );
  }

  @Override
  public void saveRep( Repository rep, IMetaStore metaStore, ObjectId id_transformation, ObjectId id_step, int nr,
      String prefix ) throws KettleException {
    super.saveRep( rep, metaStore, id_transformation, id_step, nr, prefix );
    rep.saveStepAttribute( id_transformation, id_step, nr, prefix + "-interpolate", interpolate );
    String percentileString = Double.toString( percentile );
    rep.saveStepAttribute( id_transformation, id_step, nr, prefix + "-percentile", percentileString );
  }

  @Override
  public String getXml() {
    StringBuilder sb = new StringBuilder( super.getXml() );
    sb.append( XMLHandler.addTagValue( "interpolate", interpolate ) );
    sb.append( XMLHandler.addTagValue( "percentile", Double.toString( percentile ) ) );
    return sb.toString();
  }

  @Override
  public void loadXML( Node producerNode, List<DatabaseMeta> databases, IMetaStore metaStore )
    throws KettleXMLException {
    super.loadXML( producerNode, databases, metaStore );
    interpolate = !"N".equalsIgnoreCase( XMLHandler.getTagValue( producerNode, "interpolate" ) );
    percentile = Double.parseDouble( Const.NVL( XMLHandler.getTagValue( producerNode, "percentile" ), "0" ) );
  }
}
