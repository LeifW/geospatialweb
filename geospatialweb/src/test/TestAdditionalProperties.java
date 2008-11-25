package test;

import org.apache.log4j.Logger;
import org.geospatialweb.geometry.GeometryFunction;

public class TestAdditionalProperties {
	Logger log;
	
	public TestAdditionalProperties() {
		log = Logger.getLogger(GeometryFunction.class);
	}
	
	public void testGetDistanceSpheroid() {
		GeometryFunction gf = new GeometryFunction();
		log.info(gf.getDistanceSpheroid(40.45, 73.5, -13.23, 52.3));
		log.info(gf.getDistanceSpheroid(40.45, 73.5, -13.23, 52.3)==6302.787147083358);
	}
}
