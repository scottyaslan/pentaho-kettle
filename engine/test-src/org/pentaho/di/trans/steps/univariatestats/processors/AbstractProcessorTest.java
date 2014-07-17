package org.pentaho.di.trans.steps.univariatestats.processors;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaFactory;
import org.pentaho.di.core.row.value.ValueMetaPluginType;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueProcessor;

public abstract class AbstractProcessorTest {
  protected abstract Object getExpectedValue( ValueMetaInterface vmi, List<Object> sourceList );

  protected abstract UnivariateStatsValueProcessor getProcessor();

  protected void validate( UnivariateStatsValueProcessor processor, ValueMetaInterface vmi, List<Object> sourceList ) {
    assertEquals( getExpectedValue( vmi, sourceList ), processor.getValue() );
  }

  @BeforeClass
  public static void beforeClass() throws KettlePluginException {
    ValueMetaPluginType.getInstance().searchPlugins();
  }

  private void testList( List<Double> sourceList ) throws KettleException {
    ValueMetaInterface vmi = ValueMetaFactory.createValueMeta( ValueMetaInterface.TYPE_NUMBER );
    List<Object> processList = new ArrayList<Object>( sourceList );
    processList.add( null );
    processList.add( new Object() );
    UnivariateStatsValueProcessor processor = getProcessor();
    long timeBefore = System.currentTimeMillis();
    for ( Object value : processList ) {
      processor.process( vmi, value );
    }
    processor.getValue();
    long timeAfter = System.currentTimeMillis();
    System.out.println( getClass().getCanonicalName() + ": processing " + sourceList.size() + " elements took "
        + ( timeAfter - timeBefore ) + " ms" );
    validate( processor, vmi, processList );
  }

  @Test
  public void testNoValues() throws KettleException {
    testList( Arrays.<Double>asList() );
  }

  @Test
  public void test1Values() throws KettleException {
    testList( Arrays.<Double>asList( 1.25 ) );
  }

  @Test
  public void test2Values() throws KettleException {
    testList( Arrays.<Double>asList( 1.25, 2.5 ) );
  }

  @Test
  public void test500000Values() throws KettleException {
    List<Double> list = new ArrayList<Double>( 500000 );
    Random random = new Random();
    for ( int i = 0; i < 500000; i++ ) {
      list.add( random.nextDouble() );
    }
    testList( list );
  }

  @Test
  public void test1000000Values() throws KettleException {
    List<Double> list = new ArrayList<Double>( 1000000 );
    Random random = new Random();
    for ( int i = 0; i < 1000000; i++ ) {
      list.add( random.nextDouble() );
    }
    testList( list );
  }
}
