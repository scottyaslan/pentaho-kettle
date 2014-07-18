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
import org.pentaho.di.trans.steps.univariatestats.stats.processors.MinValueProcessor;

public class MinValueProcessorTest extends AbstractProcessorTestBase {
  @Override
  protected UnivariateStatsValueProcessor getProcessor() {
    return new MinValueProcessor();
  }

  @Override
  protected Object getExpectedValue( ValueMetaInterface vmi, List<Object> sourceList ) {
    double min = Double.MAX_VALUE;
    for ( Object source : sourceList ) {
      if ( source != null ) {
        try {
          min = Math.min( min, vmi.getNumber( source ).doubleValue() );
        } catch ( KettleValueException e ) {
          // ignore
        }
      }
    }
    return min;
  }

  @Test
  public void testGetValueMeta() throws KettlePluginException {
    MinValueProcessor minValueProcessor = new MinValueProcessor();
    String origin = UUID.randomUUID().toString();
    minValueProcessor.setOrigin( origin );
    ValueMetaInterface vmi = minValueProcessor.getOutputValueMeta();
    assertEquals( ValueMetaInterface.TYPE_NUMBER, vmi.getType() );
    assertEquals( origin + "(" + BaseMessages.getString( MinValueProcessor.class, MinValueProcessor.NAME ) + ")", vmi
        .getName() );
  }
}
