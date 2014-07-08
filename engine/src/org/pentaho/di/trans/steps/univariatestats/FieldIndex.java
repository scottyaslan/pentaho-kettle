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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.row.ValueMetaInterface;

/**
 * Class used to hold operating field index, intermediate data and final results for a stats calculation.
 * 
 * Has functions to compute the mean, standard deviation and arbitrary percentiles. Percentiles can be computed using
 * interpolation or a simple method. See <a href="http://www.itl.nist.gov/div898/handbook/prc/section2/prc252.htm"> The
 * Engineering Statistics Handbook</a> for details.
 */
public class FieldIndex {
  public final int m_columnIndex;
  private final List<UnivariateStatsValueProducer> valueProducers;
  private final List<UnivariateStatsValueProcessor> valueProcessors;

  public FieldIndex( UnivariateStatsMetaFunction univariateStatsMetaFunction, int columnIndex )
    throws KettleStepException {
    valueProducers = univariateStatsMetaFunction.getRequestedValues();
    valueProcessors = univariateStatsMetaFunction.getProcessors( valueProducers );
    this.m_columnIndex = columnIndex;
  }

  public void processEntry( ValueMetaInterface inputMeta, Object input ) throws KettleException {
    for ( UnivariateStatsValueProcessor valueProcessor : valueProcessors ) {
      valueProcessor.process( inputMeta, input );
    }
  }

  public int getNumFields() {
    return valueProcessors.size();
  }

  /**
   * Constructs an array of Objects containing the requested statistics for one univariate stats meta function using
   * this <code>FieldIndex</code>.
   * 
   * @param usmf
   *          the<code>UnivariateStatsMetaFunction</code> to compute stats for. This contains the input field selected
   *          by the user along with which stats to compute for it.
   * @return an array of computed statistics
   * @throws KettleStepException
   * @throws KettlePluginException
   * @throws KettleValueException
   */
  public Object[] generateOutputValues() throws KettleStepException, KettleValueException, KettlePluginException {
    Set<UnivariateStatsValueProducer> producers = new HashSet<UnivariateStatsValueProducer>( valueProcessors );
    producers.addAll( valueProcessors );
    Object[] result = new Object[valueProducers.size()];
    int index = 0;
    for ( UnivariateStatsValueProducer producer : valueProducers ) {
      if ( producer instanceof UnivariateStatsValueCalculator ) {
        ( (UnivariateStatsValueCalculator) producer ).process( producers );
      }
      result[index++] = producer.getValue();
    }
    return result;
  }
}
