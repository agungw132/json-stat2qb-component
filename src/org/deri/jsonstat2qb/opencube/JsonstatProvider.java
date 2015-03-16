package org.deri.jsonstat2qb.opencube;

public class JsonstatProvider extends AbstractFlexProvider<TarqlProvider.Config> {
	private static final long serialVersionUID = 1L;

	public static class Config implements Serializable {

		private static final long serialVersionUID = 1L;

	}

	@Override
	public void gather(final List<Statement> res) throws Exception {

		Config c = config;

	}

	@Override
	public Class<? extends Config> getConfigClass() {
		return Config.class;
	}

}
