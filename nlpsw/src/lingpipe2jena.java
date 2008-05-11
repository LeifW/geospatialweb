import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;


public class lingpipe2jena {
	public static void main(String[] args){
		Model m = ModelFactory.createDefaultModel();
		
		Resource breck = m.createResource("http://wwww.alias.com/breck");
		Resource person = m.createResource("http://wwww.alias.com/person");
		Resource human = m.createResource("http://wwww.alias.com/human");
		Resource male_pronoun = m.createResource("http://wwww.alias.com/male_promoun");
		
		person.addProperty(RDF.type, RDFS.Class);
		person.addProperty(RDFS.subClassOf, human);
		male_pronoun.addProperty(RDFS.subClassOf, person);
		
		breck.addProperty(RDF.type, person);
		
		m.write(System.out	);
		
	
	}
}
