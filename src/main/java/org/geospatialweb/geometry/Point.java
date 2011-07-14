package org.geospatialweb.geometry;
/*
 * @author 	Marco Neumann
 * @serial  1/21/2008
 * @version 1
 * @see 	http://geospatialweb.googlecode.com
 */

public class Point{
	double x=0;
	double y=0;
	double z=0;
	
	void setX(double x){
		this.x = x;
	}
	
	void setY(double y){
		this.y = y;
	}
	
	void setZ(double z){
		this.z = z;
	}
	
	double getX(){
		return x;
	}
	
	double getY(){
		return y;
	}

	double getZ(){
		return z;
	}

    public Point(double x, double y, double z) {
    	this.x = x;
    	this.y = y;
    }

    public Point(Point p) {
    	this(p.x, p.y, p.z);
    }
    
    public Point() {
    	this(0, 0, 0);
    }
}
