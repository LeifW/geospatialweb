package org.geospatialweb.geometry;

public class GeometryFunction {
	/*
	 * @author 	Marco Neumann
	 * @serial  1/21/2008
	 * @version 1
	 * @see 	http://geospatialweb.googlecode.com
	 */
	
	
	public double distance2D(double x1, double y1, double x2, double y2){
		double dx = x2-x1;
		double dy = y2-y1;
		double distance = Math.sqrt(dx*dx + dy*dy);
		
		return distance;
	}

	public double distance3D(double x1, double y1, double z1, double x2, double y2, double z2){
		double dx = x2-x1;
		double dy = y2-y1;
		double dz = z2-z1;
		double distance = Math.sqrt(dx*dx + dy*dy + dz*dz);
		
		return distance;
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
