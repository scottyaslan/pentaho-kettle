package org.pentaho.di.ui.trans.steps.univariatestats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.swt.widgets.TableItem;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaFactory;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueCalculator;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueProcessor;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueProducer;
import org.pentaho.di.trans.steps.univariatestats.stats.UnivariateValueCalculatorPlugin;
import org.pentaho.di.trans.steps.univariatestats.stats.UnivariateValueProcessorPlugin;
import org.pentaho.di.ui.core.widget.ColumnInfo;

public class UnivariateProducerBinding {
  private final List<UnivariateStatsValueProducer> producers;
  private final Set<Class<?>> producerClasses;
  private List<ColumnInfo> columnInfos;
  private List<String> parameterList;

  public UnivariateProducerBinding() {
    producers = new ArrayList<UnivariateStatsValueProducer>();
    producerClasses = new HashSet<Class<?>>();
    for ( UnivariateStatsValueProducer producer : producers ) {
      producerClasses.add( producer.getClass() );
    }
    columnInfos = null;
  }

  public void addProducer( UnivariateStatsValueProducer producer ) {
    this.producers.add( producer );
    producerClasses.add( producer.getClass() );
    columnInfos = null;
  }

  public List<ColumnInfo> getColumnInfos() {
    init();
    return Collections.unmodifiableList( columnInfos );
  }

  private UnivariateStatsValueProducer findProducer( TableItem item, int offset ) {
    if ( producers.size() > 0 ) {
      String firstVal = item.getText( offset );
      if ( producers.size() == 1 ) {
        if ( "True".equalsIgnoreCase( firstVal ) ) {
          return producers.get( 0 );
        }
      } else {
        for ( UnivariateStatsValueProducer producer : producers ) {
          if ( getName( producer ).equals( firstVal ) ) {
            return producer;
          }
        }
      }
    }
    return null;
  }

  public void setProducers( List<UnivariateStatsValueProducer> producers, TableItem item, int offset ) {
    for ( UnivariateStatsValueProducer producer : producers ) {
      if ( producerClasses.contains( producer.getClass() ) ) {
        int index = offset;
        if ( this.producers.size() > 1 ) {
          item.setText( index++, BaseMessages.getString( producer.getClass(), getName( producer ) ) );
        } else {
          item.setText( index++, "True" );
        }
        Map<String, Object> parameters = producer.getParameters();
        Map<String, Integer> parameterTypes = getParameters( producer );
        for ( String parameter : parameterList ) {
          Object value = parameters.get( parameter );
          String strValue = null;
          if ( value != null ) {
            try {
              strValue =
                  (String) ValueMetaFactory.createValueMeta( ValueMetaInterface.TYPE_STRING ).convertDataCompatible(
                      ValueMetaFactory.createValueMeta( parameterTypes.get( parameter ) ), value );
            } catch ( KettleValueException e ) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            } catch ( KettlePluginException e ) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
          }
          item.setText( index++, Const.NVL( strValue, "" ) );
        }
        return;
      }
    }
  }

  public UnivariateStatsValueProducer getProducer( TableItem item, int offset ) {
    UnivariateStatsValueProducer producer = findProducer( item, offset );
    if ( producer != null ) {
      int index = offset + 1;
      Map<String, Integer> parameterTypes = getParameters( producer );
      Map<String, Object> parameters = new HashMap<String, Object>();
      for ( int i = 0; i < parameterList.size(); i++ ) {
        String currentParameter = parameterList.get( i );
        Integer type = parameterTypes.get( currentParameter );
        if ( type != null ) {
          if ( type == ValueMetaInterface.TYPE_BOOLEAN ) {
            parameters.put( currentParameter, "True".equalsIgnoreCase( item.getText( index ) ) );
          } else {
            try {
              parameters.put( currentParameter, ValueMetaFactory.createValueMeta( type ).convertDataCompatible(
                  ValueMetaFactory.createValueMeta( ValueMetaInterface.TYPE_STRING ), item.getText( index ) ) );
            } catch ( KettleException e ) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
          }
        }
        index++;
      }
      producer.setParameters( parameters );
    }
    return producer;
  }

  private void init() {
    if ( columnInfos == null ) {
      List<ColumnInfo> columnInfos = new ArrayList<ColumnInfo>();
      List<String> parameterList = new ArrayList<String>();
      if ( producers.size() > 0 ) {
        if ( producers.size() == 1 ) {
          UnivariateStatsValueProducer producer = producers.get( 0 );
          columnInfos.add( new ColumnInfo( BaseMessages.getString( producer.getClass(), getName( producer ) ),
              ColumnInfo.COLUMN_TYPE_CCOMBO, new String[] { "True", "False" }, true ) );
        } else {
          List<String> options = new ArrayList<String>();
          options.add( "" );
          for ( UnivariateStatsValueProducer producer : producers ) {
            options.add( BaseMessages.getString( producer.getClass(), getName( producer ) ) );
          }
          UnivariateStatsValueProducer producer = producers.get( 0 );
          columnInfos.add( new ColumnInfo( BaseMessages.getString( producer.getClass(), UnivariateProducerBindings
              .getProvides( producer ) ), ColumnInfo.COLUMN_TYPE_CCOMBO, options.toArray( new String[options.size()] ),
              true ) );
        }
        Set<String> parameters = new HashSet<String>();
        Map<String, Set<Integer>> typeMap = new HashMap<String, Set<Integer>>();
        Map<String, String> parameterNames = new HashMap<String, String>();
        for ( UnivariateStatsValueProducer producer : producers ) {
          Map<String, Integer> parameterTypes = getParameters( producer );
          parameters.addAll( parameterTypes.keySet() );
          for ( Entry<String, Integer> entry : parameterTypes.entrySet() ) {
            Set<Integer> types = typeMap.get( entry.getKey() );
            if ( types == null ) {
              types = new HashSet<Integer>();
              typeMap.put( entry.getKey(), types );
            }
            types.add( entry.getValue() );
            parameterNames.put( entry.getKey(), BaseMessages.getString( producer.getClass(), entry.getKey() ) );
          }
        }
        if ( parameters.size() > 0 ) {
          parameterList = new ArrayList<String>( parameters );
          Collections.sort( parameterList );
          for ( String parameter : parameterList ) {
            Set<Integer> types = typeMap.get( parameter );
            if ( types.size() == 1 && types.contains( ValueMetaInterface.TYPE_BOOLEAN ) ) {
              columnInfos.add( new ColumnInfo( parameterNames.get( parameter ), ColumnInfo.COLUMN_TYPE_CCOMBO,
                  new String[] { "True", "False" }, true ) );
            } else {
              columnInfos.add( new ColumnInfo( parameterNames.get( parameter ), ColumnInfo.COLUMN_TYPE_TEXT, false ) );
            }
          }
        }
      }
      this.columnInfos = columnInfos;
      this.parameterList = parameterList;
    }
  }

  private Map<String, Integer> getParameters( UnivariateStatsValueProducer producer ) {
    Map<String, Integer> result = new HashMap<String, Integer>();
    if ( producer instanceof UnivariateStatsValueProcessor ) {
      UnivariateValueProcessorPlugin univariateValueProcessorPlugin =
          producer.getClass().getAnnotation( UnivariateValueProcessorPlugin.class );
      for ( int i = 0; i < univariateValueProcessorPlugin.parameterNames().length; i++ ) {
        result.put( univariateValueProcessorPlugin.parameterNames()[i],
            univariateValueProcessorPlugin.parameterTypes()[i] );
      }
    } else if ( producer instanceof UnivariateStatsValueCalculator ) {
      UnivariateValueCalculatorPlugin univariateValueCalculatorPlugin =
          producer.getClass().getAnnotation( UnivariateValueCalculatorPlugin.class );
      for ( int i = 0; i < univariateValueCalculatorPlugin.parameterNames().length; i++ ) {
        result.put( univariateValueCalculatorPlugin.parameterNames()[i], univariateValueCalculatorPlugin
            .parameterTypes()[i] );
      }
    }
    return result;
  }

  private String getName( UnivariateStatsValueProducer producer ) {
    if ( producer instanceof UnivariateStatsValueProcessor ) {
      return producer.getClass().getAnnotation( UnivariateValueProcessorPlugin.class ).name();
    } else if ( producer instanceof UnivariateStatsValueCalculator ) {
      return producer.getClass().getAnnotation( UnivariateValueCalculatorPlugin.class ).name();
    } else {
      return null;
    }
  }
}
