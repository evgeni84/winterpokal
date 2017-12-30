package de.ea.winterpokal.persistence.remote;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.util.Log;

import com.google.gson.Gson;

import de.ea.winterpokal.App;
import de.ea.winterpokal.model.WPTeam;
import de.ea.winterpokal.model.WPUser;
import de.ea.winterpokal.persistence.IUserDAO;
import de.ea.winterpokal.utils.web.Constants;
import de.ea.winterpokal.utils.web.RemoteRequest;
import de.ea.winterpokal.utils.web.RequestType;

public class UserRemoteDAO implements IUserDAO {

	public WPUser get(int id) {
		String responseString = RemoteRequest.DoRequest(Constants.HOST_NAME + "users/get/" + id + ".json", RequestType.GET);

		Gson gson = new Gson();
		UserResponse response = null;
		try {
			response = gson.fromJson(responseString, UserResponse.class);
		}catch (Exception ex) {
			Log.e("UserRetrieveFailed", "ParseError:" + ex.getMessage() );
		}

		if (response != null && "OK".equals(response.getStatus())) {
			UserData userData = response.getData();
			if(userData != null) {
				WPUser user = userData.getUser();
				user.setTeam(userData.getTeam());
				return user;
			} else {
				Log.e("UserRetrieveFailed", "NoUserData:"+responseString);
			}
		} else {
			Log.e("UserRetrieveFailed", responseString+ "");
		}

		return null;
	}

	public List<WPUser> find(String nameOrNamepart, int limit) {
		ArrayList<WPUser> users = new ArrayList<WPUser>();
		HashMap<String, String> args = new HashMap<String, String>();

		args.put("query", URLEncoder.encode(nameOrNamepart));
		args.put("limit", Math.min(limit, 50) + "");

		String responseString = RemoteRequest.DoRequest(Constants.HOST_NAME + "users/search.json", args, RequestType.GET);

		Gson gson = new Gson();
		UserListResponse response = gson.fromJson(responseString, UserListResponse.class);

		if ("OK".equals(response.getStatus())) {
			for (UserData d : response.getData()) {
				users.add(d.getUser());
			}
		} else {
			Log.e("UserFindFailed", responseString);
		}
		return users;
	}

	public static WPUser getMe() {
		String responseString = RemoteRequest.DoRequest(Constants.HOST_NAME + "users/me.json", RequestType.GET);
		if(responseString!=null) {
			Gson gson = new Gson();

			UserResponse response = null;
			try {
			 	response= gson.fromJson(responseString, UserResponse.class);
			} catch(Exception ex) {
				Log.e("UserRetrieveFailed", "parseResonseFailed:" + ex.getMessage());
			}
			if (response!=null && "OK".equals(response.getStatus())) {
				UserData data = response.getData();
				if(data!=null) {
					WPUser user = data.getUser();
					user.setTeam(data.getTeam());
					if (user.getTeam() != null) {
						WPTeam team = App.getInstance().getDAOFactory().getTeamDAO().get(user.getTeam().getId());
						user.setTeam(team);
					}
					return user;
				} else {
					Log.e("UserRetrieveFailed", "noValidData:"+responseString);
				}
			} else {
				Log.e("UserRetrieveFailed", responseString);
			}
		}else {
			Log.e("UserRetrieveFailed","NoResponse");
		}
		return null;
	}

	public boolean addAsFavorite(WPTeam team) {

		HashMap<String, String> postData = new HashMap<String, String>();
		postData.put("team-id", team.getId() + "");

		String responseString = RemoteRequest.DoRequest(Constants.HOST_NAME + "faforites/add.json", postData, RequestType.POST);

		Gson gson = new Gson();
		UserResponse response = gson.fromJson(responseString, UserResponse.class);

		if ("OK".equals(response.getStatus())) {
			WPUser user = response.getData().getUser();
			user.setTeam(response.getData().getTeam());
			return true;
		} else {
			Log.e("UserRetrieveFailed", responseString);
			return false;
		}
	}

	public boolean removeFavorite(WPTeam team) {
		// TODO Auto-generated method stub
		return false;
	}
}
