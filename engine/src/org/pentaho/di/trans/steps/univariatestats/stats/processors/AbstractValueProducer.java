package org.pentaho.di.trans.steps.univariatestats.stats.processors;

import java.util.Map;

import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaFactory;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueProducer;

public abstract class AbstractValueProducer implements UnivariateStatsValueProducer {
  private final String name;
  private String origin;
  private final int outputType;
  private ValueMetaInterface outputMeta = null;

  public AbstractValueProducer( String name, int outputType ) {
    this.name = name;
    this.outputType = outputType;
  }

  protected String getValueMetaName() {
    return origin + "(" + name + ")";
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public synchronized ValueMetaInterface getOutputValueMeta() throws KettlePluginException {
    if ( outputMeta == null ) {
      this.outputMeta = ValueMetaFactory.createValueMeta( getValueMetaName(), outputType );
      this.outputMeta.setOrigin( origin );
    }
    return outputMeta;
  }

  @Override
  public void setOrigin( String origin ) {
    this.origin = origin;
  }

  @Override
  public void setParameters( Map<String, Object> parameters ) {
    // noop default
  }
}
