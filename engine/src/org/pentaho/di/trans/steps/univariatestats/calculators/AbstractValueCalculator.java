package org.pentaho.di.trans.steps.univariatestats.calculators;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueCalculator;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueProducer;
import org.pentaho.di.trans.steps.univariatestats.processors.AbstractValueProcessor;

public abstract class AbstractValueCalculator extends AbstractValueProcessor implements UnivariateStatsValueCalculator {
  private static final Class<AbstractValueCalculator> PKG = AbstractValueCalculator.class;
  private final Set<Class<? extends UnivariateStatsValueProducer>> requiredProducers;

  public AbstractValueCalculator( String name, String origin, int outputType,
      Set<Class<? extends UnivariateStatsValueProducer>> requiredProducers ) {
    super( name, origin, outputType );
    this.requiredProducers =
        Collections.unmodifiableSet( new HashSet<Class<? extends UnivariateStatsValueProducer>>( requiredProducers ) );
  }

  @Override
  public Set<Class<? extends UnivariateStatsValueProducer>> getRequiredProcessors() {
    return requiredProducers;
  }

  @Override
  public void process( Set<UnivariateStatsValueProducer> producers ) throws KettleStepException, KettleValueException,
    KettlePluginException {
    producers = new HashSet<UnivariateStatsValueProducer>( producers );
    Set<Class<? extends UnivariateStatsValueProducer>> requiredProducers =
        new HashSet<Class<? extends UnivariateStatsValueProducer>>( this.requiredProducers );
    Map<Class<? extends UnivariateStatsValueProducer>, UnivariateStatsValueProducer> producerMap =
        new HashMap<Class<? extends UnivariateStatsValueProducer>, UnivariateStatsValueProducer>();
    for ( UnivariateStatsValueProducer producer : producers ) {
      if ( requiredProducers.remove( producer.getClass() ) ) {
        producerMap.put( producer.getClass(), producer );
      }
    }
    producers.removeAll( producerMap.values() );
    for ( Class<? extends UnivariateStatsValueProducer> clazz : requiredProducers ) {
      UnivariateStatsValueProducer foundProducer = null;
      for ( UnivariateStatsValueProducer producer : producers ) {
        if ( clazz.isInstance( producer ) ) {
          foundProducer = producer;
          break;
        }
      }
      if ( foundProducer != null ) {
        producers.remove( foundProducer );
        producerMap.put( clazz, foundProducer );
      } else {
        throw new KettleStepException( BaseMessages.getString( PKG, "AbstractValueCalculator.MissingProducer", clazz
            .toString() ) );
      }
    }

    process( producerMap );
  }

  protected abstract void process(
      Map<Class<? extends UnivariateStatsValueProducer>, UnivariateStatsValueProducer> producerMap )
    throws KettleValueException, KettlePluginException;
}
