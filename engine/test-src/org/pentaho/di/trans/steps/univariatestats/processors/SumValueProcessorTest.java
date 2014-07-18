package org.pentaho.di.trans.steps.univariatestats.processors;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueProcessor;
import org.pentaho.di.trans.steps.univariatestats.stats.processors.SumValueProcessor;

public class SumValueProcessorTest extends AbstractProcessorTestBase {

  @Override
  protected Object getExpectedValue( ValueMetaInterface vmi, List<Object> sourceList ) {
    double result = 0;
    for ( Object obj : sourceList ) {
      if ( obj != null ) {
        try {
          double value = vmi.getNumber( obj ).doubleValue();
          result += value;
        } catch ( KettleValueException e ) {
          // ignore
        }
      }
    }
    return result;
  }

  @Override
  protected UnivariateStatsValueProcessor getProcessor() {
    return new SumValueProcessor();
  }

  @Test
  public void testGetValueMeta() throws KettlePluginException {
    SumValueProcessor valueProcessor = new SumValueProcessor();
    String origin = UUID.randomUUID().toString();
    valueProcessor.setOrigin( origin );
    ValueMetaInterface vmi = valueProcessor.getOutputValueMeta();
    assertEquals( ValueMetaInterface.TYPE_NUMBER, vmi.getType() );
    assertEquals( origin + "(" + BaseMessages.getString( valueProcessor.getClass(), SumValueProcessor.NAME ) + ")", vmi
        .getName() );
  }
}
