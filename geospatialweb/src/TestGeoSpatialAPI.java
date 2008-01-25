import org.apache.log4j.Logger;
import org.geospatialweb.geometry.GeometryFunction;

import junit.framework.TestCase;


public class TestGeoSpatialAPI extends TestCase {

	Logger log;
	
	public TestGeoSpatialAPI() {
		log = Logger.getLogger(GeometryFunction.class);
	}
	
	
	public void testGetDistanceSpheroid() {
		log.info(GeometryFunction.getDistanceSpheroid(40.45, 73.5, -13.23, 52.3));
	}

}
