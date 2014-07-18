package org.pentaho.di.trans.steps.univariatestats.processors;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
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
import org.pentaho.di.trans.steps.univariatestats.stats.processors.TDigestValueProcessor;

import com.clearspring.analytics.stream.quantile.TDigest;

public class TDigestValueProcessorTest extends AbstractProcessorTestBase {

  @Override
  protected Object getExpectedValue( ValueMetaInterface vmi, List<Object> sourceList ) {
    return null;
  }

  private Map<String, Object> createParameterMap( Double compression ) {
    Map<String, Object> result = new HashMap<String, Object>();
    result.put( TDigestValueProcessor.COMPRESSION_NAME, compression );
    return result;
  }

  @SuppressWarnings( "unchecked" )
  @Override
  protected List<Map<String, Object>> getRoundTripParameters() {
    return new ArrayList<Map<String, Object>>( Arrays.asList( createParameterMap( -1.0 ), createParameterMap( 100.0 ) ) );
  }

  @Override
  protected void validate( UnivariateStatsValueProcessor processor, ValueMetaInterface vmi, List<Object> sourceList ) {
    TDigest value = (TDigest) processor.getValue();
    int size = 0;
    for ( Object obj : sourceList ) {
      if ( obj != null ) {
        try {
          vmi.getNumber( obj ).doubleValue();
          size++;
        } catch ( KettleValueException e ) {
          // Ignore
        }
      }
    }
    assertEquals( size, value.size() );
  }

  @Override
  protected UnivariateStatsValueProcessor getProcessor() {
    TDigestValueProcessor result = new TDigestValueProcessor();
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put( TDigestValueProcessor.COMPRESSION_NAME, 100.0 );
    result.setParameters( parameters );
    return result;
  }

  @Test
  public void testGetValueMeta() throws KettlePluginException {
    TDigestValueProcessor tdigestValueProcessor = new TDigestValueProcessor();
    String origin = UUID.randomUUID().toString();
    tdigestValueProcessor.setOrigin( origin );
    ValueMetaInterface vmi = tdigestValueProcessor.getOutputValueMeta();
    assertEquals( ValueMetaInterface.TYPE_NONE, vmi.getType() );
    assertEquals( origin + "(" + BaseMessages.getString( TDigestValueProcessor.class, TDigestValueProcessor.NAME )
        + ")", vmi.getName() );
  }
}
