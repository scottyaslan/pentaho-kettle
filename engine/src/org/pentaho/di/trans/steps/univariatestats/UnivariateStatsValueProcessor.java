package org.pentaho.di.trans.steps.univariatestats;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.ValueMetaInterface;

public interface UnivariateStatsValueProcessor extends UnivariateStatsValueProducer {
  public void process( ValueMetaInterface inputMeta, Object input ) throws KettleException;
}
