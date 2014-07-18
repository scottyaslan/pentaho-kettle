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
import org.pentaho.di.trans.steps.univariatestats.stats.processors.MaxValueProcessor;

public class MaxValueProcessorTest extends AbstractProcessorTestBase {

  @Override
  protected Object getExpectedValue( ValueMetaInterface vmi, List<Object> sourceList ) {
    double max = Double.MIN_VALUE;
    for ( Object source : sourceList ) {
      if ( source != null ) {
        try {
          max = Math.max( max, vmi.getNumber( source ).doubleValue() );
        } catch ( KettleValueException e ) {
          // ignore
        }
      }
    }
    return max;
  }

  @Override
  protected UnivariateStatsValueProcessor getProcessor() {
    return new MaxValueProcessor();
  }

  @Test
  public void testGetValueMeta() throws KettlePluginException {
    MaxValueProcessor maxValueProcessor = new MaxValueProcessor();
    String origin = UUID.randomUUID().toString();
    maxValueProcessor.setOrigin( origin );
    ValueMetaInterface vmi = maxValueProcessor.getOutputValueMeta();
    assertEquals( ValueMetaInterface.TYPE_NUMBER, vmi.getType() );
    assertEquals( origin + "(" + BaseMessages.getString( MaxValueProcessor.class, MaxValueProcessor.NAME ) + ")", vmi
        .getName() );
  }
}
