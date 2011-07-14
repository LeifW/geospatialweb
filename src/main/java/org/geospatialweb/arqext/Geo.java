package org.geospatialweb.arqext;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.sparql.ARQConstants;
import com.hp.hpl.jena.sparql.util.Symbol;

/**
 * @author Taylor Cowan
 */
public class Geo {
	// The symbol used to register the index in the query context
    public static final Symbol indexKey = ARQConstants.allocSymbol("geospatial") ;
    
    public static void setContext(QueryExecution qexec, Indexer i) {
    	qexec.getContext().set(Geo.indexKey, i);
    }

}
