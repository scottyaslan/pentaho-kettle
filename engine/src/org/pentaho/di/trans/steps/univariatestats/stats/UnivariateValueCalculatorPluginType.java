package org.pentaho.di.trans.steps.univariatestats.stats;

import java.util.ArrayList;

import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.plugins.BasePluginType;
import org.pentaho.di.core.plugins.PluginMainClassType;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueCalculator;
import org.pentaho.di.trans.steps.univariatestats.stats.calculators.MeanValueCalculator;
import org.pentaho.di.trans.steps.univariatestats.stats.calculators.PercentileValueCalculator;
import org.pentaho.di.trans.steps.univariatestats.stats.calculators.StandardDeviationCalculator;
import org.pentaho.di.trans.steps.univariatestats.stats.calculators.TDigestQuantileValueCalculator;

@PluginMainClassType( UnivariateStatsValueCalculator.class )
public class UnivariateValueCalculatorPluginType extends BasePluginType {
  private static final UnivariateValueCalculatorPluginType instance = new UnivariateValueCalculatorPluginType();

  public static UnivariateValueCalculatorPluginType getInstance() {
    return instance;
  }

  private UnivariateValueCalculatorPluginType() {
    super( UnivariateValueCalculatorPlugin.class, "UnivariateCalculator", "UnivariateCalculator" );
    populateFolders( "univariate" );
  }

  @Override
  protected void registerNatives() throws KettlePluginException {
    handlePluginAnnotation( MeanValueCalculator.class, MeanValueCalculator.class
        .getAnnotation( UnivariateValueCalculatorPlugin.class ), new ArrayList<String>(), true, null );
    handlePluginAnnotation( PercentileValueCalculator.class, PercentileValueCalculator.class
        .getAnnotation( UnivariateValueCalculatorPlugin.class ), new ArrayList<String>(), true, null );
    handlePluginAnnotation( StandardDeviationCalculator.class, StandardDeviationCalculator.class
        .getAnnotation( UnivariateValueCalculatorPlugin.class ), new ArrayList<String>(), true, null );
    handlePluginAnnotation( TDigestQuantileValueCalculator.class, TDigestQuantileValueCalculator.class
        .getAnnotation( UnivariateValueCalculatorPlugin.class ), new ArrayList<String>(), true, null );
  }
}
