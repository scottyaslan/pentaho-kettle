package org.pentaho.di.trans.steps.univariatestats;

import java.util.Set;

import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleValueException;

public interface UnivariateStatsValueCalculator extends UnivariateStatsValueProducer {
  public Set<Class<? extends UnivariateStatsValueProducer>> getRequiredProcessors();

  public void process( Set<UnivariateStatsValueProducer> producers ) throws KettleStepException, KettleValueException, KettlePluginException;
}
