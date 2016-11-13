package de.ea.winterpokal;

import java.sql.SQLException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

import de.ea.winterpokal.model.WPToken;
import de.ea.winterpokal.persistence.DatabaseHelper;
import de.ea.winterpokal.utils.web.auth.Auth;

public class LoginActivity extends OrmLiteBaseActivity<DatabaseHelper> implements OnClickListener {
	DatabaseHelper helper;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		helper = new DatabaseHelper(this);

		setContentView(R.layout.login);
		Button b = ((Button) findViewById(R.id.bLogin));
		b.setOnClickListener(this);
	}

	ProgressDialog progressDialog;

	public void onClick(View v) {
		new LoginTask(this).execute();

	}

	class LoginTask extends AsyncTask<Void, Void, Boolean> {

		private Activity context;

		public LoginTask(Activity context) {
			this.context = context;
		}

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(context, "", getString(R.string.loggingIn));
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (progressDialog != null)
				progressDialog.dismiss();
			progressDialog = null;
			super.onPostExecute(result);
		}

		@Override
		protected Boolean doInBackground(Void... arg0) {
			Activity act = context;
			EditText loginView = (EditText) act.findViewById(R.id.etLoginName);
			EditText passwordView = (EditText) act.findViewById(R.id.etLoginPassword);
			if (loginView != null && passwordView != null) {
				String login = loginView.getText().toString();
				String password = passwordView.getText().toString();
				WPToken token = Auth.retrieveToken(login, password);
				progressDialog.dismiss();
				if (token == null) {
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(App.getAppContext(), (CharSequence) "login failed", 1000).show();
						}
					});
				} else {
					boolean tokenSaved = false;
					try {
						helper.getTokenDao().createOrUpdate(token);
						tokenSaved = true;
					} catch (SQLException e) {
						Log.e("", "errorSaveToken", e);
					}
					if (!tokenSaved) {
						Auth.setEnforcedTempToken(token);
					}
					Intent in = new Intent(act, StartupActivity.loggedInActivityClazz);
					in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
					act.startActivity(in);

				}

			}
			return true;

		}
	}
}
