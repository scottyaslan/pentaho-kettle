package org.pentaho.di.trans.steps.univariatestats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.pentaho.di.core.row.ValueMetaInterface;

public class UnivariteStatsOutputMetadata {
  private final String sourceFieldName;

  private final List<ValueMetaInterface> valueMetaInterfaces;

  public UnivariteStatsOutputMetadata( String sourceFieldName, List<ValueMetaInterface> valueMetaInterfaces ) {
    this.sourceFieldName = sourceFieldName;
    this.valueMetaInterfaces = Collections.unmodifiableList( new ArrayList<ValueMetaInterface>( valueMetaInterfaces ) );
  }

  public String getSourceFieldName() {
    return sourceFieldName;
  }

  public List<ValueMetaInterface> getValueMetaInterfaces() {
    return valueMetaInterfaces;
  }
}
