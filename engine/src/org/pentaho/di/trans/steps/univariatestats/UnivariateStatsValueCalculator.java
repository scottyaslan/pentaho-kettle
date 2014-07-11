package org.pentaho.di.trans.steps.univariatestats;

import java.util.Map;

import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleValueException;

public interface UnivariateStatsValueCalculator extends UnivariateStatsValueProducer {
  public void process( Map<String, UnivariateStatsValueProcessor> processors ) throws KettleStepException, KettleValueException, KettlePluginException;
}
