package org.pentaho.di.trans.steps.univariatestats.stats.processors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaFactory;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueProducer;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;

public abstract class AbstractValueProducer implements UnivariateStatsValueProducer {
  private String name;
  private String origin;
  private final int outputType;
  private ValueMetaInterface outputMeta = null;

  public AbstractValueProducer( String name, int outputType ) {
    this.name = name;
    this.outputType = outputType;
  }

  protected String getValueMetaName() {
    return origin + "(" + getName() + ")";
  }

  @Override
  public String getName() {
    return BaseMessages.getString( getClass(), name );
  }

  public void setName( String name ) {
    this.name = name;
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
  public String getOrigin() {
    return origin;
  }

  @Override
  public void setParameters( Map<String, Object> parameters ) {
    // noop default
  }

  @Override
  public String getXml() {
    return "";
  }

  @Override
  public void loadXML( Node producerNode, List<DatabaseMeta> databases, IMetaStore metaStore )
    throws KettleXMLException {
    // Noop
  }

  @Override
  public void saveRep( Repository rep, IMetaStore metaStore, ObjectId id_transformation, ObjectId id_step, int nr,
      String prefix ) throws KettleException {
    // Noop
  }

  @Override
  public void readRep( Repository rep, IMetaStore metaStore, ObjectId id_step, List<DatabaseMeta> databases, int nr,
      String prefix ) throws KettleException {
    // Noop
  }

  @Override
  public Map<String, Object> getParameters() {
    return new HashMap<String, Object>();
  }
}
