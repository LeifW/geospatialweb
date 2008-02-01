/**
 * You'll need more that just the libs in jenalib.  jenalib has the bare minimum to
 * compile, however, to run this you need the full ARQ distribution set of libraries.
 * 
 */
import spatialindex.SpatialIndex;
import spatialindex.rtree.RTree;
import spatialindex.storagemanager.IStorageManager;
import spatialindex.storagemanager.PropertySet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.geospatialweb.arqext.Geo;
import org.geospatialweb.arqext.Indexer;



import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class TestBasic {
	
	public static void main(String[] args) throws IOException {
		TestBasic t = new TestBasic();
		t.england();
	}
	

	public void england() throws IOException {
		System.out.println("Loading large file into jena model...");
		String geo = "http://www.w3.org/2003/01/geo/wgs84_pos#";
		String base = "http://example.org/";

		OntModel m = ModelFactory.createOntologyModel();
		OntClass city =  m.createClass(base + "City");
		OntClass other =  m.createClass(base + "Other");
		
		FileReader file = new FileReader("GB.txt");
		BufferedReader reader = new BufferedReader(file);
		reader.readLine();
		for (String line; (line = reader.readLine()) != null;) {
			String[] values = line.split("\t");
			String id = values[0];
			String name = values[1];
			double lat = Double.parseDouble(values[4]);
			double lon = Double.parseDouble(values[5]);
			String type = values[6];

			Resource r = ("P".equals(type)) ?
					m.createIndividual(base + id, city):
					m.createIndividual(base + id, other);
			Property plat = m.createProperty(geo + "lat");
			Property plon = m.createProperty(geo + "long");
			Property pname = m.createProperty(base + "name");
			r.addProperty(plat, m.createTypedLiteral(lat));
			r.addProperty(plon, m.createTypedLiteral(lon));
			r.addProperty(pname, name);

		}
		//m.write(System.out);
		
		IStorageManager store = SpatialIndex.createMemoryStorageManager();
		RTree rtree = new RTree(props(), store);
		Indexer i = new Indexer(rtree);
		i.createIndex(m);

		String queryString = 
			"PREFIX : <http://example.org/>" +	
			"PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>\n\n " +
			"SELECT ?n ?lat ?lon WHERE { ?s a :City . ?s :name ?n . ?s geo:lat ?lat . ?s geo:long ?lon}";

		// First get a list of cities to find nearby things
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, m);
		try {
			ResultSet results = qexec.execSelect();
			for (;results.hasNext();) {
				QuerySolution soln = results.nextSolution(); soln.
				double lat =  soln.getLiteral("lat").getDouble();
				double lon = soln.getLiteral("lon").getDouble();
				String place = soln.getLiteral("n").getString();				
				findNearbyStuff(m, i, place, lat, lon);
			}
		} finally {
			qexec.close();
		}
		

		

	}

	private void findNearbyStuff(OntModel m, Indexer i, String city, double lat, double lon) {
		String queryString = 
			"PREFIX : <http://example.org/>" +			
			"PREFIX geo: <java:org.geospatialweb.arqext.>\n\n " +
			"SELECT ?n WHERE { ?s geo:nearby(" + lat + " " + lon + " 10) . ?s a :Other . ?s :name ?n }";

		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, m);
		Geo.setContext(qexec, i);
		try {
			long t1 = System.currentTimeMillis();
			ResultSet results = qexec.execSelect();
			long t2 = System.currentTimeMillis();
			System.out.println("Query complete in " + (t2-t1) + "ms");
			System.out.println("Places near " + city);
			for (;results.hasNext();) {
				System.out.println("\t" + results.nextSolution().getLiteral("n"));				
			}
		} finally {
			qexec.close();
		}
	}

	public void test() {
		
		Model m = ModelFactory.createDefaultModel();
		m.read("file:capitals.rdf");
		IStorageManager store = SpatialIndex.createMemoryStorageManager();
		RTree rtree = new RTree(props(), store);
		Indexer i = new Indexer(rtree);
		i.createIndex(m);

		
		String queryString = 
			"PREFIX : <http://www.geonames.org/ontology#>" +			
			"PREFIX geo: <java:org.geospatialweb.arqext.>\n\n " +
			"SELECT ?n WHERE { ?s geo:nearby(19 -99 10) . ?s :name ?n }";

		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, m);
		Geo.setContext(qexec, i);
		try {
			long t1 = System.currentTimeMillis();
			ResultSet results = qexec.execSelect();
			long t2 = System.currentTimeMillis();
			System.out.println(t2-t1);
			for (;results.hasNext();) {
				System.out.println(results.nextSolution().getLiteral("n"));
				
			}
		} finally {
			qexec.close();
		}

	}
	
	private static PropertySet props() {
		PropertySet ps2 = new PropertySet();
		Double f = new Double(0.7);
		ps2.setProperty("FillFactor", f);
		ps2.setProperty("IndexCapacity", 20);
		ps2.setProperty("LeafCapacity", 20);
			// Index capacity and leaf capacity may be different.
		ps2.setProperty("Dimension", 2);
		return ps2;
	}	
}
