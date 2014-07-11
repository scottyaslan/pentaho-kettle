package org.pentaho.di.trans.steps.univariatestats.stats.processors;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueProcessor;
import org.pentaho.di.trans.steps.univariatestats.stats.UnivariateValueProcessorPlugin;

import com.clearspring.analytics.stream.quantile.TDigest;

@UnivariateValueProcessorPlugin( id = TDigestValueProcessor.ID, name = TDigestValueProcessor.NAME,
    parameterNames = { TDigestValueProcessor.COMPRESSION_NAME },
    parameterTypes = { ValueMetaInterface.TYPE_NUMBER } )
public class TDigestValueProcessor extends AbstractValueProducer implements UnivariateStatsValueProcessor {
  public static final String ID = "TDIGEST";
  public static final String NAME = "tdigest";

  public static final String COMPRESSION_NAME = "TDigestValueProcessor.Compression.Name";
  public static final Class<?> COMPRESSION_TYPE = double.class;

  private final TDigest tdigest;

  public TDigestValueProcessor( ) {
    super( "TDIGEST", ValueMetaInterface.TYPE_NONE );
    tdigest = null; //new TDigest( compression );
  }

  @Override
  public Object getValue() {
    return tdigest;
  }

  @Override
  public void process( ValueMetaInterface inputMeta, Object input ) throws KettleException {
    if ( input != null ) {
      tdigest.add( inputMeta.getNumber( input ).doubleValue() );
    }
  }
}
