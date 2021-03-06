package de.ea.winterpokal.persistence;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import de.ea.winterpokal.model.WPEntry;
import de.ea.winterpokal.model.WPToken;

public class DatabaseConfigUtil extends OrmLiteConfigUtil {
	private static final Class<?>[] classes = new Class[] { WPEntry.class,
			WPToken.class };

	public static void createConfigFile() throws Exception {
		writeConfigFile("ormlite_config.txt", classes);
	}
}
