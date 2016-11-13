package de.ea.winterpokal;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Random;

import android.app.Activity;
import android.util.Log;

public class DefaultExceptionHandler implements UncaughtExceptionHandler {

	private Activity activity;
	private UncaughtExceptionHandler defaultExceptionHandler;
	private static final String TAG = "UNHANDLED_EXCEPTION";

	// constructor
	public DefaultExceptionHandler(Activity activity, UncaughtExceptionHandler defaultExceptionHandler) {
		this.defaultExceptionHandler = defaultExceptionHandler;
		this.activity = activity;
	}

	public void uncaughtException(Thread t, Throwable e) {
		Log.e("Error", e.getMessage(), e);

		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		e.printStackTrace(printWriter);
		try {
			// Random number to avoid duplicate files
			Random generator = new Random();
			int random = generator.nextInt(99999);
			// Embed version in stacktrace filename
			String filename = "wp" + "-" + Integer.toString(random);
			// Write the stacktrace to disk
			BufferedWriter bos = new BufferedWriter(new FileWriter(G.FILES_PATH + "/" + filename + ".stacktrace"));
			bos.write(G.ANDROID_VERSION + "\n");
			bos.write(G.PHONE_MODEL + "\n");
			bos.write(result.toString());
			bos.flush();
			// Close up everything
			bos.close();
		} catch (Exception ebos) {
			// Nothing much we can do about this - the game is over
			ebos.printStackTrace();
		}

		defaultExceptionHandler.uncaughtException(t, e);
	}

}
