package org.pentaho.di.trans.steps.univariatestats.stats;

import java.util.ArrayList;

import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.plugins.BasePluginType;
import org.pentaho.di.core.plugins.PluginMainClassType;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueProcessor;
import org.pentaho.di.trans.steps.univariatestats.stats.processors.CacheingValueProcessor;
import org.pentaho.di.trans.steps.univariatestats.stats.processors.CountValueProcessor;
import org.pentaho.di.trans.steps.univariatestats.stats.processors.MaxValueProcessor;
import org.pentaho.di.trans.steps.univariatestats.stats.processors.MinValueProcessor;
import org.pentaho.di.trans.steps.univariatestats.stats.processors.SumOfSquaresValueProcessor;
import org.pentaho.di.trans.steps.univariatestats.stats.processors.SumValueProcessor;
import org.pentaho.di.trans.steps.univariatestats.stats.processors.TDigestValueProcessor;

@PluginMainClassType( UnivariateStatsValueProcessor.class )
public class UnivariateValueProcessorPluginType extends BasePluginType {
  private static final UnivariateValueProcessorPluginType instance = new UnivariateValueProcessorPluginType();

  public static UnivariateValueProcessorPluginType getInstance() {
    return instance;
  }

  private UnivariateValueProcessorPluginType() {
    super( UnivariateValueProcessorPlugin.class, "UnivariateProcessor", "UnivariateProcessor" );
    populateFolders( "univariate" );
  }

  @Override
  protected void registerNatives() throws KettlePluginException {
    handlePluginAnnotation( CacheingValueProcessor.class, CacheingValueProcessor.class
        .getAnnotation( UnivariateValueProcessorPlugin.class ), new ArrayList<String>(), true, null );
    handlePluginAnnotation( CountValueProcessor.class, CountValueProcessor.class
        .getAnnotation( UnivariateValueProcessorPlugin.class ), new ArrayList<String>(), true, null );
    handlePluginAnnotation( MaxValueProcessor.class, MaxValueProcessor.class
        .getAnnotation( UnivariateValueProcessorPlugin.class ), new ArrayList<String>(), true, null );
    handlePluginAnnotation( MinValueProcessor.class, MinValueProcessor.class
        .getAnnotation( UnivariateValueProcessorPlugin.class ), new ArrayList<String>(), true, null );
    handlePluginAnnotation( SumOfSquaresValueProcessor.class, SumOfSquaresValueProcessor.class
        .getAnnotation( UnivariateValueProcessorPlugin.class ), new ArrayList<String>(), true, null );
    handlePluginAnnotation( SumValueProcessor.class, SumValueProcessor.class
        .getAnnotation( UnivariateValueProcessorPlugin.class ), new ArrayList<String>(), true, null );
    handlePluginAnnotation( TDigestValueProcessor.class, TDigestValueProcessor.class
        .getAnnotation( UnivariateValueProcessorPlugin.class ), new ArrayList<String>(), true, null );
  }
}
