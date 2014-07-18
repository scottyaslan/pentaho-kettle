package org.pentaho.di.trans.steps.univariatestats.stats.calculators;

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
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueCalculator;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueProcessor;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueProducer;
import org.pentaho.di.trans.steps.univariatestats.stats.UnivariateValueCalculatorPlugin;
import org.pentaho.di.trans.steps.univariatestats.stats.processors.AbstractValueProducer;
import org.pentaho.di.trans.steps.univariatestats.stats.processors.TDigestValueProcessor;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;

import com.clearspring.analytics.stream.quantile.TDigest;

@UnivariateValueCalculatorPlugin( id = TDigestQuantileValueCalculator.ID, name = TDigestQuantileValueCalculator.NAME,
    requiredProcessors = { TDigestValueProcessor.ID }, parameterNames = { PercentileValueCalculator.PERCENTILE_NAME },
    parameterTypes = { PercentileValueCalculator.PERCENTILE_TYPE }, provides = PercentileValueCalculator.PROVIDES )
public class TDigestQuantileValueCalculator extends AbstractValueProducer implements UnivariateStatsValueCalculator {
  public static final String ID = "T_DIGEST_PERCENTILE_VALUE_CALCULATOR";
  public static final String NAME = "TDigestQuantileValueCalculator.Name";

  double percentile = 0;
  double quantileResult = Double.NaN;

  public TDigestQuantileValueCalculator() {
    super( null, ValueMetaInterface.TYPE_NUMBER );
  }

  @Override
  public Object getValue() {
    return quantileResult;
  }

  @Override
  public void process( Map<String, UnivariateStatsValueProcessor> producerMap ) throws KettleValueException,
    KettlePluginException {
    UnivariateStatsValueProducer tdigestProcessor = producerMap.get( TDigestValueProcessor.ID );
    quantileResult = ( (TDigest) ( tdigestProcessor.getValue() ) ).quantile( percentile );
  }

  @Override
  public String getName() {
    return PercentileValueCalculator.getName( percentile );
  }

  @Override
  public void setParameters( Map<String, Object> parameters ) {
    super.setParameters( parameters );
    percentile = (Double) parameters.get( PercentileValueCalculator.PERCENTILE_NAME );
    setName( PercentileValueCalculator.getName( percentile ) );
  }

  @Override
  public Map<String, Object> getParameters() {
    Map<String, Object> result = super.getParameters();
    result.put( PercentileValueCalculator.PERCENTILE_NAME, percentile );
    return result;
  }

  @Override
  public void readRep( Repository rep, IMetaStore metaStore, ObjectId id_step, List<DatabaseMeta> databases, int nr,
      String prefix ) throws KettleException {
    super.readRep( rep, metaStore, id_step, databases, nr, prefix );
    String percentileString = Const.NVL( rep.getStepAttributeString( id_step, nr, prefix + "-percentile" ), "0" );
    percentile = Double.parseDouble( percentileString );
  }

  @Override
  public void saveRep( Repository rep, IMetaStore metaStore, ObjectId id_transformation, ObjectId id_step, int nr,
      String prefix ) throws KettleException {
    super.saveRep( rep, metaStore, id_transformation, id_step, nr, prefix );
    String percentileString = Double.toString( percentile );
    rep.saveStepAttribute( id_transformation, id_step, nr, prefix + "-percentile", percentileString );
  }

  @Override
  public String getXml() {
    StringBuilder sb = new StringBuilder( super.getXml() );
    sb.append( XMLHandler.addTagValue( "percentile", Double.toString( percentile ) ) );
    return sb.toString();
  }

  @Override
  public void loadXML( Node producerNode, List<DatabaseMeta> databases, IMetaStore metaStore )
    throws KettleXMLException {
    super.loadXML( producerNode, databases, metaStore );
    percentile = Double.parseDouble( Const.NVL( XMLHandler.getTagValue( producerNode, "percentile" ), "0" ) );
  }
}
