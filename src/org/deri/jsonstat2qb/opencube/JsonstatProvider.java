package org.deri.opencube.jsonstat2qb;

import java.io.Serializable;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import org.openrdf.model.Statement;

import com.fluidops.iwb.model.ParameterConfigDoc;
import com.fluidops.iwb.model.ParameterConfigDoc.Type;
import com.fluidops.iwb.provider.AbstractFlexProvider;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

public class JsonstatProvider extends AbstractFlexProvider<JsonstatProvider.Config> {
	private static final long serialVersionUID = 1L;

	public static class Config implements Serializable {

		private static final long serialVersionUID = 1L;

		@ParameterConfigDoc(desc = "URL of the input JSON-stat file. Use file:// URLs for local files.", type=Type.FILEEDITOR)
		public String jsonFileLocation;

	}

	@Override
	public Class<? extends Config> getConfigClass() {
		return Config.class;
	}

	@Override
	public void gather(List<Statement> res) throws Exception {
		
	}

}
