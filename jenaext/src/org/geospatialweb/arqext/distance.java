package org.geospatialweb.arqext;

import org.geospatialweb.geometry.GeometryFunction;

import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.function.FunctionBase4;

public class distance extends FunctionBase4 {

	@Override
	public NodeValue exec(NodeValue v1, NodeValue v2, NodeValue v3, NodeValue v4) {
		double lat = v1.getDouble();
		double lon = v2.getDouble();
		double lat2 = v3.getDouble();
		double lon2 = v4.getDouble();
	
		GeometryFunction geo = new GeometryFunction();
		geo.getDistance2D(lat, lon, lat2, lon2);
		return null;
	}


}
