package org.geospatialweb.vocabulary;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

/*
 * @author 	Marco Neumann
 * @serial  1/21/2008
 * @version 1
 * @see 	http://geospatialweb.googlecode.com
 */

public class GEO {
	private static Model m_model = ModelFactory.createDefaultModel();
    public static final String NS = "http://geospatialweb.googlecode.com/svn/trunk/geospatialweb/geo/elements/1.0/geo.owl";
    public static String getURI() {return NS;}
    
    
    //location predicates
    public static final Property latitude  = m_model.createProperty( NS+"#latitude" );
    public static final Property longitude  = m_model.createProperty( NS+"#longitude" );
    public static final Property altitude  = m_model.createProperty( NS+"#altitude" );

    public static final Property lat  = m_model.createProperty( NS+"#lat" );
    public static final Property lng  = m_model.createProperty( NS+"#long" );
    public static final Property alt  = m_model.createProperty( NS+"#alt" );    

    public static final Property x  = m_model.createProperty( NS+"#x" );
    public static final Property y  = m_model.createProperty( NS+"#y" );    
    public static final Property z  = m_model.createProperty( NS+"#z" );
    
    public static final Property height  = m_model.createProperty( NS+"#height" );
    
    //The DE-9IM Model binary predicates
    public static final Property equals  = m_model.createProperty( NS+"#equals" );
    public static final Property disjoint  = m_model.createProperty( NS+"#disjoint" );
    public static final Property intersects  = m_model.createProperty( NS+"#intersects" );
    public static final Property touches  = m_model.createProperty( NS+"#touches" );
    public static final Property crosses  = m_model.createProperty( NS+"#crosses" );
    public static final Property contains  = m_model.createProperty( NS+"#contains" );
    public static final Property overlaps  = m_model.createProperty( NS+"#overlaps" );
    public static final Property within  = m_model.createProperty( NS+"#within" );
    
    //Object Classes
    public static final Resource Point = m_model.createResource( NS+"#Point" );
    public static final Resource SRS = m_model.createResource( NS+"#SpatialReferenceSystem" );    
    public static final Resource Curve = m_model.createResource( NS+"#Curve" );
    public static final Resource GeometryCollection = m_model.createResource( NS+"#GeometryCollection" );    
    public static final Resource Surface = m_model.createResource( NS+"#Surface" );
    public static final Resource LineString = m_model.createResource( NS+"#LineString" );   
    
}

