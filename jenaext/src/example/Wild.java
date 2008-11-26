package example;

import org.geospatialweb.arqext.Geo;
import org.geospatialweb.arqext.Indexer;
import spatialindex.SpatialIndex;
import spatialindex.rtree.RTree;
import spatialindex.storagemanager.PropertySet;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * Code found in the wild.
 *
 */
public class Wild {
    public static void main(String... argv) {
 
 
        try {
            final RTree rtree = new RTree(props(), SpatialIndex
                    .createMemoryStorageManager());
            final Indexer i = new Indexer(rtree);           
            Model m = ModelFactory.createOntologyModel();
            m.read("file:jenaext/src/example/test.rdf");
            i.createIndex(m);        
            final Query q = QueryFactory
                    .create("PREFIX geom:  <http://www.w3.org/2003/01/geo/wgs84_pos#> "
                            + "PREFIX owl:   <http://www.w3.org/2002/07/owl#> "
                            + "PREFIX geo: <java:org.geospatialweb.arqext.> "
                            + "select ?ouri "
                            + "WHERE "
                            + "{ ?ouri geo:nearby( 44.88 2.23 1) }"

                    );
            final QueryExecution qe = QueryExecutionFactory.create(q, m);
            Geo.setContext(qe, i);
            final ResultSet rs = qe.execSelect();
       
            ResultSetFormatter.out(System.out, rs, q);

        } catch (final Exception e) {
            e.printStackTrace();
        }
   
    }

    private static PropertySet props() {
        final PropertySet ps2 = new PropertySet();
        ps2.setProperty("Dimension", 2);
        return ps2;
    }

}
