package org.pentaho.di.trans.steps.univariatestats;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.row.ValueMetaInterface;

public class UnivariateStatsValueProducerMockUtil {
  public static UnivariateStatsValueProcessor mockProducer( Double number ) throws KettlePluginException,
    KettleValueException {
    Object testObj = new Object();
    UnivariateStatsValueProcessor mockProducer = mock( UnivariateStatsValueProcessor.class );
    ValueMetaInterface mockValueMetaInterface = mock( ValueMetaInterface.class );
    when( mockProducer.getOutputValueMeta() ).thenReturn( mockValueMetaInterface );
    when( mockProducer.getValue() ).thenReturn( testObj );
    when( mockValueMetaInterface.getNumber( testObj ) ).thenReturn( number );
    return mockProducer;
  }
}
