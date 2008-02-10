package org.geospatialweb.arqext;

import java.util.LinkedList;
import java.util.List;

import spatialindex.IData;
import spatialindex.INode;
import spatialindex.IShape;
import spatialindex.ISpatialIndex;
import spatialindex.IVisitor;
import spatialindex.Point;
import spatialindex.Region;
import spatialindex.SpatialIndex;
import spatialindex.rtree.RTree;
import spatialindex.storagemanager.IStorageManager;
import spatialindex.storagemanager.PropertySet;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;

/**
 * Basic indexer for spatial data annotated with
 * the w3c wgs84_pos namespace. Any resource having lat and long
 * properties from namespace http://www.w3.org/2003/01/geo/wgs84_pos#
 * are indexed, along with their URI.
 * 
 * @author Taylor Cowan
 */
public class Indexer {

	private ISpatialIndex index;
	private int id = 0;
	public static final String w3c_geo = "http://www.w3.org/2003/01/geo/wgs84_pos#";
	public static final String defaultQueryString = 
		"PREFIX geo: <" + w3c_geo + ">\n\n " +
		"SELECT ?s ?lat ?lon " + 
		"WHERE {?s geo:lat ?lat . ?s geo:long ?lon}";

	public Indexer(ISpatialIndex i) {
		index = i;
	}
	
	/**
	 * Creates a default indexer that uses an in memory store.
	 *  
	 * @return
	 */
	public static Indexer createDefaultIndexer() {
		PropertySet props = new PropertySet();
		props.setProperty("Dimension", 2);
		IStorageManager store = SpatialIndex.createMemoryStorageManager();
		RTree rtree = new RTree(props, store);
		return new Indexer(rtree);
	}
	
	public void createIndex(Model m) {
		createIndex(m,defaultQueryString);
	}

	public void createIndex(Model m, String q) {

		Query query = QueryFactory.create(q);
		QueryExecution qexec = QueryExecutionFactory.create(query, m);
		try {
			ResultSet results = qexec.execSelect();
			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();
				double lat =  soln.getLiteral("lat").getDouble();
				double lon = soln.getLiteral("lon").getDouble();
				String subject = soln.getResource("s").getURI();
				Point p = new Point(new double[] {lat,lon});
				index.insertData(subject.getBytes(), p, id++);
			}
		} finally {
			qexec.close();
		}

	}
	
	public int getSize() {
		return id;
	}
	
	public List<String> getNearby(double lat, double lon, int i) {
		
		final List<String> results = new LinkedList<String>();
		IVisitor v = new IVisitor() {
			public void visitData(IData d) {results.add(new String(d.getData()));}
			public void visitNode(INode n) {}			
		};
		index.nearestNeighborQuery(i, new Point(lat, lon), v);
		return results;
	}
	
	public List<String> getWithin(double lat, double lon, double lat2, double lon2) {
		
		final List<String> results = new LinkedList<String>();
		IVisitor v = new IVisitor() {
			public void visitData(IData d) {results.add(new String(d.getData()));}
			public void visitNode(INode n) {}			
		};
		double[] low = new double[] {lat, lon};
		double[] high = new double[] {lat2, lon2};
		IShape shape = new Region(low, high);
		index.containmentQuery(shape, v);
		return results;
	}	
	

}
