package org.pentaho.di.ui.trans.steps.univariatestats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.widgets.TableItem;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueCalculator;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueProcessor;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueProducer;
import org.pentaho.di.trans.steps.univariatestats.stats.UnivariateValueCalculatorPlugin;
import org.pentaho.di.trans.steps.univariatestats.stats.UnivariateValueCalculatorPluginType;
import org.pentaho.di.trans.steps.univariatestats.stats.UnivariateValueProcessorPlugin;
import org.pentaho.di.trans.steps.univariatestats.stats.UnivariateValueProcessorPluginType;
import org.pentaho.di.ui.core.widget.ColumnInfo;

public class UnivariateProducerBindings {
  private final List<UnivariateProducerBinding> bindings;

  public UnivariateProducerBindings() {
    bindings = new ArrayList<UnivariateProducerBinding>();
    Map<String, UnivariateProducerBinding> providesBindings = new HashMap<String, UnivariateProducerBinding>();
    for ( PluginInterface plugin : getPlugins() ) {
      try {
        UnivariateStatsValueProducer producer =
            (UnivariateStatsValueProducer) PluginRegistry.getInstance().loadClass( plugin );
        if ( shouldShow( producer ) ) {
          String provides = getProvides( producer.getClass() );
          UnivariateProducerBinding binding = null;
          if ( !Const.isEmpty( provides ) ) {
            binding = providesBindings.get( provides );
            if ( binding == null ) {
              binding = new UnivariateProducerBinding();
              providesBindings.put( provides, binding );
              bindings.add( binding );
            }
          } else {
            binding = new UnivariateProducerBinding();
            bindings.add( binding );
          }
          binding.addProducerClass( producer.getClass() );
        }
      } catch ( KettlePluginException e1 ) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }
    }
  }

  private boolean shouldShow( UnivariateStatsValueProducer producer ) {
    if ( producer instanceof UnivariateStatsValueProcessor ) {
      return !producer.getClass().getAnnotation( UnivariateValueProcessorPlugin.class ).hidden();
    }
    return true;
  }

  private List<PluginInterface> getPlugins() {
    Set<PluginInterface> ids = new HashSet<PluginInterface>();
    ids.addAll( PluginRegistry.getInstance().getPlugins( UnivariateValueProcessorPluginType.class ) );
    ids.addAll( PluginRegistry.getInstance().getPlugins( UnivariateValueCalculatorPluginType.class ) );
    List<PluginInterface> result = new ArrayList<PluginInterface>( ids );
    return result;
  }

  public static String getProvides( Class<? extends UnivariateStatsValueProducer> producer ) {
    if ( UnivariateStatsValueCalculator.class.isAssignableFrom( producer ) ) {
      return producer.getAnnotation( UnivariateValueCalculatorPlugin.class ).provides();
    } else {
      return null;
    }
  }

  public List<ColumnInfo> getColumnInfos() {
    List<ColumnInfo> infos = new ArrayList<ColumnInfo>();
    for ( UnivariateProducerBinding binding : bindings ) {
      infos.addAll( binding.getColumnInfos() );
    }
    return infos;
  }

  public List<UnivariateStatsValueProducer> getProducers( TableItem item ) {
    List<UnivariateStatsValueProducer> producers = new ArrayList<UnivariateStatsValueProducer>();
    int offset = 2;
    for ( UnivariateProducerBinding binding : bindings ) {
      UnivariateStatsValueProducer producer = binding.getProducer( item, offset );
      if ( producer != null ) {
        producers.add( producer );
      }
      offset += binding.getColumnInfos().size();
    }
    return producers;
  }

  public void setUpTable( List<UnivariateStatsValueProducer> producers, TableItem item ) {
    int offset = 2;
    for ( UnivariateProducerBinding binding : bindings ) {
      binding.setProducers( producers, item, offset );
      offset += binding.getColumnInfos().size();
    }
  }
}
