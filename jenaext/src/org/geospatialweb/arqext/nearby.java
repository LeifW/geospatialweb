package org.geospatialweb.arqext;


import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.QueryExecException;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.ExecutionContext;
import com.hp.hpl.jena.sparql.engine.QueryIterator;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.engine.binding.BindingMap;
import com.hp.hpl.jena.sparql.engine.iterator.QueryIterNullIterator;
import com.hp.hpl.jena.sparql.engine.iterator.QueryIterPlainWrapper;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.pfunction.PropFuncArg;
import com.hp.hpl.jena.sparql.pfunction.PropFuncArgType;
import com.hp.hpl.jena.sparql.pfunction.PropertyFunctionEval;
import com.hp.hpl.jena.sparql.util.NodeFactory;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.Map1;
import com.hp.hpl.jena.util.iterator.Map1Iterator;

/**
 * 
 * @author Taylor Cowan
 */
public class nearby extends PropertyFunctionEval {

	private Node plat = Node.createURI("http://www.w3.org/2003/01/geo/wgs84_pos#lat");
	private Node plon = Node.createURI("http://www.w3.org/2003/01/geo/wgs84_pos#long");
	private int LIMIT = 100;
	
	public nearby() {
		super(PropFuncArgType.PF_ARG_EITHER, PropFuncArgType.PF_ARG_EITHER);
	}

	@Override
	public QueryIterator execEvaluated(Binding binding, PropFuncArg argSubject,
			Node arg2, PropFuncArg argObject, ExecutionContext ctx) {
		if ( argObject.isNode())
			return nearbyNode(binding, argSubject, argObject.getArg(), ctx, 100);
		else if (argObject.getArgListSize() == 2) {
			Node limit = argObject.getArg(1);
			return nearbyNode(binding, argSubject, argObject.getArg(0), ctx, asInteger(limit));
		}
		Node nlat = argObject.getArg(0);
		Node nlon = argObject.getArg(1);
		Node limit = argObject.getArg(2);
		return find(binding, argSubject.getArg(), ctx, asFloat(nlat), asFloat(nlon), asInteger(limit));
	}

	private QueryIterator nearbyNode(Binding binding, PropFuncArg argSubject, Node n,
			ExecutionContext ctx, int limit) {
		double lat = Double.MIN_VALUE;
		double lon = Double.MIN_VALUE;
		Graph g = ctx.getActiveGraph();
		ExtendedIterator it = g.find(n, plat, Node.ANY);
		if ( it.hasNext()) {
			Triple t = (Triple)it.next();
			lat = asFloat(t.getObject());
		}
		it = g.find(n, plon, Node.ANY);
		if ( it.hasNext()) {
			Triple t = (Triple)it.next();
			lon = asFloat(t.getObject());
		}
		if ( (lat != Double.MIN_VALUE) && (lon != Double.MIN_VALUE))
			return find(binding, argSubject.getArg(), ctx, lat, lon, limit);			
		return new QueryIterNullIterator(ctx);
	}

	private QueryIterator find(Binding binding, Node match,
			ExecutionContext ctx, double lat, double lon, int limit) {
		Indexer idx = (Indexer) ctx.getContext().get(Geo.indexKey);
		List<String> l = idx.getNearby(lat, lon, limit);
		if (! match.isVariable())
			throw new QueryExecException("nearby() must come first in your WHERE clause.");
		HitConverter cnv = new HitConverter(binding, Var.alloc(match));
		Iterator<?> iter = new Map1Iterator(cnv, l.iterator());
		return new QueryIterPlainWrapper(iter, ctx);
	}

	static private double asFloat(Node n) {
		if (n == null)
			return Float.MIN_VALUE;
		NodeValue nv = NodeValue.makeNode(n);
		if (nv.isFloat())
			return nv.getFloat();
		else if ( nv.isDouble())
			return nv.getDouble();
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
