package org.pentaho.di.trans.steps.univariatestats.processors;

import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaFactory;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueProducer;

public abstract class AbstractValueProcessor implements UnivariateStatsValueProducer {
  private final String name;
  private final String origin;
  private final int outputType;
  private ValueMetaInterface outputMeta = null;

  public AbstractValueProcessor( String name, String origin, int outputType ) {
    this.name = name;
    this.origin = origin;
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
}
