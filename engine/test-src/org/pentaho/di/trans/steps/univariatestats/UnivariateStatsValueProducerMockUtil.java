package org.pentaho.di.trans.steps.univariatestats;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.row.ValueMetaInterface;

public class UnivariateStatsValueProducerMockUtil {
  public static UnivariateStatsValueProducer mockProducer( Double number ) throws KettlePluginException,
    KettleValueException {
    Object testObj = new Object();
    UnivariateStatsValueProducer mockProducer = mock( UnivariateStatsValueProducer.class );
    ValueMetaInterface mockValueMetaInterface = mock( ValueMetaInterface.class );
    when( mockProducer.getOutputValueMeta() ).thenReturn( mockValueMetaInterface );
    when( mockProducer.getValue() ).thenReturn( testObj );
    when( mockValueMetaInterface.getNumber( testObj ) ).thenReturn( number );
    return mockProducer;
  }
}
