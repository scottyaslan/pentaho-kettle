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

import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleXMLException;
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
  public static final String PLUGGABLE_VERSION_1 = "pluggable-v1";
  public static final String PREFIX_BASE = "Producer-";

  private String m_sourceFieldName;
  private List<UnivariateStatsValueProducer> valueProducers;

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
    valueProducers = new ArrayList<UnivariateStatsValueProducer>();
  }

  public UnivariateStatsMetaFunction( String sourceFieldName, boolean n, boolean mean, boolean stdDev, boolean min,
      boolean max, boolean median, double arbPercentile, boolean interpolate ) {
    legacyLoad( sourceFieldName, n, mean, stdDev, min, max, median, arbPercentile, interpolate );
  }

  public void legacyLoad( String sourceField, boolean count, boolean mean, boolean stdDev, boolean min, boolean max,
      boolean median, double percentile, boolean interpolate ) {
    m_sourceFieldName = sourceField;
    valueProducers = new ArrayList<UnivariateStatsValueProducer>();

    if ( count ) {
      valueProducers.add( new CountValueProcessor() );
    }
    if ( mean ) {
      valueProducers.add( new MeanValueCalculator() );
    }
    if ( stdDev ) {
      valueProducers.add( new StandardDeviationCalculator() );
    }
    if ( min ) {
      valueProducers.add( new MinValueProcessor() );
    }
    if ( max ) {
      valueProducers.add( new MaxValueProcessor() );
    }
    PercentileValueCalculator medianConfig = null;
    Map<String, Object> medianConfigParameters = new HashMap<String, Object>();
    if ( median ) {
      medianConfig = new PercentileValueCalculator();
      medianConfigParameters.put( PercentileValueCalculator.PERCENTILE_NAME, 0.5 );
      valueProducers.add( medianConfig );
    }
    PercentileValueCalculator percentileConfig = null;
    Map<String, Object> percentileConfigParameters = new HashMap<String, Object>();
    try {
      double m_arbitraryPercentile = percentile;
      if ( m_arbitraryPercentile != -1 ) {
        percentileConfig = new PercentileValueCalculator();
        percentileConfigParameters.put( PercentileValueCalculator.PERCENTILE_NAME, m_arbitraryPercentile );
        valueProducers.add( percentileConfig );
      }
    } catch ( Exception ex ) {
      // noop m_arbitraryPercentile = -1;
    }
    if ( interpolate ) {
      if ( medianConfig != null ) {
        medianConfigParameters.put( PercentileValueCalculator.INTERPOLATE_NAME, true );
      }
      if ( percentileConfig != null ) {
        percentileConfigParameters.put( PercentileValueCalculator.INTERPOLATE_NAME, true );
      }
    }
    for ( UnivariateStatsValueProducer producer : valueProducers ) {
      producer.setOrigin( getSourceFieldName() );
    }
    if ( medianConfig != null ) {
      medianConfig.setParameters( medianConfigParameters );
    }
    if ( percentileConfig != null ) {
      percentileConfig.setParameters( percentileConfigParameters );
    }
  }

  /**
   * Construct from an XML node
   * 
   * @param uniNode
   *          a XML node
   * @throws KettlePluginException
   * @throws KettleXMLException
   */
  public UnivariateStatsMetaFunction( Node uniNode, List<DatabaseMeta> databases, IMetaStore metaStore )
    throws KettleXMLException {
    String temp = XMLHandler.getTagValue( uniNode, "percentile" );
    if ( Const.isEmpty( XMLHandler.getTagValue( uniNode, "version" ) ) ) {
      double m_arbitraryPercentile = -1;
      try {
        m_arbitraryPercentile = Double.parseDouble( temp );
      } catch ( Exception ex ) {
        // noop
      }
      legacyLoad( XMLHandler.getTagValue( uniNode, "source_field_name" ), !XMLHandler.getTagValue( uniNode, "N" )
          .equalsIgnoreCase( "N" ), !XMLHandler.getTagValue( uniNode, "mean" ).equalsIgnoreCase( "N" ), !XMLHandler
          .getTagValue( uniNode, "stdDev" ).equalsIgnoreCase( "N" ), !XMLHandler.getTagValue( uniNode, "min" )
          .equalsIgnoreCase( "N" ), !XMLHandler.getTagValue( uniNode, "max" ).equalsIgnoreCase( "N" ), !XMLHandler
          .getTagValue( uniNode, "median" ).equalsIgnoreCase( "N" ), m_arbitraryPercentile, !XMLHandler.getTagValue(
          uniNode, "interpolate" ).equalsIgnoreCase( "N" ) );
    } else {
      m_sourceFieldName = XMLHandler.getTagValue( uniNode, "source_field_name" );
      List<UnivariateStatsValueProducer> producers = new ArrayList<UnivariateStatsValueProducer>();
      for ( Node node : XMLHandler.getNodes( uniNode, "producer" ) ) {
        String id = XMLHandler.getTagValue( node, "id" );
        UnivariateStatsValueProducer producer;
        try {
          producer = createProducer( id );
        } catch ( KettlePluginException e ) {
          throw new KettleXMLException( e );
        }
        producer.loadXML( node, databases, metaStore );
        producers.add( producer );
      }
      valueProducers = producers;
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
  public UnivariateStatsMetaFunction( Repository rep, IMetaStore metaStore, ObjectId id_step,
      List<DatabaseMeta> databases, int nr ) throws KettleException {
    String m_sourceFieldName = rep.getStepAttributeString( id_step, nr, "source_field_name" );
    if ( Const.isEmpty( rep.getStepAttributeString( id_step, nr, "version" ) ) ) {
      boolean m_n = rep.getStepAttributeBoolean( id_step, nr, "N" );
      boolean m_mean = rep.getStepAttributeBoolean( id_step, nr, "mean" );
      boolean m_stdDev = rep.getStepAttributeBoolean( id_step, nr, "stdDev" );
      boolean m_min = rep.getStepAttributeBoolean( id_step, nr, "min" );
      boolean m_max = rep.getStepAttributeBoolean( id_step, nr, "max" );
      boolean m_median = rep.getStepAttributeBoolean( id_step, nr, "median" );
      String temp = rep.getStepAttributeString( id_step, nr, "percentile" );
      double m_arbitraryPercentile;
      try {
        m_arbitraryPercentile = Double.parseDouble( temp );
      } catch ( Exception ex ) {
        m_arbitraryPercentile = -1;
      }
      boolean m_interpolatePercentile = rep.getStepAttributeBoolean( id_step, nr, "interpolate" );
      legacyLoad( m_sourceFieldName, m_n, m_mean, m_stdDev, m_min, m_max, m_median, m_arbitraryPercentile,
          m_interpolatePercentile );
    } else {
      this.m_sourceFieldName = m_sourceFieldName;
      List<UnivariateStatsValueProducer> producers = new ArrayList<UnivariateStatsValueProducer>();
      int index = 0;
      String prefix = PREFIX_BASE + Integer.toString( index++ );
      String id = rep.getStepAttributeString( id_step, nr, prefix + "-ID" );
      while ( !Const.isEmpty( id ) ) {
        UnivariateStatsValueProducer producer = createProducer( id );
        producer.readRep( rep, metaStore, id_step, databases, nr, prefix );
        producers.add( producer );
        prefix = PREFIX_BASE + Integer.toString( index++ );
        id = rep.getStepAttributeString( id_step, nr, prefix + "-ID" );
      }
      valueProducers = producers;
    }
  }

  /**
   * Check for equality
   * 
   * @param obj
   *          an UnivarateStatsMetaFunction to compare against
   * @return true if this Object and the supplied one are the same
   */
  @Override
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
    StringBuilder sb = new StringBuilder( "<" );
    sb.append( XML_TAG );
    sb.append( ">" );
    sb.append( XMLHandler.addTagValue( "source_field_name", m_sourceFieldName ) );
    sb.append( XMLHandler.addTagValue( "version", PLUGGABLE_VERSION_1 ) );
    for ( UnivariateStatsValueProducer producer : valueProducers ) {
      final String id;
      if ( producer instanceof UnivariateStatsValueProcessor ) {
        id = producer.getClass().getAnnotation( UnivariateValueProcessorPlugin.class ).id();
      } else if ( producer instanceof UnivariateStatsValueCalculator ) {
        id = producer.getClass().getAnnotation( UnivariateValueCalculatorPlugin.class ).id();
      } else {
        continue;
      }
      sb.append( "<producer>" );
      sb.append( XMLHandler.addTagValue( "id", id ) );
      sb.append( producer.getXml() );
      sb.append( "</producer>" );
    }
    sb.append( "</" );
    sb.append( XML_TAG );
    sb.append( ">" );
    return sb.toString();
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
    rep.saveStepAttribute( id_transformation, id_step, nr, "version", PLUGGABLE_VERSION_1 );
    int index = 0;
    for ( UnivariateStatsValueProducer producer : valueProducers ) {
      final String id;
      if ( producer instanceof UnivariateStatsValueProcessor ) {
        id = producer.getClass().getAnnotation( UnivariateValueProcessorPlugin.class ).id();
      } else if ( producer instanceof UnivariateStatsValueCalculator ) {
        id = producer.getClass().getAnnotation( UnivariateValueCalculatorPlugin.class ).id();
      } else {
        continue;
      }
      String prefix = PREFIX_BASE + Integer.toString( index++ );
      rep.saveStepAttribute( id_transformation, id_step, nr, prefix + "-ID", id );
      producer.saveRep( rep, metaStore, id_transformation, id_step, nr, prefix );
    }
  }

  /**
   * Make a copy
   * 
   * @return a copy of this UnivariateStatsMetaFunction.
   */
  @Override
  public Object clone() {
    try {
      String xml = getXML();
      UnivariateStatsMetaFunction retval =
          new UnivariateStatsMetaFunction( XMLHandler.getSubNode( XMLHandler.loadXMLString( xml ), XML_TAG ), null, null );
      return retval;
    } catch ( KettleXMLException e ) {
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
      return percentile / 100.0;
    }

    return -1; // not used
  }

  public List<UnivariateStatsValueProducer> getRequestedValues() {
    return new ArrayList<UnivariateStatsValueProducer>( valueProducers );
  }

  private UnivariateStatsValueProducer createProducer( String id ) throws KettlePluginException {
    PluginInterface producerPlugin =
        PluginRegistry.getInstance().getPlugin( UnivariateValueCalculatorPluginType.class, id );
    if ( producerPlugin == null ) {
      producerPlugin = PluginRegistry.getInstance().getPlugin( UnivariateValueProcessorPluginType.class, id );
    }
    return (UnivariateStatsValueProducer) PluginRegistry.getInstance().loadClass( producerPlugin );
  }

  public void setProducers( List<UnivariateStatsValueProducer> producers ) {
    valueProducers = producers;
  }

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

  @Override
  public String toString() {
    return "UnivariateStatsMetaFunction [m_sourceFieldName=" + m_sourceFieldName + ", valueProducers=" + valueProducers
        + "]";
  }
}
