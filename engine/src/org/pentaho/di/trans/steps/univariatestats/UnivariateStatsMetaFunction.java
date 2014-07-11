/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2013 by Pentaho : http://www.pentaho.com
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.steps.univariatestats.stats.UnivariateValueCalculatorPlugin;
import org.pentaho.di.trans.steps.univariatestats.stats.UnivariateValueCalculatorPluginType;
import org.pentaho.di.trans.steps.univariatestats.stats.UnivariateValueProcessorPlugin;
import org.pentaho.di.trans.steps.univariatestats.stats.UnivariateValueProcessorPluginType;
import org.pentaho.di.trans.steps.univariatestats.stats.calculators.MeanValueCalculator;
import org.pentaho.di.trans.steps.univariatestats.stats.calculators.PercentileValueCalculator;
import org.pentaho.di.trans.steps.univariatestats.stats.calculators.StandardDeviationCalculator;
import org.pentaho.di.trans.steps.univariatestats.stats.processors.CountValueProcessor;
import org.pentaho.di.trans.steps.univariatestats.stats.processors.MaxValueProcessor;
import org.pentaho.di.trans.steps.univariatestats.stats.processors.MinValueProcessor;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;

/**
 * Holds meta information about one univariate stats calculation: source field name and what derived values are to be
 * computed
 * 
 * @author Mark Hall (mhall{[at]}pentaho.org
 * @version 1.0
 */
public class UnivariateStatsMetaFunction implements Cloneable {

  public static final String XML_TAG = "univariate_stats";

  private String m_sourceFieldName;
  private List<UnivariateStatsValueProducer> valueProducers = new ArrayList<UnivariateStatsValueProducer>();

  /**
   * Creates a new <code>UnivariateStatsMetaFunction</code>
   * 
   * @param sourceFieldName
   *          the name of the input field to compute stats for
   * @param n
   *          output N
   * @param mean
   *          compute and output the mean
   * @param stdDev
   *          compute and output the standard deviation
   * @param min
   *          output the minumum value
   * @param max
   *          output the maximum value
   * @param median
   *          compute and output the median (requires data caching and sorting)
   * @param arbPercentile
   *          compute and output a percentile (0 <= arbPercentile <= 1)
   * @param interpolate
   *          true if interpolation is to be used for percentiles (rather than a simple method). See <a
   *          href="http://www.itl.nist.gov/div898/handbook/prc/section2/prc252.htm"> The Engineering Statistics
   *          Handbook</a> for details.
   */
  public UnivariateStatsMetaFunction( String sourceFieldName ) {
    m_sourceFieldName = sourceFieldName;
  }

  public void legacyLoad( String sourceField, boolean count, boolean mean, boolean stdDev, boolean min, boolean max, boolean median,
      double percentile, boolean interpolate ) {
    List<UnivariateStatsValueConfig> configs = new ArrayList<UnivariateStatsValueConfig>();
    m_sourceFieldName = sourceField;

    String temp = XMLHandler.getTagValue( uniNode, "N" );
    if ( !temp.equalsIgnoreCase( "N" ) ) {
      configs.add( new UnivariateStatsValueConfig( CountValueProcessor.ID ) );
    }
    temp = XMLHandler.getTagValue( uniNode, "mean" );
    if ( !temp.equalsIgnoreCase( "N" ) ) {
      configs.add( new UnivariateStatsValueConfig( MeanValueCalculator.ID ) );
    }

    temp = XMLHandler.getTagValue( uniNode, "stdDev" );
    if ( !temp.equalsIgnoreCase( "N" ) ) {
      configs.add( new UnivariateStatsValueConfig( StandardDeviationCalculator.ID ) );
    }

    temp = XMLHandler.getTagValue( uniNode, "min" );
    if ( !temp.equalsIgnoreCase( "N" ) ) {
      configs.add( new UnivariateStatsValueConfig( MinValueProcessor.ID ) );
    }

    temp = XMLHandler.getTagValue( uniNode, "max" );
    if ( !temp.equalsIgnoreCase( "N" ) ) {
      configs.add( new UnivariateStatsValueConfig( MaxValueProcessor.ID ) );
    }

    temp = XMLHandler.getTagValue( uniNode, "median" );
    UnivariateStatsValueConfig medianConfig = null;
    if ( !temp.equalsIgnoreCase( "N" ) ) {
      medianConfig = new UnivariateStatsValueConfig( PercentileValueCalculator.ID );
      medianConfig.setParameter( PercentileValueCalculator.PERCENTILE_NAME, 0.5 );
      configs.add( medianConfig );
    }

    temp = XMLHandler.getTagValue( uniNode, "percentile" );
    UnivariateStatsValueConfig percentileConfig = null;
    try {
      double m_arbitraryPercentile = Double.parseDouble( temp );
      if ( m_arbitraryPercentile != -1 ) {
        percentileConfig = new UnivariateStatsValueConfig( PercentileValueCalculator.ID );
        percentileConfig.setParameter( PercentileValueCalculator.PERCENTILE_NAME, m_arbitraryPercentile );
      }
    } catch ( Exception ex ) {
      // noop m_arbitraryPercentile = -1;
    }

    temp = XMLHandler.getTagValue( uniNode, "interpolate" );
    if ( !temp.equalsIgnoreCase( "N" ) ) {
      if ( medianConfig != null ) {
        medianConfig.setParameter( PercentileValueCalculator.INTERPOLATE_NAME, true );
      }
      if ( percentileConfig != null ) {
        percentileConfig.setParameter( PercentileValueCalculator.INTERPOLATE_NAME, true );
      }
    }

    try {
      valueProducers = createUnivariateStatsValueProducerList( configs );
    } catch ( KettlePluginException e ) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Construct from an XML node
   * 
   * @param uniNode
   *          a XML node
   */
  public UnivariateStatsMetaFunction( Node uniNode ) {
    String temp;
    List<UnivariateStatsValueConfig> configs = new ArrayList<UnivariateStatsValueConfig>();
    m_sourceFieldName = XMLHandler.getTagValue( uniNode, "source_field_name" );

    temp = XMLHandler.getTagValue( uniNode, "N" );
    if ( !temp.equalsIgnoreCase( "N" ) ) {
      configs.add( new UnivariateStatsValueConfig( CountValueProcessor.ID ) );
    }
    temp = XMLHandler.getTagValue( uniNode, "mean" );
    if ( !temp.equalsIgnoreCase( "N" ) ) {
      configs.add( new UnivariateStatsValueConfig( MeanValueCalculator.ID ) );
    }

    temp = XMLHandler.getTagValue( uniNode, "stdDev" );
    if ( !temp.equalsIgnoreCase( "N" ) ) {
      configs.add( new UnivariateStatsValueConfig( StandardDeviationCalculator.ID ) );
    }

    temp = XMLHandler.getTagValue( uniNode, "min" );
    if ( !temp.equalsIgnoreCase( "N" ) ) {
      configs.add( new UnivariateStatsValueConfig( MinValueProcessor.ID ) );
    }

    temp = XMLHandler.getTagValue( uniNode, "max" );
    if ( !temp.equalsIgnoreCase( "N" ) ) {
      configs.add( new UnivariateStatsValueConfig( MaxValueProcessor.ID ) );
    }

    temp = XMLHandler.getTagValue( uniNode, "median" );
    UnivariateStatsValueConfig medianConfig = null;
    if ( !temp.equalsIgnoreCase( "N" ) ) {
      medianConfig = new UnivariateStatsValueConfig( PercentileValueCalculator.ID );
      medianConfig.setParameter( PercentileValueCalculator.PERCENTILE_NAME, 0.5 );
      configs.add( medianConfig );
    }

    temp = XMLHandler.getTagValue( uniNode, "percentile" );
    UnivariateStatsValueConfig percentileConfig = null;
    try {
      double m_arbitraryPercentile = Double.parseDouble( temp );
      if ( m_arbitraryPercentile != -1 ) {
        percentileConfig = new UnivariateStatsValueConfig( PercentileValueCalculator.ID );
        percentileConfig.setParameter( PercentileValueCalculator.PERCENTILE_NAME, m_arbitraryPercentile );
      }
    } catch ( Exception ex ) {
      // noop m_arbitraryPercentile = -1;
    }

    temp = XMLHandler.getTagValue( uniNode, "interpolate" );
    if ( !temp.equalsIgnoreCase( "N" ) ) {
      if ( medianConfig != null ) {
        medianConfig.setParameter( PercentileValueCalculator.INTERPOLATE_NAME, true );
      }
      if ( percentileConfig != null ) {
        percentileConfig.setParameter( PercentileValueCalculator.INTERPOLATE_NAME, true );
      }
    }

    try {
      valueProducers = createUnivariateStatsValueProducerList( configs );
    } catch ( KettlePluginException e ) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Construct using data stored in repository
   * 
   * @param rep
   *          the repository
   * @param id_step
   *          the id of the step
   * @param nr
   *          the step number
   * @exception KettleException
   *              if an error occurs
   */
  public UnivariateStatsMetaFunction( Repository rep, ObjectId id_step, int nr ) throws KettleException {
    m_sourceFieldName = rep.getStepAttributeString( id_step, nr, "source_field_name" );
    m_n = rep.getStepAttributeBoolean( id_step, nr, "N" );
    m_mean = rep.getStepAttributeBoolean( id_step, nr, "mean" );
    m_stdDev = rep.getStepAttributeBoolean( id_step, nr, "stdDev" );
    m_min = rep.getStepAttributeBoolean( id_step, nr, "min" );
    m_max = rep.getStepAttributeBoolean( id_step, nr, "max" );
    m_median = rep.getStepAttributeBoolean( id_step, nr, "median" );
    String temp = rep.getStepAttributeString( id_step, nr, "percentile" );
    try {
      m_arbitraryPercentile = Double.parseDouble( temp );
    } catch ( Exception ex ) {
      m_arbitraryPercentile = -1;
    }
    m_interpolatePercentile = rep.getStepAttributeBoolean( id_step, nr, "interpolate" );
  }

  /**
   * Check for equality
   * 
   * @param obj
   *          an UnivarateStatsMetaFunction to compare against
   * @return true if this Object and the supplied one are the same
   */
  public boolean equals( Object obj ) {
    if ( ( obj != null ) && ( obj.getClass().equals( this.getClass() ) ) ) {
      UnivariateStatsMetaFunction mf = (UnivariateStatsMetaFunction) obj;

      return ( getXML().equals( mf.getXML() ) );
    }

    return false;
  }

  /**
   * Return a String containing XML describing this UnivariateStatsMetaFunction
   * 
   * @return an XML description of this UnivarateStatsMetaFunction
   */
  public String getXML() {
    String xml = ( "<" + XML_TAG + ">" );

    xml += XMLHandler.addTagValue( "source_field_name", m_sourceFieldName );
    xml += XMLHandler.addTagValue( "N", m_n );
    xml += XMLHandler.addTagValue( "mean", m_mean );
    xml += XMLHandler.addTagValue( "stdDev", m_stdDev );
    xml += XMLHandler.addTagValue( "min", m_min );
    xml += XMLHandler.addTagValue( "max", m_max );
    xml += XMLHandler.addTagValue( "median", m_median );
    xml += XMLHandler.addTagValue( "percentile", "" + m_arbitraryPercentile );
    xml += XMLHandler.addTagValue( "interpolate", m_interpolatePercentile );

    xml += ( "</" + XML_TAG + ">" );

    return xml;
  }

  /**
   * Save this UnivariateStatsMetaFunction to a repository
   * 
   * @param rep
   *          the repository to save to
   * @param id_transformation
   *          the transformation id
   * @param id_step
   *          the step id
   * @param nr
   *          the step number
   * @exception KettleException
   *              if an error occurs
   */
  public void saveRep( Repository rep, IMetaStore metaStore, ObjectId id_transformation, ObjectId id_step, int nr )
    throws KettleException {

    rep.saveStepAttribute( id_transformation, id_step, nr, "source_field_name", m_sourceFieldName );
    rep.saveStepAttribute( id_transformation, id_step, nr, "N", m_n );
    rep.saveStepAttribute( id_transformation, id_step, nr, "mean", m_mean );
    rep.saveStepAttribute( id_transformation, id_step, nr, "stdDev", m_stdDev );
    rep.saveStepAttribute( id_transformation, id_step, nr, "min", m_min );
    rep.saveStepAttribute( id_transformation, id_step, nr, "max", m_max );
    rep.saveStepAttribute( id_transformation, id_step, nr, "median", m_median );
    rep.saveStepAttribute( id_transformation, id_step, nr, "percentile", " " + m_arbitraryPercentile );
    rep.saveStepAttribute( id_transformation, id_step, nr, "interpolate", m_interpolatePercentile );
  }

  /**
   * Make a copy
   * 
   * @return a copy of this UnivariateStatsMetaFunction.
   */
  public Object clone() {
    try {
      UnivariateStatsMetaFunction retval = (UnivariateStatsMetaFunction) super.clone();

      return retval;
    } catch ( CloneNotSupportedException e ) {
      return null;
    }
  }

  /**
   * Set the name of the input field used by this UnivariateStatsMetaFunction.
   * 
   * @param sn
   *          the name of the source field to use
   */
  public void setSourceFieldName( String sn ) {
    m_sourceFieldName = sn;
  }

  /**
   * Return the name of the input field used by this UnivariateStatsMetaFunction
   * 
   * @return the name of the input field used
   */
  public String getSourceFieldName() {
    return m_sourceFieldName;
  }

  /**
   * Set whether to calculate N for this input field
   * 
   * @param n
   *          true if N is to be calculated
   */
  public void setCalcN( boolean n ) {
    m_n = n;
  }

  /**
   * Get whether N is to be calculated for this input field
   * 
   * @return true if N is to be calculated
   */
  public boolean getCalcN() {
    return m_n;
  }

  /**
   * Set whether to calculate the mean for this input field
   * 
   * @param b
   *          true if the mean is to be calculated
   */
  public void setCalcMean( boolean b ) {
    m_mean = b;
  }

  /**
   * Get whether the mean is to be calculated for this input field
   * 
   * @return true if the mean is to be calculated
   */
  public boolean getCalcMean() {
    return m_mean;
  }

  /**
   * Set whether the standard deviation is to be calculated for this input value
   * 
   * @param b
   *          true if the standard deviation is to be calculated
   */
  public void setCalcStdDev( boolean b ) {
    m_stdDev = b;
  }

  /**
   * Get whether the standard deviation is to be calculated for this input value
   * 
   * @return true if the standard deviation is to be calculated
   */
  public boolean getCalcStdDev() {
    return m_stdDev;
  }

  /**
   * Set whether the minimum is to be calculated for this input value
   * 
   * @param b
   *          true if the minimum is to be calculated
   */
  public void setCalcMin( boolean b ) {
    m_min = b;
  }

  /**
   * Get whether the minimum is to be calculated for this input value
   * 
   * @return true if the minimum is to be calculated
   */
  public boolean getCalcMin() {
    return m_min;
  }

  /**
   * Set whether the maximum is to be calculated for this input value
   * 
   * @param b
   *          true if the maximum is to be calculated
   */
  public void setCalcMax( boolean b ) {
    m_max = b;
  }

  /**
   * Get whether the maximum is to be calculated for this input value
   * 
   * @return true if the maximum is to be calculated
   */
  public boolean getCalcMax() {
    return m_max;
  }

  /**
   * Set whether the median is to be calculated for this input value
   * 
   * @param b
   *          true if the median is to be calculated
   */
  public void setCalcMedian( boolean b ) {
    m_median = b;
  }

  /**
   * Get whether the median is to be calculated for this input value
   * 
   * @return true if the median is to be calculated
   */
  public boolean getCalcMedian() {
    return m_median;
  }

  /**
   * Get whether interpolation is to be used in the computation of percentiles
   * 
   * @return true if interpolation is to be used
   */
  public boolean getInterpolatePercentile() {
    return m_interpolatePercentile;
  }

  /**
   * Set whether interpolation is to be used in the computation of percentiles
   * 
   * @param i
   *          true is interpolation is to be used
   */
  public void setInterpolatePercentile( boolean i ) {
    m_interpolatePercentile = i;
  }

  /**
   * Gets whether an arbitrary percentile is to be calculated for this input field
   * 
   * @return true if a percentile is to be computed
   */
  public double getCalcPercentile() {
    return m_arbitraryPercentile;
  }

  /**
   * Sets whether an arbitrary percentile is to be calculated for this input field
   * 
   * @param percentile
   *          the percentile to compute (0 <= percentile <= 100)
   */
  public double calcCalcPercentile( double percentile ) {
    if ( percentile < 0 ) {
      return -1; // not used
    }

    if ( percentile >= 0 && percentile <= 100 ) {
      m_arbitraryPercentile = percentile / 100.0;
      return percentile / 100.0;
    }

    return -1; // not used
  }

  public List<UnivariateStatsValueProducer> getRequestedValues() {
    return new ArrayList<UnivariateStatsValueProducer>( valueProducers );
  }

  private List<UnivariateStatsValueProducer> createUnivariateStatsValueProducerList(
      List<UnivariateStatsValueConfig> configs ) throws KettlePluginException {
    List<UnivariateStatsValueProducer> result = new ArrayList<UnivariateStatsValueProducer>( configs.size() );
    for ( UnivariateStatsValueConfig config : configs ) {
      PluginInterface producerPlugin =
          PluginRegistry.getInstance().getPlugin( UnivariateValueCalculatorPluginType.class, config.getId() );
      if ( producerPlugin == null ) {
        producerPlugin =
            PluginRegistry.getInstance().getPlugin( UnivariateValueProcessorPluginType.class, config.getId() );
      }
      UnivariateStatsValueProducer producer =
          (UnivariateStatsValueProducer) PluginRegistry.getInstance().loadClass( producerPlugin );
      producer.setParameters( config.getParameters() );
    }
    return result;
  }

  public void addConfig( UnivariateStatsValueConfig config ) throws KettlePluginException {
    PluginInterface producerPlugin =
        PluginRegistry.getInstance().getPlugin( UnivariateValueCalculatorPluginType.class, config.getId() );
    if ( producerPlugin == null ) {
      producerPlugin =
          PluginRegistry.getInstance().getPlugin( UnivariateValueProcessorPluginType.class, config.getId() );
    }
    UnivariateStatsValueProducer producer =
        (UnivariateStatsValueProducer) PluginRegistry.getInstance().loadClass( producerPlugin );
    producer.setParameters( config.getParameters() );
    valueProducers.add( producer );
  }

  @SuppressWarnings( "unchecked" )
  public Map<String, UnivariateStatsValueProcessor> getProcessors( List<UnivariateStatsValueProducer> producers )
    throws KettleStepException {
    Map<String, UnivariateStatsValueProcessor> result = new HashMap<String, UnivariateStatsValueProcessor>();
    Set<String> requiredProcessors = new HashSet<String>();
    for ( UnivariateStatsValueProducer producer : producers ) {
      if ( producer instanceof UnivariateStatsValueProcessor ) {
        UnivariateValueProcessorPlugin processorAnnotation =
            producer.getClass().getAnnotation( UnivariateValueProcessorPlugin.class );
        result.put( processorAnnotation.id(), (UnivariateStatsValueProcessor) producer );
      } else if ( producer instanceof UnivariateStatsValueCalculator ) {
        UnivariateValueCalculatorPlugin calculatorAnnotation =
            producer.getClass().getAnnotation( UnivariateValueCalculatorPlugin.class );
        for ( String requiredId : calculatorAnnotation.requiredProcessors() ) {
          requiredProcessors.add( requiredId );
        }
      }
    }
    requiredProcessors.removeAll( result.keySet() );
    for ( String requiredId : requiredProcessors ) {
      PluginInterface processorPlugin =
          PluginRegistry.getInstance().getPlugin( UnivariateValueProcessorPluginType.class, requiredId );
      UnivariateStatsValueProcessor processor = null;
      try {
        processor = (UnivariateStatsValueProcessor) PluginRegistry.getInstance().loadClass( processorPlugin );
      } catch ( KettlePluginException e ) {
        throw new KettleStepException( e );
      }
      processor.setOrigin( getSourceFieldName() );
      result.put( requiredId, processor );
    }
    return result;
  }
}
