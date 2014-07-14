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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.json.simple.parser.ParseException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.value.ValueMetaPluginType;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.StringObjectId;
import org.pentaho.di.trans.steps.loadsave.MemoryRepository;
import org.pentaho.di.trans.steps.univariatestats.stats.UnivariateValueCalculatorPluginType;
import org.pentaho.di.trans.steps.univariatestats.stats.UnivariateValueProcessorPluginType;
import org.pentaho.di.trans.steps.univariatestats.stats.calculators.MeanValueCalculator;
import org.pentaho.di.trans.steps.univariatestats.stats.calculators.PercentileValueCalculator;
import org.pentaho.di.trans.steps.univariatestats.stats.calculators.StandardDeviationCalculator;
import org.pentaho.di.trans.steps.univariatestats.stats.processors.CountValueProcessor;
import org.pentaho.di.trans.steps.univariatestats.stats.processors.MaxValueProcessor;
import org.pentaho.di.trans.steps.univariatestats.stats.processors.MinValueProcessor;
import org.pentaho.test.util.GetterSetterTester;
import org.pentaho.test.util.ObjectTesterBuilder;

public class UnivariateStatsMetaFunctionTest {

  @BeforeClass
  public static void beforeClass() throws KettlePluginException {
    ValueMetaPluginType.getInstance().searchPlugins();
    UnivariateValueProcessorPluginType.getInstance().searchPlugins();
    UnivariateValueCalculatorPluginType.getInstance().searchPlugins();
  }

  @Test
  public void testValuesConstructor() {
    UnivariateStatsMetaFunction function =
        new UnivariateStatsMetaFunction( null, false, false, false, false, false, false, -1, false );
    assertNull( function.getSourceFieldName() );
    List<UnivariateStatsValueProducer> producers = function.getRequestedValues();
    Set<Class<?>> producerClasses = new HashSet<Class<?>>();
    for ( UnivariateStatsValueProducer producer : producers ) {
      producerClasses.add( producer.getClass() );
    }
    assertFalse( producerClasses.contains( CountValueProcessor.class ) );
    assertFalse( producerClasses.contains( MeanValueCalculator.class ) );
    assertFalse( producerClasses.contains( StandardDeviationCalculator.class ) );
    assertFalse( producerClasses.contains( MinValueProcessor.class ) );
    assertFalse( producerClasses.contains( MaxValueProcessor.class ) );
    assertFalse( producerClasses.contains( PercentileValueCalculator.class ) );

    function = new UnivariateStatsMetaFunction( "test", true, true, true, true, true, true, 0.55, true );
    assertEquals( "test", function.getSourceFieldName() );
    producers = function.getRequestedValues();
    producerClasses = new HashSet<Class<?>>();
    for ( UnivariateStatsValueProducer producer : producers ) {
      producerClasses.add( producer.getClass() );
    }
    assertTrue( producerClasses.contains( CountValueProcessor.class ) );
    assertTrue( producerClasses.contains( MeanValueCalculator.class ) );
    assertTrue( producerClasses.contains( StandardDeviationCalculator.class ) );
    assertTrue( producerClasses.contains( MinValueProcessor.class ) );
    assertTrue( producerClasses.contains( MaxValueProcessor.class ) );
    assertTrue( producerClasses.contains( PercentileValueCalculator.class ) );
    boolean foundMedian = false;
    boolean foundPercentile = false;
    for ( UnivariateStatsValueProducer producer : producers ) {
      if ( producer instanceof PercentileValueCalculator ) {
        PercentileValueCalculator calculator = (PercentileValueCalculator) producer;
        double percentile = calculator.getPercentile();
        if ( percentile == 0.5 ) {
          foundMedian = true;
        } else if ( percentile == 0.55 ) {
          foundPercentile = true;
        }
        assertTrue( calculator.isInterpolate() );
      }
    }
    assertTrue( foundMedian );
    assertTrue( foundPercentile );
  }

  @Test
  public void testNodeConstructor() throws IOException, KettleXMLException {
    String functionXml =
        IOUtils.toString( UnivariateStatsMetaTest.class.getClassLoader().getResourceAsStream(
            "org/pentaho/di/trans/steps/univariatestats/trueValuesUnivariateStatsMetaFunctionNode.xml" ) );
    UnivariateStatsMetaFunction function =
        new UnivariateStatsMetaFunction( XMLHandler.loadXMLString( functionXml ).getFirstChild(), null, null );
    List<UnivariateStatsValueProducer> producers = function.getRequestedValues();
    Set<Class<?>> producerClasses = new HashSet<Class<?>>();
    for ( UnivariateStatsValueProducer producer : producers ) {
      producerClasses.add( producer.getClass() );
    }
    assertEquals( "a", function.getSourceFieldName() );
    assertTrue( producerClasses.contains( CountValueProcessor.class ) );
    assertTrue( producerClasses.contains( MeanValueCalculator.class ) );
    assertTrue( producerClasses.contains( StandardDeviationCalculator.class ) );
    assertTrue( producerClasses.contains( MinValueProcessor.class ) );
    assertTrue( producerClasses.contains( MaxValueProcessor.class ) );
    assertTrue( producerClasses.contains( PercentileValueCalculator.class ) );

    boolean foundMedian = false;
    boolean foundPercentile = false;
    for ( UnivariateStatsValueProducer producer : producers ) {
      if ( producer instanceof PercentileValueCalculator ) {
        PercentileValueCalculator calculator = (PercentileValueCalculator) producer;
        double percentile = calculator.getPercentile();
        if ( percentile == 0.5 ) {
          foundMedian = true;
        } else if ( percentile == 0.55 ) {
          foundPercentile = true;
        }
        assertTrue( calculator.isInterpolate() );
      }
    }
    assertTrue( foundMedian );
    assertTrue( foundPercentile );

    functionXml =
        IOUtils.toString( UnivariateStatsMetaTest.class.getClassLoader().getResourceAsStream(
            "org/pentaho/di/trans/steps/univariatestats/falseValuesUnivariateStatsMetaFunctionNode.xml" ) );
    function = new UnivariateStatsMetaFunction( XMLHandler.loadXMLString( functionXml ).getFirstChild(), null, null );
    assertTrue( Const.isEmpty( function.getSourceFieldName() ) );
    producers = function.getRequestedValues();
    producerClasses = new HashSet<Class<?>>();
    for ( UnivariateStatsValueProducer producer : producers ) {
      producerClasses.add( producer.getClass() );
    }
    assertFalse( producerClasses.contains( CountValueProcessor.class ) );
    assertFalse( producerClasses.contains( MeanValueCalculator.class ) );
    assertFalse( producerClasses.contains( StandardDeviationCalculator.class ) );
    assertFalse( producerClasses.contains( MinValueProcessor.class ) );
    assertFalse( producerClasses.contains( MaxValueProcessor.class ) );
    assertFalse( producerClasses.contains( PercentileValueCalculator.class ) );
  }

  @Test
  public void testRepoConstructor() throws KettleException, IOException, ParseException {
    String jsString =
        IOUtils.toString( UnivariateStatsMetaTest.class.getClassLoader().getResourceAsStream(
            "org/pentaho/di/trans/steps/univariatestats/trueValuesUnivariateStatsMetaFunctionNode.json" ) );
    Repository repo = new MemoryRepository( jsString );
    UnivariateStatsMetaFunction function =
        new UnivariateStatsMetaFunction( repo, null, new StringObjectId( "test" ), null, 0 );
    assertEquals( "test", function.getSourceFieldName() );
    List<UnivariateStatsValueProducer> producers = function.getRequestedValues();
    Set<Class<?>> producerClasses = new HashSet<Class<?>>();
    for ( UnivariateStatsValueProducer producer : producers ) {
      producerClasses.add( producer.getClass() );
    }
    assertEquals( "test", function.getSourceFieldName() );
    assertTrue( producerClasses.contains( CountValueProcessor.class ) );
    assertTrue( producerClasses.contains( MeanValueCalculator.class ) );
    assertTrue( producerClasses.contains( StandardDeviationCalculator.class ) );
    assertTrue( producerClasses.contains( MinValueProcessor.class ) );
    assertTrue( producerClasses.contains( MaxValueProcessor.class ) );
    assertTrue( producerClasses.contains( PercentileValueCalculator.class ) );

    boolean foundMedian = false;
    boolean foundPercentile = false;
    for ( UnivariateStatsValueProducer producer : producers ) {
      if ( producer instanceof PercentileValueCalculator ) {
        PercentileValueCalculator calculator = (PercentileValueCalculator) producer;
        double percentile = calculator.getPercentile();
        if ( percentile == 0.5 ) {
          foundMedian = true;
        } else if ( percentile == 0.55 ) {
          foundPercentile = true;
        }
        assertTrue( calculator.isInterpolate() );
      }
    }
    assertTrue( foundMedian );
    assertTrue( foundPercentile );

    jsString =
        IOUtils.toString( UnivariateStatsMetaTest.class.getClassLoader().getResourceAsStream(
            "org/pentaho/di/trans/steps/univariatestats/falseValuesUnivariateStatsMetaFunctionNode.json" ) );
    repo = new MemoryRepository( jsString );
    function = new UnivariateStatsMetaFunction( repo, null, new StringObjectId( "test" ), null, 0 );
    assertTrue( Const.isEmpty( function.getSourceFieldName() ) );
    producers = function.getRequestedValues();
    producerClasses = new HashSet<Class<?>>();
    for ( UnivariateStatsValueProducer producer : producers ) {
      producerClasses.add( producer.getClass() );
    }
    assertFalse( producerClasses.contains( CountValueProcessor.class ) );
    assertFalse( producerClasses.contains( MeanValueCalculator.class ) );
    assertFalse( producerClasses.contains( StandardDeviationCalculator.class ) );
    assertFalse( producerClasses.contains( MinValueProcessor.class ) );
    assertFalse( producerClasses.contains( MaxValueProcessor.class ) );
    assertFalse( producerClasses.contains( PercentileValueCalculator.class ) );
  }

  @Test
  public void testEquals() throws IOException, KettleXMLException {
    String functionXml =
        IOUtils.toString( UnivariateStatsMetaTest.class.getClassLoader().getResourceAsStream(
            "org/pentaho/di/trans/steps/univariatestats/trueValuesUnivariateStatsMetaFunctionNode.xml" ) );
    UnivariateStatsMetaFunction function =
        new UnivariateStatsMetaFunction( XMLHandler.loadXMLString( functionXml ).getFirstChild(), null, null );
    UnivariateStatsMetaFunction function2 =
        new UnivariateStatsMetaFunction( XMLHandler.loadXMLString( functionXml ).getFirstChild(), null, null );
    assertEquals( function, function2 );

    functionXml =
        IOUtils.toString( UnivariateStatsMetaTest.class.getClassLoader().getResourceAsStream(
            "org/pentaho/di/trans/steps/univariatestats/falseValuesUnivariateStatsMetaFunctionNode.xml" ) );
    function = new UnivariateStatsMetaFunction( XMLHandler.loadXMLString( functionXml ).getFirstChild(), null, null );
    assertFalse( function.equals( function2 ) );
    function2 = new UnivariateStatsMetaFunction( XMLHandler.loadXMLString( functionXml ).getFirstChild(), null, null );
    assertEquals( function, function2 );
  }

  @Test
  public void testClone() {
    UnivariateStatsMetaFunction function =
        new UnivariateStatsMetaFunction( null, false, false, false, false, false, false, 0, false );
    assertEquals( UnivariateStatsMetaFunction.class, function.clone().getClass() );
  }

  @Test
  public void testGettersAndSetters() {
    GetterSetterTester<UnivariateStatsMetaFunction> getterSetterTest =
        new GetterSetterTester<UnivariateStatsMetaFunction>( UnivariateStatsMetaFunction.class );
    getterSetterTest.addObjectTester( "sourceFieldName", new ObjectTesterBuilder<String>().addObject( null ).addObject(
        UUID.randomUUID().toString() ).build() );
    getterSetterTest.test( new UnivariateStatsMetaFunction( null, false, false, false, false, false, false, 0, false ) );
  }
}
