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
  private final List<Class<? extends UnivariateStatsValueProducer>> producerClassList;
  private final Set<Class<?>> producerClassSet;
  private List<ColumnInfo> columnInfos;
  private List<String> parameterList;
  private String trueVal;
  private String falseVal;

  public UnivariateProducerBinding() {
    producerClassList = new ArrayList<Class<? extends UnivariateStatsValueProducer>>();
    producerClassSet = new HashSet<Class<?>>();
    for ( Class<? extends UnivariateStatsValueProducer> producer : producerClassList ) {
      producerClassSet.add( producer );
    }
    columnInfos = null;
  }

  public void addProducerClass( Class<? extends UnivariateStatsValueProducer> producerClass ) {
    this.producerClassList.add( producerClass );
    producerClassSet.add( producerClass );
    columnInfos = null;
  }

  public List<ColumnInfo> getColumnInfos() {
    init();
    return Collections.unmodifiableList( columnInfos );
  }

  private Class<? extends UnivariateStatsValueProducer> findProducerClass( TableItem item, int offset ) {
    init();
    if ( producerClassList.size() > 0 ) {
      String firstVal = item.getText( offset );
      if ( producerClassList.size() == 1 ) {
        if ( trueVal.equalsIgnoreCase( firstVal ) ) {
          return producerClassList.get( 0 );
        }
      } else {
        for ( Class<? extends UnivariateStatsValueProducer> producerClass : producerClassList ) {
          if ( getName( producerClass ).equals( firstVal ) ) {
            return producerClass;
          }
        }
      }
    }
    return null;
  }

  public void setProducers( List<UnivariateStatsValueProducer> producers, TableItem item, int offset ) {
    init();
    for ( UnivariateStatsValueProducer producer : producers ) {
      if ( producerClassSet.contains( producer.getClass() ) ) {
        int index = offset;
        if ( this.producerClassList.size() > 1 ) {
          item.setText( index++, getName( producer.getClass() ) );
        } else {
          item.setText( index++, trueVal );
        }
        Map<String, Object> parameters = producer.getParameters();
        Map<String, Integer> parameterTypes = getParameters( producer.getClass() );
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
    if ( this.producerClassList.size() == 1 ) {
      item.setText( offset, falseVal );
    } else {
      item.setText( offset, "" );
    }
  }

  public UnivariateStatsValueProducer getProducer( TableItem item, int offset ) {
    Class<? extends UnivariateStatsValueProducer> producer = findProducerClass( item, offset );
    if ( producer != null ) {
      int index = offset + 1;
      Map<String, Integer> parameterTypes = getParameters( producer );
      Map<String, Object> parameters = new HashMap<String, Object>();
      for ( int i = 0; i < parameterList.size(); i++ ) {
        String currentParameter = parameterList.get( i );
        Integer type = parameterTypes.get( currentParameter );
        if ( type != null ) {
          try {
            parameters.put( currentParameter, ValueMetaFactory.createValueMeta( type ).convertDataCompatible(
                ValueMetaFactory.createValueMeta( ValueMetaInterface.TYPE_STRING ), item.getText( index ) ) );
          } catch ( KettleException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
        index++;
      }
      UnivariateStatsValueProducer result = null;
      try {
        result = producer.newInstance();
        result.setParameters( parameters );
      } catch ( Exception e ) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      return result;
    }
    return null;
  }

  private void init() {
    if ( columnInfos == null ) {
      ValueMetaInterface booleanValueMeta;
      try {
        booleanValueMeta = ValueMetaFactory.createValueMeta( ValueMetaInterface.TYPE_BOOLEAN );
        trueVal = booleanValueMeta.getString( true );
        falseVal = booleanValueMeta.getString( false );
      } catch ( KettleException e ) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      String[] trueOrFalse = new String[] { trueVal, falseVal };
      List<ColumnInfo> columnInfos = new ArrayList<ColumnInfo>();
      List<String> parameterList = new ArrayList<String>();
      if ( producerClassList.size() > 0 ) {
        if ( producerClassList.size() == 1 ) {
          Class<? extends UnivariateStatsValueProducer> producer = producerClassList.get( 0 );
          columnInfos.add( new ColumnInfo( getName( producer ), ColumnInfo.COLUMN_TYPE_CCOMBO, trueOrFalse, true ) );
        } else {
          List<String> options = new ArrayList<String>();
          options.add( "" );
          for ( Class<? extends UnivariateStatsValueProducer> producer : producerClassList ) {
            options.add( getName( producer ) );
          }
          Class<? extends UnivariateStatsValueProducer> producer = producerClassList.get( 0 );
          columnInfos.add( new ColumnInfo( BaseMessages.getString( producer, UnivariateProducerBindings
              .getProvides( producer ) ), ColumnInfo.COLUMN_TYPE_CCOMBO, options.toArray( new String[options.size()] ),
              true ) );
        }
        Set<String> parameters = new HashSet<String>();
        Map<String, Set<Integer>> typeMap = new HashMap<String, Set<Integer>>();
        Map<String, String> parameterNames = new HashMap<String, String>();
        for ( Class<? extends UnivariateStatsValueProducer> producer : producerClassList ) {
          Map<String, Integer> parameterTypes = getParameters( producer );
          parameters.addAll( parameterTypes.keySet() );
          for ( Entry<String, Integer> entry : parameterTypes.entrySet() ) {
            Set<Integer> types = typeMap.get( entry.getKey() );
            if ( types == null ) {
              types = new HashSet<Integer>();
              typeMap.put( entry.getKey(), types );
            }
            types.add( entry.getValue() );
            parameterNames.put( entry.getKey(), BaseMessages.getString( producer, entry.getKey() ) );
          }
        }
        if ( parameters.size() > 0 ) {
          parameterList = new ArrayList<String>( parameters );
          Collections.sort( parameterList );
          for ( String parameter : parameterList ) {
            Set<Integer> types = typeMap.get( parameter );
            if ( types.size() == 1 && types.contains( ValueMetaInterface.TYPE_BOOLEAN ) ) {
              columnInfos.add( new ColumnInfo( parameterNames.get( parameter ), ColumnInfo.COLUMN_TYPE_CCOMBO,
                  trueOrFalse, true ) );
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

  private Map<String, Integer> getParameters( Class<? extends UnivariateStatsValueProducer> producer ) {
    Map<String, Integer> result = new HashMap<String, Integer>();
    if ( UnivariateStatsValueProcessor.class.isAssignableFrom( producer ) ) {
      UnivariateValueProcessorPlugin univariateValueProcessorPlugin =
          producer.getAnnotation( UnivariateValueProcessorPlugin.class );
      for ( int i = 0; i < univariateValueProcessorPlugin.parameterNames().length; i++ ) {
        result.put( univariateValueProcessorPlugin.parameterNames()[i],
            univariateValueProcessorPlugin.parameterTypes()[i] );
      }
    } else if ( UnivariateStatsValueCalculator.class.isAssignableFrom( producer ) ) {
      UnivariateValueCalculatorPlugin univariateValueCalculatorPlugin =
          producer.getAnnotation( UnivariateValueCalculatorPlugin.class );
      for ( int i = 0; i < univariateValueCalculatorPlugin.parameterNames().length; i++ ) {
        result.put( univariateValueCalculatorPlugin.parameterNames()[i], univariateValueCalculatorPlugin
            .parameterTypes()[i] );
      }
    }
    return result;
  }

  private String getName( Class<? extends UnivariateStatsValueProducer> producer ) {
    String result = null;
    if ( UnivariateStatsValueProcessor.class.isAssignableFrom( producer ) ) {
      result = producer.getAnnotation( UnivariateValueProcessorPlugin.class ).name();
    } else if ( UnivariateStatsValueCalculator.class.isAssignableFrom( producer ) ) {
      result = producer.getAnnotation( UnivariateValueCalculatorPlugin.class ).name();
    }
    if ( result != null ) {
      return BaseMessages.getString( producer, result );
    }
    return result;
  }
}
