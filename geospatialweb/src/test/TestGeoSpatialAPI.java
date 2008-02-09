package test;

import org.apache.log4j.Logger;
import org.geospatialweb.geometry.GeometryFunction;

import spatialindex.SpatialIndex;
import spatialindex.rtree.RTree;
import spatialindex.storagemanager.IStorageManager;
import spatialindex.storagemanager.PropertySet;

import junit.framework.TestCase;


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
		IStorageManager store = SpatialIndex.createMemoryStorageManager();
		RTree rtree = new RTree(props(), store);
		
		String queryString = "PREFIX : <http://example.org/>"
			+ "PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>\n "
			+ "PREFIX ext: <java:org.geospatialweb.arqext.>\n\n "
			+ "SELECT ?city ?hotel ?lat1 ?lon1 ?lat2 ?lon2 " 
			+ "WHERE { ?c a :City . ?s ext:nearby( ?c 20 ) . ?s :name ?hotel . ?c :name ?city . ?s a :Airport "
			+ " . ?c geo:lat ?lat1 . ?c geo:long ?lon1 . ?s geo:lat ?lat2 . ?s geo:long ?lon2}";
		
		
	}
	
	private static PropertySet props() {
		PropertySet ps2 = new PropertySet();
		ps2.setProperty("Dimension", 2);
		return ps2;
	}
	
}
