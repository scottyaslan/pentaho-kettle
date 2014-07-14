package org.pentaho.di.trans.steps.univariatestats;

import java.util.List;
import java.util.Map;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;

public interface UnivariateStatsValueProducer {
  public String getName();

  public ValueMetaInterface getOutputValueMeta() throws KettlePluginException;

  public Object getValue();

  public void setOrigin( String origin );

  public void setParameters( Map<String, Object> parameters );

  public String getXml();

  public void loadXML( Node producerNode, List<DatabaseMeta> databases, IMetaStore metaStore ) throws KettleXMLException;

  public void saveRep( Repository rep, IMetaStore metaStore, ObjectId id_transformation, ObjectId id_step, int nr,
      String prefix ) throws KettleException;

  public void readRep( Repository rep, IMetaStore metaStore, ObjectId id_step, List<DatabaseMeta> databases, int nr,
      String prefix ) throws KettleException;
}
