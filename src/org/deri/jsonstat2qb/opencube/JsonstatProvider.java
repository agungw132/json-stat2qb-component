package org.deri.jsonstat2qb.opencube;

import java.io.Serializable;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.deri.jsonstat2qb.opencube.ui.EncodingSelectValueFactory;
import org.deri.jsonstat2qb.jsonstat2qb;

import org.openrdf.model.Statement;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

import com.fluidops.iwb.model.ParameterConfigDoc;
import com.fluidops.iwb.model.ParameterConfigDoc.Type;
import com.fluidops.iwb.provider.AbstractFlexProvider;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class JsonstatProvider extends AbstractFlexProvider<JsonstatProvider.Config> {
	private static final long serialVersionUID = 1L;

	private static final String DEFAULT_BASE_URI = "http://example.com/default-dataset";

	public static class Config implements Serializable {

		private static final long serialVersionUID = 1L;

		@ParameterConfigDoc(
				desc = "URL of the input JSON-stat file. Use file:// URLs for local files.",
				type=Type.FILEEDITOR)
		public String jsonFileLocation;

		@ParameterConfigDoc(
				desc = "Base URI",
				type = Type.SIMPLE,
				defaultContent = DEFAULT_BASE_URI)
		public String systemBaseURI;

		@ParameterConfigDoc(
				desc = "The CSV file's character encoding",
				required = false,
				type = Type.DROPDOWN,
				selectValuesFactory = EncodingSelectValueFactory.class)
		public String encoding;
		
	}

	@Override
	public Class<? extends Config> getConfigClass() {
		return Config.class;
	}

	@Override
	public void gather(List<Statement> res) throws Exception {

		Config c = config;

		jsonstat2qb converter = new jsonstat2qb(null);

		Model model = null;

		// BaseUri
		if (StringUtils.isNotBlank(c.systemBaseURI) ) {
			converter.setBaseUri(c.systemBaseURI);
		}

		// Encoding
		if (StringUtils.isNotBlank(c.encoding) && ( ! c.encoding.equals("Autodetect")) ) {
			converter.setEncoding(c.encoding);
		}
		
		try {

			// jsonFileLocation
			if (StringUtils.isNotBlank(c.jsonFileLocation) ) {
				// TODO separate function
				model = converter.jsonstat2qb(c.jsonFileLocation);
				
				StmtIterator iter = model.listStatements();

				ValueFactory factory = new ValueFactoryImpl();
				
				int counter = 1;
				 
				while (iter.hasNext()) {
					
					Triple t = iter.nextStatement().asTriple();

					Node n = t.getObject();
					Statement st;

					if (t.getSubject().isURI()) {
						if (n.isLiteral()) {
							// TODO Literals are treated as strings (no datatypes
							// retained)
							st = factory.createStatement(
									factory.createURI(t.getSubject().getURI()),
									factory.createURI(t.getPredicate().getURI()),
									factory.createLiteral(n.toString()));
						} else if (n.isURI()) {
							st = factory.createStatement(
									factory.createURI(t.getSubject().getURI()),
									factory.createURI(t.getPredicate().getURI()),
									factory.createURI(n.toString()));
						} else {
							st = factory.createStatement(
									factory.createURI(t.getSubject().getURI()),
									factory.createURI(t.getPredicate().getURI()),
									factory.createBNode(n.toString()));
						}
					} else {
						// subject is a blank node
						if (n.isLiteral()) {
							// TODO Literals are treated as strings (no datatypes retained)
							st = factory.createStatement(
									factory.createBNode(t.getSubject().toString()),
									factory.createURI(t.getPredicate().getURI()),
									factory.createLiteral(n.toString()));
						} else if (n.isURI()) {
							st = factory.createStatement(
									factory.createBNode(t.getSubject().toString()),
									factory.createURI(t.getPredicate().getURI()),
									factory.createURI(n.toString()));
						} else {
							st = factory.createStatement(
									factory.createBNode(t.getSubject().toString()),
									factory.createURI(t.getPredicate().getURI()),
									factory.createBNode(n.toString()));
						}
					}
					res.add(st);
				}

			}
			
		} catch (Exception e) {
			// don nothing
		}

	}

}
