package org.deri.jsonstat2qb.opencube;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.deri.jsonstat2qb.jsonstat2qb;
import org.deri.jsonstat2qb.opencube.ui.EncodingSelectValueFactory;
import org.deri.jsonstat2qb.opencube.ui.LanguageSelectValueFactory;
import org.openrdf.model.Statement;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.semarglproject.vocab.XSD;

import com.fluidops.iwb.model.ParameterConfigDoc;
import com.fluidops.iwb.model.ParameterConfigDoc.Type;
import com.fluidops.iwb.model.TypeConfigDoc;
import com.fluidops.iwb.provider.AbstractFlexProvider;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.StmtIterator;

@TypeConfigDoc("The JSON-stat2qb Data Provider enables the generation of an RDF Data Cube from JSON-stat data source.")
public class JsonstatProvider extends AbstractFlexProvider<JsonstatProvider.Config> {
	private static final long serialVersionUID = 1L;

	private static final String DEFAULT_BASE_URI = "http://example.com/";

	public static class Config implements Serializable {

		private static final long serialVersionUID = 1L;

		@ParameterConfigDoc(
				desc = "The location of source JSON-stat file. Please provide either a remote source (http://, https://, or file:) or upload *.json file",
				type=Type.FILEEDITOR,
				required=true)
		public String jsonFileLocation;

		@ParameterConfigDoc(
				desc = "Base URI",
				type = Type.SIMPLE,
				defaultContent = DEFAULT_BASE_URI)
		public String systemBaseURI;


		@ParameterConfigDoc(
				desc = "Data language (used for label)",
				required = false,
				type = Type.DROPDOWN,
				selectValuesFactory = LanguageSelectValueFactory.class)
		public String lang;

		@ParameterConfigDoc(
				desc = "File's character encoding",
				required = false,
				type = Type.DROPDOWN,
				selectValuesFactory = EncodingSelectValueFactory.class)
		public String encoding;

//		@ParameterConfigDoc(
//				desc="The location of source. Please provide either a remote source (http://, https://, or file:) or upload *.json file.", 
//				required=true )
//		@FileSelector(
//				fileType=FileType.FILE, 
//				allowMultiFileUpload=false)
//		public String location_tmp;

	}

	@Override
	public Class<? extends Config> getConfigClass() {
		return Config.class;
	}

	@Override
	public void gather(List<Statement> res) throws Exception {

		Config c = config;

		Model model = null;

		// BaseUri
		if (StringUtils.isNotBlank(c.systemBaseURI) ) {
			jsonstat2qb.setBaseUri(c.systemBaseURI);
		}

		// Language
		if (StringUtils.isBlank(c.lang) ) {
			c.lang = "en";
		}

		// Encoding
		if (StringUtils.isNotBlank(c.encoding) && ( ! c.encoding.equals("Autodetect")) ) {
			// jsonstat2qb.setEncoding(c.encoding);
		}

		try {

			// jsonFileLocation
			if (StringUtils.isNotBlank(c.jsonFileLocation) ) {

				model = jsonstat2qb.jsonstat2qb(c.jsonFileLocation);

				StmtIterator iter = model.listStatements();

				ValueFactory factory = new ValueFactoryImpl();

				while (iter.hasNext()) {

					Triple t = iter.nextStatement().asTriple();

					Node n = t.getObject();
					Statement st;

					if (t.getSubject().isURI()) {
						if (n.isLiteral()) {

							try {
								Double.parseDouble( n.getLiteralValue().toString() );
								st = factory.createStatement(
										factory.createURI(t.getSubject().getURI()),
										factory.createURI(t.getPredicate().getURI()),
										factory.createLiteral( n.getLiteralValue().toString(), XSD.DOUBLE ));
							} catch (Exception e) {
								st = factory.createStatement(
										factory.createURI(t.getSubject().getURI()),
										factory.createURI(t.getPredicate().getURI()),
										factory.createLiteral( n.getLiteralValue().toString(), c.lang ));
							}

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
		    System.out.println("Error: " + e.getMessage());
		}

	}

}
