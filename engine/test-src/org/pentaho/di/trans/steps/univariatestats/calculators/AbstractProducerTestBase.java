package org.pentaho.di.trans.steps.univariatestats.calculators;

import static org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueProducerMockUtil.testProducerRoundTrip;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.row.value.ValueMetaPluginType;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueProducer;

public abstract class AbstractProducerTestBase {

  @BeforeClass
  public static void beforeClass() throws KettlePluginException {
    ValueMetaPluginType.getInstance().searchPlugins();
  }

  protected abstract UnivariateStatsValueProducer getProducer();

  @SuppressWarnings( "unchecked" )
  protected List<Map<String, Object>> getRoundTripParameters() {
    return new ArrayList<Map<String, Object>>( Arrays.asList( new HashMap<String, Object>() ) );
  }

  @Test
  public void testRoundTrip() throws Exception {
    for ( Map<String, Object> parameters : getRoundTripParameters() ) {
      String origin = UUID.randomUUID().toString();
      UnivariateStatsValueProducer producer = getProducer();
      producer.setOrigin( origin );
      producer.setParameters( parameters );
      testProducerRoundTrip( producer );
    }
  }
}
