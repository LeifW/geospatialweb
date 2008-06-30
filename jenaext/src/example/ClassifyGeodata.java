package example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;

public class ClassifyGeodata {
	static String AIRPORT = "AIRP";
	static String HOTEL = "HTL";
	
	public static OntModel getModel(String filepath) throws IOException {
		String geo = "http://www.w3.org/2003/01/geo/wgs84_pos#";
		String base = "http://example.org/";

		OntModel m = ModelFactory.createOntologyModel();

		OntClass city = m.createClass(base + "City");
		OntClass hotel = m.createClass(base + "Hotel");
		OntClass airport = m.createClass(base + "Airport");

		Property plat = m.createProperty(geo + "lat");
		Property plon = m.createProperty(geo + "long");
		Property pname = m.createProperty(base + "name");

		FileReader file = new FileReader(filepath);

		BufferedReader reader = new BufferedReader(file);
		reader.readLine();

		for (String line; (line = reader.readLine()) != null;) {
			String[] values = line.split("\t");
			String id = values[0];
			String name = values[1];
			double lat = Double.parseDouble(values[4]);
			double lon = Double.parseDouble(values[5]);
			String subtype = values[7];
			int population = Integer.parseInt( values[14] );

				
			Individual r = null;
			if (subtype.startsWith("PPL") && population > 0) {
				r = city.createIndividual(base + id);
				//System.out.println(population);
			} else if (HOTEL.equals(subtype)) {
				r = hotel.createIndividual(base + id);
			} else if (AIRPORT.equals(subtype)) {
				r = airport.createIndividual(base + id);
			} else {
				continue;
			}
			
			r.addProperty(plat, m.createTypedLiteral(lat));
			r.addProperty(plon, m.createTypedLiteral(lon));
			r.addProperty(pname, name);

		} 
		

		return m;
	}
	
}
