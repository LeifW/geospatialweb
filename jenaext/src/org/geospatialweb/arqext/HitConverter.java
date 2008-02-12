/**
 * 
 */
package org.geospatialweb.arqext;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.engine.binding.BindingMap;
import com.hp.hpl.jena.util.iterator.Map1;

class HitConverter implements Map1 {
	private Binding binding;
	private Var match;

	HitConverter(Binding binding, Var matchVar) {			
		this.binding = binding;
		this.match = matchVar;
	}

	public Object map1(Object thing) {
		String uri = (String) thing;
		Binding b = new BindingMap(binding);
		b.add(match, Node.createURI(uri));
		return b;
	}

}