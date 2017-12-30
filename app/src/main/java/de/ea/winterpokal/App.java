package de.ea.winterpokal;

import de.ea.winterpokal.persistence.DAOFactory;
import de.ea.winterpokal.persistence.DatabaseHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;

public class App extends Application    {

	private static Context context;
	private static App instance;
	private static DAOFactory daoFactory;
	private  LifeCycleManager lifeCycleManager;

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
			lifeCycleManager = new LifeCycleManager();
			registerActivityLifecycleCallbacks(lifeCycleManager);
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


	class LifeCycleManager implements Application.ActivityLifecycleCallbacks {
		@Override
		public void onActivityCreated(final Activity activity, Bundle savedInstanceState) {


		}

		@Override
		public void onActivityStarted(final Activity activity) {
			if (!App.getInstance().isOnline()) {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
				// set title
				alertDialogBuilder.setTitle(R.string.error);
				alertDialogBuilder.setMessage(R.string.noConnection).setCancelable(false).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						activity.finish();
					}
				});
				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();
				// show it
				alertDialog.show();
				return;
			}
		}

		@Override
		public void onActivityResumed(Activity activity) {

		}

		@Override
		public void onActivityPaused(Activity activity) {

		}

		@Override
		public void onActivityStopped(Activity activity) {

		}

		@Override
		public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

		}

		@Override
		public void onActivityDestroyed(Activity activity) {

		}
	}
}
