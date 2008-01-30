package org.geospatialweb.arqext;


import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.ExecutionContext;
import com.hp.hpl.jena.sparql.engine.QueryIterator;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.engine.binding.BindingMap;
import com.hp.hpl.jena.sparql.engine.iterator.QueryIterPlainWrapper;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.pfunction.PropFuncArg;
import com.hp.hpl.jena.sparql.pfunction.PropFuncArgType;
import com.hp.hpl.jena.sparql.pfunction.PropertyFunctionEval;
import com.hp.hpl.jena.sparql.util.NodeFactory;
import com.hp.hpl.jena.util.iterator.Map1;
import com.hp.hpl.jena.util.iterator.Map1Iterator;

/**
 * 
 * @author Taylor Cowan
 */
public class nearby extends PropertyFunctionEval {

	public nearby() {
		super(PropFuncArgType.PF_ARG_EITHER, PropFuncArgType.PF_ARG_EITHER);
	}

	@Override
	public QueryIterator execEvaluated(Binding binding, PropFuncArg argSubject,
			Node arg2, PropFuncArg argObject, ExecutionContext ctx) {
		Node nlat = argObject.getArg(0);
		Node nlon = argObject.getArg(1);
		Node limit = argObject.getArg(2);
		Indexer idx = (Indexer) ctx.getContext().get(Geo.indexKey);
		List<String> l = idx.getNearby(asFloat(nlat), asFloat(nlon), asInteger(limit));
		Node match = argSubject.getArg();
		HitConverter cnv = new HitConverter(binding, Var.alloc(match));
		Iterator<?> iter = new Map1Iterator(cnv, l.iterator());
		return new QueryIterPlainWrapper(iter, ctx);
	}

	static private float asFloat(Node n) {
		if (n == null)
			return Float.MIN_VALUE;
		NodeValue nv = NodeValue.makeNode(n);
		if (nv.isFloat())
			return nv.getFloat();
		return Float.MIN_VALUE;
	}

	static class HitConverter implements Map1 {
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

	static private int asInteger(Node n) {
		return (n == null) ? Integer.MIN_VALUE:NodeFactory.nodeToInt(n);
	}
}
