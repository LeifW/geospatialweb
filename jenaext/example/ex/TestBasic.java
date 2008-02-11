package ex;

/**
 * You'll need more that just the libs in jenalib.  jenalib has the bare minimum to
 * compile, however, to run this you need the full ARQ distribution set of libraries.
 * 
 */
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.geospatialweb.arqext.Geo;
import org.geospatialweb.arqext.Indexer;
import org.geospatialweb.geometry.GeometryFunction;
import org.junit.Test;

import spatialindex.SpatialIndex;
import spatialindex.rtree.RTree;
import spatialindex.storagemanager.IStorageManager;
import spatialindex.storagemanager.PropertySet;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class TestBasic {

	@Test
	public void england() throws IOException {
		System.out.println("Loading large file into jena model...");
		OntModel m = ClassifyGeodata.getModel("jenaext/GB.txt");
		Indexer i = Indexer.createDefaultIndexer();
		i.createIndex(m);


		String queryString = "PREFIX : <http://example.org/>"
			+ "PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>\n\n "
			+ "SELECT ?n ?lat ?lon WHERE { ?s a :City . ?s :name ?n . ?s geo:lat ?lat . ?s geo:long ?lon}";

		queryString = "PREFIX : <http://example.org/>"
			+ "PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>\n "
			+ "PREFIX ext: <java:org.geospatialweb.arqext.>\n\n "
			+ "SELECT ?city ?hotel ?lat1 ?lon1 ?lat2 ?lon2 " 
			+ "WHERE { ?c a :City . ?s ext:nearby( ?c 20 ) . ?s :name ?hotel . ?c :name ?city . ?s a :Airport "
			+ " . ?c geo:lat ?lat1 . ?c geo:long ?lon1 . ?s geo:lat ?lat2 . ?s geo:long ?lon2}";

		// First get a list of cities to find nearby things
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, m);
		Geo.setContext(qexec, i);

		try {
			ResultSet results = qexec.execSelect();
			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();
				
				double lat1 = soln.getLiteral("lat1").getDouble();
				double lon1 = soln.getLiteral("lon1").getDouble();
				double lat2 = soln.getLiteral("lat2").getDouble();
				double lon2 = soln.getLiteral("lon2").getDouble();
				String city = soln.getLiteral("city").getString();
				String hotel = soln.getLiteral("hotel").getString();
				double dist = GeometryFunction.getDistanceSpheroid(lat1, lon1, lat2, lon2);
				System.out.print(city + " " + hotel  + " " + dist + "\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			qexec.close();
		}
	}

	private void findNearbyStuff(OntModel m, Indexer i, String city,
			double lat, double lon) {
		String queryString = "PREFIX : <http://example.org/>"
				+ "PREFIX geo: <java:org.geospatialweb.arqext.>\n\n "
				+ "SELECT ?n WHERE { ?s geo:nearby(" + lat + " " + lon
				+ " 10) . ?s a :Other . ?s :name ?n }";

		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, m);
		Geo.setContext(qexec, i);
		try {
			long t1 = System.currentTimeMillis();
			ResultSet results = qexec.execSelect();
			long t2 = System.currentTimeMillis();
			System.out.println("Query complete in " + (t2 - t1) + "ms");
			System.out.println("Places near " + city);
			for (; results.hasNext();) {
				System.out.println("\t"
						+ results.nextSolution().getLiteral("n"));
			}
		} finally {
			qexec.close();
		}
	}

	@Test
	public void testWithin() {
		Model m = ModelFactory.createDefaultModel();
		m.read("file:jenaext/capitals.rdf");
		IStorageManager store = SpatialIndex.createMemoryStorageManager();
		RTree rtree = new RTree(props(), store);
		Indexer i = new Indexer(rtree);
		i.createIndex(m);
		String queryString = "PREFIX : <http://www.geonames.org/ontology#>"
				+ "PREFIX geo: <java:org.geospatialweb.arqext.>\n\n "
				+ "SELECT ?n WHERE { ?s geo:within(38.18 -9.78 53.75 22.41 ) . ?s :name ?n }";

		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, m);
		Geo.setContext(qexec, i);
		try {
			long t1 = System.currentTimeMillis();
			ResultSet results = qexec.execSelect();
			long t2 = System.currentTimeMillis();
			System.out.println(t2 - t1);
			for (; results.hasNext();) {
				System.out.println(results.nextSolution().getLiteral("n"));

			}
		} finally {
			qexec.close();
		}

	}

	@Test
	public void testGood() {

		Model m = ModelFactory.createDefaultModel();
		m.read("file:jenaext/capitals.rdf");
		IStorageManager store = SpatialIndex.createMemoryStorageManager();
		RTree rtree = new RTree(props(), store);
		Indexer i = new Indexer(rtree);
		i.createIndex(m);

		String queryString = "PREFIX : <http://www.geonames.org/ontology#>"
				+ "PREFIX geo: <java:org.geospatialweb.arqext.>\n\n "
				+ "SELECT ?n WHERE { ?s geo:nearby(19 -99 10) . ?s :name ?n }";

		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, m);
		Geo.setContext(qexec, i);
		try {
			long t1 = System.currentTimeMillis();
			ResultSet results = qexec.execSelect();
			long t2 = System.currentTimeMillis();
			System.out.println(t2 - t1);
			for (; results.hasNext();) {
				System.out.println(results.nextSolution().getLiteral("n"));

			}
		} finally {
			qexec.close();
		}

	}

	@Test
	public void testError() {

		Model m = ModelFactory.createDefaultModel();
		m.read("file:jenaext/capitals.rdf");
		IStorageManager store = SpatialIndex.createMemoryStorageManager();
		RTree rtree = new RTree(props(), store);
		Indexer i = new Indexer(rtree);
		i.createIndex(m);

		String queryString = "PREFIX : <http://www.geonames.org/ontology#>"
				+ "PREFIX geo: <java:org.geospatialweb.arqext.>\n\n "
				+ "SELECT ?n WHERE { ?s :name ?n . ?s geo:nearby(19 -99 10)  }";

		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, m);
		Geo.setContext(qexec, i);
		boolean caught = false;
		try {
			long t1 = System.currentTimeMillis();
			ResultSet results = qexec.execSelect();
			long t2 = System.currentTimeMillis();
			System.out.println(t2 - t1);
			for (; results.hasNext();) {
				System.out.println(results.nextSolution().getLiteral("n"));

			}

		} catch (Exception e) {
			caught = true;
		} finally {
			qexec.close();
		}
		assertTrue(caught);

	}

	private static PropertySet props() {
		PropertySet ps2 = new PropertySet();
		ps2.setProperty("Dimension", 2);
		return ps2;
	}
}
