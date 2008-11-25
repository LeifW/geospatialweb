package test;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.geospatialweb.arqext.Indexer;
import org.geospatialweb.geometry.GeometryFunction;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import test.QueryTest;

import spatialindex.SpatialIndex;
import spatialindex.rtree.RTree;
import spatialindex.storagemanager.IStorageManager;
import spatialindex.storagemanager.PropertySet;

import junit.framework.TestCase;

//Test GeoSpatialWeb. Index RDF data and perform query 

public class TestGeoSpatialAPI extends TestCase {

	Logger log;
	
	public TestGeoSpatialAPI() {
		log = Logger.getLogger(GeometryFunction.class);
	}
	
	public void testGetDistanceSpheroid() {
		log.info(GeometryFunction.getDistanceSpheroid(40.45, 73.5, -13.23, 52.3)==6302.787147083358);
	}
	
	public void testSpatialQuery(){
		
		//Create the RTree-index 
		//IStorageManager store = SpatialIndex.createMemoryStorageManager();
		//RTree rtree = new RTree(props(), store);
		
		QueryTest qt = new QueryTest();
		try{
			qt.doIT();
		}catch(IOException ex){
			
		}
		
	}
	
	private static PropertySet props() {
		PropertySet ps2 = new PropertySet();
		ps2.setProperty("Dimension", 2);
		return ps2;
	}
	
}
