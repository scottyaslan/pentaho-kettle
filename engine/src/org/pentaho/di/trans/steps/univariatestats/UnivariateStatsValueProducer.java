package org.pentaho.di.trans.steps.univariatestats;

import java.util.Map;

import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.row.ValueMetaInterface;

public interface UnivariateStatsValueProducer {
  public String getName();

  public ValueMetaInterface getOutputValueMeta() throws KettlePluginException;

  public Object getValue();

  public void setOrigin( String origin );

  public void setParameters( Map<String, Object> parameters );
}
