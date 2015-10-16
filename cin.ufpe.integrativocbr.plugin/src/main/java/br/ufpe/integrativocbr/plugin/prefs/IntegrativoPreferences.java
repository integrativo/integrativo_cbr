package br.ufpe.integrativocbr.plugin.prefs;


public class IntegrativoPreferences {

	private static IntegrativoPreferences instance;
	private static final String SET = "br.ufpe.integrativocbr.plugin";
	private static final String DATABASES = "DATABASES";
	private static final String LIST = "LIST";
	
	public static synchronized IntegrativoPreferences getInstance() {
		if (instance == null) {
			instance = new IntegrativoPreferences();
		}
		return instance;
	}
	
}
