/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2014 by Pentaho : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package org.pentaho.di.trans.steps.univariatestats;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.row.value.ValueMetaNumber;
import org.pentaho.di.core.row.value.ValueMetaPluginType;
import org.pentaho.di.trans.steps.univariatestats.stats.UnivariateValueCalculatorPluginType;
import org.pentaho.di.trans.steps.univariatestats.stats.UnivariateValueProcessorPluginType;
import org.pentaho.di.trans.steps.univariatestats.stats.calculators.MeanValueCalculator;
import org.pentaho.di.trans.steps.univariatestats.stats.calculators.StandardDeviationCalculator;

public class FieldIndexTest {
  @BeforeClass
  public static void beforeClass() throws KettlePluginException {
    ValueMetaPluginType.getInstance().searchPlugins();
    UnivariateValueProcessorPluginType.getInstance().searchPlugins();
    UnivariateValueCalculatorPluginType.getInstance().searchPlugins();
  }

  @Test
  public void testCalculateDerived0Count() throws KettleStepException, KettleValueException, KettlePluginException {
    UnivariateStatsMetaFunction function = new UnivariateStatsMetaFunction( "test" );
    function.setProducers( Arrays.<UnivariateStatsValueProducer>asList( new MeanValueCalculator(), new StandardDeviationCalculator() ) );
    FieldIndex fieldIndex = new FieldIndex( function, 1 );
    // Should be only mean and stdev
    Object[] output = fieldIndex.generateOutputValues();
    assertEquals( 2, output.length );
    assertEquals( Double.NaN, output[0] );
    assertEquals( Double.NaN, output[1] );
  }

  @Test
  public void testCalculateDerived1Count() throws KettleException {
    UnivariateStatsMetaFunction function = new UnivariateStatsMetaFunction( "test" );
    function.setProducers( Arrays.<UnivariateStatsValueProducer>asList( new MeanValueCalculator(), new StandardDeviationCalculator() ) );
    FieldIndex fieldIndex = new FieldIndex( function, 1 );
    fieldIndex.processEntry( new ValueMetaNumber(), 250.0 );
    // Should be only mean and stdev
    Object[] output = fieldIndex.generateOutputValues();
    assertEquals( 2, output.length );
    assertEquals( 250.0, output[0] );
    assertEquals( Double.POSITIVE_INFINITY, output[1] );
  }

  @Test
  public void testCalculateDerived3CountPositiveStdDev() throws KettleException {
    UnivariateStatsMetaFunction function = new UnivariateStatsMetaFunction( "test" );
    function.setProducers( Arrays.<UnivariateStatsValueProducer>asList( new MeanValueCalculator(), new StandardDeviationCalculator() ) );
    FieldIndex fieldIndex = new FieldIndex( function, 1 );
    fieldIndex.processEntry( new ValueMetaNumber(), 250.0 );
    fieldIndex.processEntry( new ValueMetaNumber(), 120.0 );
    fieldIndex.processEntry( new ValueMetaNumber(), 280.0 );
    double sumOfSquares = 250.0 * 250.0 + 120.0 * 120.0 + 280.0 * 280.0;
    double sum = 250.0 + 120.0 + 280.0;
    // Should be only mean and stdev
    Object[] output = fieldIndex.generateOutputValues();
    assertEquals( 2, output.length );
    assertEquals( ( 250.0 + 120.0 + 280.0 ) / 3.0, output[0] );
    assertEquals( Math.sqrt( ( sumOfSquares - ( sum * sum ) / 3.0 ) / ( 3.0 - 1 ) ), output[1] );
  }

  @Test
  public void testGenerateOutputValuesNoCacheNoCalc() throws KettleStepException, KettleValueException,
    KettlePluginException {
    FieldIndex fieldIndex =
        new FieldIndex( new UnivariateStatsMetaFunction( "test", false, false, false, false, false, false, -1, false ),
            1 );
    Object[] outputValues = fieldIndex.generateOutputValues();
    assertEquals( 0, outputValues.length );
  }
}
