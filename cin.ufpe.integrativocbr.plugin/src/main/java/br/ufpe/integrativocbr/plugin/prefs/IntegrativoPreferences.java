package br.ufpe.integrativocbr.plugin.prefs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;

public class IntegrativoPreferences {

	private static IntegrativoPreferences instance;
	private static final String SET = "br.ufpe.integrativocbr.plugin";
	private static final String DATABASES = "DATABASES";

	public static synchronized IntegrativoPreferences getInstance() {
		if (instance == null) {
			instance = new IntegrativoPreferences();
		}
		return instance;
	}
	
	private Preferences getDatabasesPrefs() {
		return PreferencesManager.getInstance().getPreferencesForSet(DATABASES, SET);
	}
	
	@SuppressWarnings("unchecked")
	public List<DatabasePreference> readDatabasePrefsList() {
		byte[] byteIn = getDatabasesPrefs().getByteArray(DATABASES, null);
		if (byteIn != null) {
			try {
				ObjectInputStream objectIn = new ObjectInputStream(new ByteArrayInputStream(byteIn));
				return (List<DatabasePreference>) objectIn.readObject();
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return new ArrayList<DatabasePreference>();
	}
	
	public void writeDatabasePrefsList(List<DatabasePreference> prefs) {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		try {
			ObjectOutputStream objectOut = new ObjectOutputStream(byteOut);
			objectOut.writeObject(prefs);
			objectOut.close();
			getDatabasesPrefs().putByteArray(DATABASES, byteOut.toByteArray());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
