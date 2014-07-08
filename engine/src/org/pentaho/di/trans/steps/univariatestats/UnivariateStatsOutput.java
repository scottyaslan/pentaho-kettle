package org.pentaho.di.trans.steps.univariatestats;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pentaho.di.core.row.ValueMetaInterface;

public class UnivariateStatsOutput extends UnivariteStatsOutputMetadata {
  private final Map<String, Object> outputValues;

  public UnivariateStatsOutput( String sourceFieldName, List<ValueMetaInterface> valueMetaInterfaces,
      Map<String, Object> outputValues ) {
    super( sourceFieldName, valueMetaInterfaces );
    this.outputValues = Collections.unmodifiableMap( new HashMap<String, Object>( outputValues ) );
  }

  public Map<String, Object> getOutputValues() {
    return outputValues;
  }
}
