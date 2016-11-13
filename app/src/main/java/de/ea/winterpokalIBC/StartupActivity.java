package de.ea.winterpokalIBC;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import de.ea.winterpokalIBC.utils.web.auth.Auth;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

public class StartupActivity extends Activity {
	public static final Class<? extends Activity> loggedInActivityClazz = MainActivity.class;

	private void disableStrictMode() {
		// StrictMode.ThreadPolicy policy = new
		// StrictMode.ThreadPolicy.Builder().permitAll().build();
		// StrictMode.setThreadPolicy(policy);

		try {
			Class<?> strictModeClass = Class.forName("android.os.StrictMode", true, Thread.currentThread().getContextClassLoader());
			Class<?> threadPolicyClass = Class.forName("android.os.StrictMode$ThreadPolicy", true, Thread.currentThread().getContextClassLoader());
			Class<?> threadPolicyBuilderClass = Class.forName("android.os.StrictMode$ThreadPolicy$Builder", true, Thread.currentThread().getContextClassLoader());

			Method setThreadPolicyMethod = strictModeClass.getMethod("setThreadPolicy", threadPolicyClass);

			Method detectAllMethod = threadPolicyBuilderClass.getMethod("detectAll");
			Method penaltyMethod = threadPolicyBuilderClass.getMethod("penaltyLog");
			Method buildMethod = threadPolicyBuilderClass.getMethod("build");

			Constructor<?> threadPolicyBuilderConstructor = threadPolicyBuilderClass.getConstructor();
			Object threadPolicyBuilderObject = threadPolicyBuilderConstructor.newInstance();

			Object obj = detectAllMethod.invoke(threadPolicyBuilderObject);

			obj = penaltyMethod.invoke(obj);
			Object threadPolicyObject = buildMethod.invoke(obj);
			setThreadPolicyMethod.invoke(strictModeClass, threadPolicyObject);
		} catch (Exception ex) {
			Log.w("disableStrictMode", ex);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (android.os.Build.VERSION.SDK_INT > 9) {
			disableStrictMode();
		}

		final Class<? extends Activity> activityClass;
		if (!App.getInstance().isOnline()) {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(StartupActivity.this);
			// set title
			alertDialogBuilder.setTitle(R.string.error);
			alertDialogBuilder.setMessage(R.string.noConnection).setCancelable(false).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});
			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();
			// show it
			alertDialog.show();
			return;
		}

		if (Auth.isLoggedIn())
			activityClass = loggedInActivityClazz;
		else
			activityClass = LoginActivity.class;

		Intent newActivity = new Intent(this, activityClass);
		this.startActivity(newActivity);

		Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(this, Thread.getDefaultUncaughtExceptionHandler()));
	}

	@Override
	protected void onResume() {
		super.onResume();
		final Class<? extends Activity> activityClass;

		if (!App.getInstance().isOnline()) {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(StartupActivity.this);
			// set title
			alertDialogBuilder.setTitle(R.string.error);
			alertDialogBuilder.setMessage(R.string.noConnection).setCancelable(false).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});
			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();
			// show it
			alertDialog.show();
			return;
		}

		if (Auth.isLoggedIn())
			activityClass = loggedInActivityClazz;
		else
			activityClass = LoginActivity.class;

		Intent newActivity = new Intent(this, activityClass);
		newActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivity(newActivity);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Handle the back button
		if (keyCode == KeyEvent.KEYCODE_BACK && isTaskRoot()) {
			finish();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	public void onBackPressed() {
		this.finish();
	}
}
