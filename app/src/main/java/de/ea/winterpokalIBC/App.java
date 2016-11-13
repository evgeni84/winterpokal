package de.ea.winterpokalIBC;

import de.ea.winterpokalIBC.persistence.DAOFactory;
import de.ea.winterpokalIBC.persistence.DatabaseHelper;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;

public class App extends Application {

	private static Context context;
	private static App instance;
	private static DAOFactory daoFactory;

	public void onCreate() {
		super.onCreate();
		App.instance = this;
		App.context = getApplicationContext();

		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo pi;
			// Version
			pi = pm.getPackageInfo(context.getPackageName(), 0);
			G.APP_VERSION = pi.versionName;
			// Package name
			G.APP_PACKAGE = pi.packageName;
			// Files dir for storing the stack traces
			G.FILES_PATH = context.getFilesDir().getAbsolutePath();
			// Device model
			G.PHONE_MODEL = android.os.Build.MODEL;
			// Android version
			G.ANDROID_VERSION = android.os.Build.VERSION.RELEASE;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static Context getAppContext() {
		return App.context;
	}

	private static DatabaseHelper helper = null;

	public static DatabaseHelper getHelper() {
		if (helper == null)
			helper = new DatabaseHelper(context);
		return helper;
	}

	public static App getInstance() {
		return instance;
	}

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
	}

	public DAOFactory getDAOFactory() {
		if (daoFactory == null)
			daoFactory = DAOFactory.instance(DAOFactory.REMOTE);
		return daoFactory;

	}
}
