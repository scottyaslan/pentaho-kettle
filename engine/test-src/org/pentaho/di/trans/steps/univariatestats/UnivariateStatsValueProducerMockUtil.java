package org.pentaho.di.trans.steps.univariatestats;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.StringObjectId;
import org.pentaho.di.trans.steps.loadsave.MemoryRepository;
import org.pentaho.metastore.api.IMetaStore;

public class UnivariateStatsValueProducerMockUtil {
  public static UnivariateStatsValueProcessor mockProducer( Double number ) throws KettlePluginException,
    KettleValueException {
    Object testObj = new Object();
    UnivariateStatsValueProcessor mockProducer = mock( UnivariateStatsValueProcessor.class );
    ValueMetaInterface mockValueMetaInterface = mock( ValueMetaInterface.class );
    when( mockProducer.getOutputValueMeta() ).thenReturn( mockValueMetaInterface );
    when( mockProducer.getValue() ).thenReturn( testObj );
    when( mockValueMetaInterface.getNumber( testObj ) ).thenReturn( number );
    return mockProducer;
  }

  public static UnivariateStatsValueProcessor mockProducer( List<Double> numbers ) throws KettlePluginException,
    KettleValueException {
    UnivariateStatsValueProcessor mockProducer = mock( UnivariateStatsValueProcessor.class );
    ValueMetaInterface mockValueMetaInterface = mock( ValueMetaInterface.class );
    when( mockProducer.getOutputValueMeta() ).thenReturn( mockValueMetaInterface );
    when( mockProducer.getValue() ).thenReturn( numbers );
    return mockProducer;
  }

  public static UnivariateStatsValueProcessor mockProducer( Object testObj ) throws KettlePluginException,
    KettleValueException {
    UnivariateStatsValueProcessor mockProducer = mock( UnivariateStatsValueProcessor.class );
    ValueMetaInterface mockValueMetaInterface = mock( ValueMetaInterface.class );
    when( mockProducer.getOutputValueMeta() ).thenReturn( mockValueMetaInterface );
    when( mockProducer.getValue() ).thenReturn( testObj );
    return mockProducer;
  }

  public static void testProducerRoundTrip( UnivariateStatsValueProducer producer ) throws Exception {
    Map<String, Object> origMap = new HashMap<String, Object>( producer.getParameters() );
    MemoryRepository rep = new MemoryRepository();
    Random random = new Random();
    ObjectId transId = new StringObjectId( UUID.randomUUID().toString() );
    ObjectId stepId = new StringObjectId( UUID.randomUUID().toString() );
    int nr = random.nextInt();
    String prefix = UUID.randomUUID().toString();
    IMetaStore mockIMetaStore = mock( IMetaStore.class );
    producer.saveRep( rep, mockIMetaStore, transId, stepId, nr, prefix );
    UnivariateStatsValueProducer loadRepProducer = producer.getClass().getConstructor().newInstance();
    loadRepProducer.setOrigin( producer.getOrigin() );
    loadRepProducer.readRep( rep, mockIMetaStore, stepId, new ArrayList<DatabaseMeta>(), nr, prefix );
    assertEquals( origMap, loadRepProducer.getParameters() );
    assertEquals( producer.getOutputValueMeta().getName(), loadRepProducer.getOutputValueMeta().getName() );
    mockIMetaStore = mock( IMetaStore.class );
    String xml = "<producer>" + producer.getXml() + "</producer>";
    UnivariateStatsValueProducer loadXmlProducer = producer.getClass().getConstructor().newInstance();
    loadXmlProducer.setOrigin( producer.getOrigin() );
    loadXmlProducer
        .loadXML( XMLHandler.loadXMLString( xml, "producer" ), new ArrayList<DatabaseMeta>(), mockIMetaStore );
    assertEquals( origMap, loadXmlProducer.getParameters() );
    assertEquals( producer.getOutputValueMeta().getName(), loadXmlProducer.getOutputValueMeta().getName() );
  }
}
