package org.geospatialweb.arqext;

import java.util.LinkedList;
import java.util.List;

import spatialindex.IData;
import spatialindex.INode;
import spatialindex.ISpatialIndex;
import spatialindex.IVisitor;
import spatialindex.Point;
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
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * 
 * @author Taylor Cowan
 */
public class Indexer {

	private ISpatialIndex index;
	private int id = 0;

	public Indexer(ISpatialIndex i) {
		index = i;
	}

	public void createIndex(Model m) {
		String queryString = 
			"PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>\n\n " +
			"SELECT ?s ?lat ?lon " + 
			"WHERE {?s geo:lat ?lat . ?s geo:long ?lon}";
		Query query = QueryFactory.create(queryString);
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
	
	public List<String> getNearby(double lat, double lon, int i) {
		
		final List<String> results = new LinkedList<String>();
		IVisitor v = new IVisitor() {
			public void visitData(IData d) {results.add(new String(d.getData()));}
			public void visitNode(INode n) {}			
		};

		Point p = new Point(lat, lon);
		index.nearestNeighborQuery(i, p, v);
		return results;
	}
	

}
