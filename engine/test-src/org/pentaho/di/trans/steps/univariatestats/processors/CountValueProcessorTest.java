package org.pentaho.di.trans.steps.univariatestats.processors;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueProcessor;
import org.pentaho.di.trans.steps.univariatestats.stats.processors.CountValueProcessor;

public class CountValueProcessorTest extends AbstractProcessorTest {

  @Override
  protected Object getExpectedValue( ValueMetaInterface vmi, List<Object> sourceList ) {
    long count = 0;
    for ( Object num : sourceList ) {
      if ( num != null ) {
        count++;
      }
    }
    return count;
  }

  @Override
  protected UnivariateStatsValueProcessor getProcessor() {
    return new CountValueProcessor();
  }

  @Test
  public void testGetValueMeta() throws KettlePluginException {
    CountValueProcessor countValueProcessor = new CountValueProcessor();
    String origin = UUID.randomUUID().toString();
    countValueProcessor.setOrigin( origin );
    ValueMetaInterface vmi = countValueProcessor.getOutputValueMeta();
    assertEquals( ValueMetaInterface.TYPE_INTEGER, vmi.getType() );
    assertEquals( origin + "(" + BaseMessages.getString( CountValueProcessor.class, CountValueProcessor.NAME ) + ")",
        vmi.getName() );
  }
}
