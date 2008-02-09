package org.geospatialweb.geometry;

public class GeometryFunction {
	/*
	 * @author 	Marco Neumann
	 * @serial  1/21/2008
	 * @version 1
	 * @see 	http://geospatialweb.googlecode.com
	 */
	
	
	//radius of the equator in km
	protected static final double RADIUS_EQUATOR = 6378;
	
	//polar radius  in km
	protected static final double RADIUS_POLAR = 6357;  
	
	public double getDistance2D(double x1, double y1, double x2, double y2){
		double dx = x2-x1;
		double dy = y2-y1;
		double distance = Math.sqrt(dx*dx + dy*dy);
		
		return distance;
	}

	public double getDistance3D(double x1, double y1, double z1, double x2, double y2, double z2){
		double dx = x2-x1;
		double dy = y2-y1;
		double dz = z2-z1;
		double distance = Math.sqrt(dx*dx + dy*dy + dz*dz);
		
		return distance;
	} 
	
	static public double getDistanceSpheroid(double x1, double y1, double x2, double y2) {
		    double lat1 = radians(x1), lat2 = radians(x2), dlat = lat2-lat1;
		    double dlong = radians(y2)-radians(y1);

		    double a = square(Math.sin(dlat/2)) 
		               + Math.cos(lat1)*Math.cos(lat2)*square(Math.sin(dlong/2));
		    double c = 2*Math.atan2(Math.sqrt(a),Math.sqrt(1-a)); 
		    double r = globeRadiusOfCurvature((lat1+lat2)/2); 
		    return r*c;
	}

	protected static final double radians(double degrees) {
		    return degrees * (2*Math.PI) / 360;
	}
	
	protected static final double globeRadiusOfCurvature(double lat) {
		    double a = RADIUS_EQUATOR;  
		    double b = RADIUS_POLAR;    
		    double e = Math.sqrt(1 - Math.sqrt(b/a)); 
		    return a*Math.sqrt(1-Math.sqrt(e)) / (1-Math.sqrt(e*Math.sin(lat)));
	}
	 
	 protected static final double square(double d) {
		    return d*d;
	}
	
	boolean equals(){return false;}		
	boolean disjoint(){return false;}
	boolean intersects(){return false;}
	boolean touches(){return false;}
	boolean crosses(){return false;}
	boolean contains(){return false;}
	boolean overlaps(){return false;}
	boolean within(){return false;}
	
}
