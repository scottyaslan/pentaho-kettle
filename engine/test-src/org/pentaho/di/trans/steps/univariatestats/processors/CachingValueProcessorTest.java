package org.pentaho.di.trans.steps.univariatestats.processors;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueProcessor;
import org.pentaho.di.trans.steps.univariatestats.stats.processors.CachingValueProcessor;

public class CachingValueProcessorTest extends AbstractProcessorTest {

  @Override
  protected Object getExpectedValue( ValueMetaInterface vmi, List<Object> sourceList ) {
    List<Double> result = new ArrayList<Double>( sourceList.size() );
    for ( Object obj : sourceList ) {
      if ( obj != null ) {
        try {
          result.add( vmi.getNumber( obj ).doubleValue() );
        } catch ( KettleValueException e ) {
          // Ignore
        }
      }
    }
    Collections.sort( result );
    return result;
  }

  @Override
  protected UnivariateStatsValueProcessor getProcessor() {
    return new CachingValueProcessor();
  }

  @Test
  public void testGetValueMeta() throws KettlePluginException {
    CachingValueProcessor cachingValueProcessor = new CachingValueProcessor();
    String origin = UUID.randomUUID().toString();
    cachingValueProcessor.setOrigin( origin );
    ValueMetaInterface vmi = cachingValueProcessor.getOutputValueMeta();
    assertEquals( ValueMetaInterface.TYPE_NONE, vmi.getType() );
    assertEquals( origin + "(" + BaseMessages.getString( CachingValueProcessor.class, CachingValueProcessor.NAME )
        + ")", vmi.getName() );
  }
}
