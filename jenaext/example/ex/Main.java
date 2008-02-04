package ex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.geospatialweb.arqext.Geo;
import org.geospatialweb.arqext.Indexer;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;

import spatialindex.SpatialIndex;
import spatialindex.rtree.RTree;
import spatialindex.storagemanager.IStorageManager;
import spatialindex.storagemanager.PropertySet;
import static java.lang.System.out;

public class Main {

	public static void main(String[] args) throws IOException {
		OntModel m = ModelFactory.createOntologyModel();
		convert(m, new File("jenaext/GB.txt"));
		
		// create an rtree index (external to ARQ) of all data
		// tagged with wgs84_pos
		IStorageManager store = SpatialIndex.createMemoryStorageManager();
		RTree rtree = new RTree(props(), store);
		Indexer i = new Indexer(rtree);
		i.createIndex(m);
		
		out.println("ready...");
		String queryString = 
			"PREFIX : <http://example.org/>" +			
			"PREFIX geo: <java:org.geospatialweb.arqext.>\n\n " +
			"SELECT ?n WHERE { ?s geo:nearby(51.45 -2.5833333 30) . ?s a :Hotel . ?s :name ?n }";

		out.println("hotels near Bristol...");
		runQuery(m, i, queryString);		

		queryString = 
			"PREFIX : <http://example.org/>" +			
			"PREFIX geo: <java:org.geospatialweb.arqext.>\n\n " +
			"SELECT ?n WHERE { ?s geo:nearby(51.45 -2.5833333 30) . ?s a :City . ?s :name ?n }";
		out.println("cities near Bristol...");
		runQuery(m, i, queryString);		

		queryString = 
			"PREFIX : <http://example.org/>" +			
			"PREFIX geo: <java:org.geospatialweb.arqext.>\n\n " +
			"SELECT ?n WHERE { ?s geo:within(51.44 -2.72 51.52 -2.45 ) . ?s a :City . ?s :name ?n }";
		out.println("cities within bouding box...");
		runQuery(m, i, queryString);				
		
	}



	private static void runQuery(OntModel m, Indexer i, String queryString) {
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, m);
		// apply the index to the query's context
		Geo.setContext(qexec, i);
		try {
			ResultSet results = qexec.execSelect();
			for (;results.hasNext();) out.println(results.nextSolution().getLiteral("n"));				
		} finally {
			qexec.close();
		}
	}
	
	
	
	private static PropertySet props() {
		PropertySet ps2 = new PropertySet();
		ps2.setProperty("Dimension", 2);
		return ps2;
	}	
	
	public static void convert(OntModel m, File data) throws IOException {
		String geo = "http://www.w3.org/2003/01/geo/wgs84_pos#";
		String base = "http://example.org/";

		OntClass city =  m.createClass(base + "City");
		OntClass hotel =  m.createClass(base + "Hotel");
		Property plat = m.createProperty(geo + "lat");
		Property plon = m.createProperty(geo + "long");
		Property pname = m.createProperty(base + "name");

		
		FileReader file = new FileReader(data);
		BufferedReader reader = new BufferedReader(file);
		reader.readLine();
		for (String line; (line = reader.readLine()) != null;) {
			String[] values = line.split("\t");
			String id = values[0];
			String name = values[1];
			double lat = Double.parseDouble(values[4]);
			double lon = Double.parseDouble(values[5]);
			String type = values[6];
			String subtype = values[7];
			Individual r=null;
			if ( subtype.startsWith("PPL")) {
				r = city.createIndividual(base + id);
			} else if ("HTL".equals(subtype)) {
				r = hotel.createIndividual(base + id);				
			} else {
				continue;
			}
			r.addProperty(plat, m.createTypedLiteral(lat));
			r.addProperty(plon, m.createTypedLiteral(lon));
			r.addProperty(pname, name);	
		}
		file.close();
			
	}
}
