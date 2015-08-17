package org.deri.jsonstat2qb.opencube;

import java.io.Serializable;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import org.deri.jsonstat2qb.opencube.ui.EncodingSelectValueFactory;

import org.openrdf.model.Statement;

import com.fluidops.iwb.model.ParameterConfigDoc;
import com.fluidops.iwb.model.ParameterConfigDoc.Type;
import com.fluidops.iwb.provider.AbstractFlexProvider;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

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
		
	}

}
