package org.pentaho.di.trans.steps.univariatestats.stats.processors;

import java.util.List;
import java.util.Map;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueProcessor;
import org.pentaho.di.trans.steps.univariatestats.stats.UnivariateValueProcessorPlugin;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;

import com.clearspring.analytics.stream.quantile.TDigest;

@UnivariateValueProcessorPlugin( id = TDigestValueProcessor.ID, name = TDigestValueProcessor.NAME,
    parameterNames = { TDigestValueProcessor.COMPRESSION_NAME },
    parameterTypes = { TDigestValueProcessor.COMPRESSION_TYPE } )
public class TDigestValueProcessor extends AbstractValueProducer implements UnivariateStatsValueProcessor {
  public static final String ID = "TDIGEST";
  public static final String NAME = "TDigestValueProcessor.Name";

  public static final String COMPRESSION_NAME = "TDigestValueProcessor.Compression.Name";
  public static final int COMPRESSION_TYPE = ValueMetaInterface.TYPE_NUMBER;

  private double compression = -1;
  private TDigest tdigest = null;

  public TDigestValueProcessor() {
    super( "TDIGEST", ValueMetaInterface.TYPE_NONE );
  }

  @Override
  public Object getValue() {
    return tdigest;
  }

  @Override
  public void setParameters( Map<String, Object> parameters ) {
    super.setParameters( parameters );
    compression = (Double) parameters.get( COMPRESSION_NAME );
  }

  @Override
  public Map<String, Object> getParameters() {
    Map<String, Object> result = super.getParameters();
    result.put( COMPRESSION_NAME, compression );
    return result;
  }

  @Override
  public void process( ValueMetaInterface inputMeta, Object input ) throws KettleException {
    if ( input != null ) {
      if ( tdigest == null ) {
        tdigest = new TDigest( compression );
      }
      tdigest.add( inputMeta.getNumber( input ).doubleValue() );
    }
  }

  @Override
  public void readRep( Repository rep, IMetaStore metaStore, ObjectId id_step, List<DatabaseMeta> databases, int nr,
      String prefix ) throws KettleException {
    super.readRep( rep, metaStore, id_step, databases, nr, prefix );
    String percentileString = Const.NVL( rep.getStepAttributeString( id_step, nr, prefix + "-compression" ), "0" );
    compression = Double.parseDouble( percentileString );
  }

  @Override
  public void saveRep( Repository rep, IMetaStore metaStore, ObjectId id_transformation, ObjectId id_step, int nr,
      String prefix ) throws KettleException {
    super.saveRep( rep, metaStore, id_transformation, id_step, nr, prefix );
    String percentileString = Double.toString( compression );
    rep.saveStepAttribute( id_transformation, id_step, nr, prefix + "-compression", percentileString );
  }

  @Override
  public String getXml() {
    StringBuilder sb = new StringBuilder( super.getXml() );
    sb.append( XMLHandler.addTagValue( "compression", Double.toString( compression ) ) );
    return sb.toString();
  }

  @Override
  public void loadXML( Node producerNode, List<DatabaseMeta> databases, IMetaStore metaStore )
    throws KettleXMLException {
    super.loadXML( producerNode, databases, metaStore );
    compression = Double.parseDouble( Const.NVL( XMLHandler.getTagValue( producerNode, "compression" ), "0" ) );
  }
}
