package de.ea.winterpokal.utils.web.auth;

import java.util.List;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;
import de.ea.winterpokal.App;
import de.ea.winterpokal.model.WPToken;
import de.ea.winterpokal.model.WPUser;
import de.ea.winterpokal.persistence.remote.Response;
import de.ea.winterpokal.persistence.remote.UserRemoteDAO;
import de.ea.winterpokal.utils.web.Constants;
import de.ea.winterpokal.utils.web.RemoteRequest;
import de.ea.winterpokal.utils.web.RequestType;

public class Auth {

	private static WPToken enforcedTempToken = null;

	public static void setEnforcedTempToken(WPToken token) {
		enforcedTempToken = token;
	}

	public static boolean isLoggedIn() {

		return getToken() != null;
	}

	private static volatile WPUser u;

	public static boolean logout() {
		return App.getHelper().ClearTokenTable();
	}

	public static WPUser getUser() {
		if (isLoggedIn()) {
			if (u == null) {
				try {
					u = new AuthAsyncTask().execute().get();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if (u != null)
					try {
						u.setAppUser(true);
						CreateOrUpdateStatus status = App.getHelper().getUserDao().createOrUpdate(u);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						Log.e("ErrorSaveUser", e.getMessage(), e);
					}
			}
			return u;
		} else
			return null;
	}

	public static WPToken getToken() {

		if (enforcedTempToken != null)
			return enforcedTempToken;

		try {

			List<WPToken> tokens = App.getHelper().getTokenDao().queryForAll();
			if (tokens.size() > 0)
				return tokens.get(0);
		} catch (SQLException e) {
			Log.e("getToken", Log.getStackTraceString(e));
		}

		return null;
	}

	public static WPToken retrieveToken(String userNameOrEmail, String password) {
		HashMap<String, String> args = new HashMap<String, String>();
		args.put("login", userNameOrEmail);
		args.put("password", password);

		String responseString = RemoteRequest.DoRequest(Constants.HOST_NAME + "tokens/get.json", args, RequestType.POST);

		Gson gson = new Gson();
		Response<Tokens> response = null;
		response = gson.fromJson(responseString, new TypeToken<Response<Tokens>>() {
		}.getType());

		if ("OK".equals(response.getStatus())) {
			List<WPToken> tokens = response.getData().getTokens();
			if (tokens == null || tokens.size() == 0)
				return null;
			return tokens.get(0);
		} else {
			System.out.println(responseString);
		}

		return null;
	}

	private class Tokens {
		private List<WPToken> tokens;

		public List<WPToken> getTokens() {
			return tokens;
		}

		public void setTokens(List<WPToken> tokens) {
			this.tokens = tokens;
		}

	}

	private static class AuthAsyncTask extends AsyncTask<Void,Void,WPUser>{

		@Override
		protected WPUser doInBackground(Void... params) {
			return UserRemoteDAO.getMe();
		}
		
	}
}
