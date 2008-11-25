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

import test.SpatialQueryTest;

import spatialindex.SpatialIndex;
import spatialindex.rtree.RTree;
import spatialindex.storagemanager.IStorageManager;
import spatialindex.storagemanager.PropertySet;

import junit.framework.TestCase;

//Test GeoSpatialWeb. Index RDF data and perform query 

public class TestGeoSpatialWeb extends TestCase {
	
	public void testSpatialQuery(){
		
		// Create the RTree-index 
		// IStorageManager store = SpatialIndex.createMemoryStorageManager();
		// RTree rtree = new RTree(props(), store);
		
		SpatialQueryTest sqt = new SpatialQueryTest();
		try{
			sqt.runSpatialIndexTest();
		}catch(IOException ex){
			
		}
		
	}
	
	private static PropertySet props() {
		PropertySet ps2 = new PropertySet();
		ps2.setProperty("Dimension", 2);
		return ps2;
	}
	
}
