package de.ea.winterpokal.persistence.remote;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import de.ea.winterpokal.model.WPTeam;
import de.ea.winterpokal.model.WPUser;
import de.ea.winterpokal.persistence.IFavoritesDAO;
import de.ea.winterpokal.persistence.remote.model.TeamObject;

import de.ea.winterpokal.utils.web.Constants;
import de.ea.winterpokal.utils.web.RemoteRequest;
import de.ea.winterpokal.utils.web.RequestType;

public class FavoritesRemoteDAO implements IFavoritesDAO {

	public List<WPUser> getUsers() {
		ArrayList<WPUser> users = new ArrayList<WPUser>();

		String responseString = RemoteRequest.DoRequest(Constants.HOST_NAME + "favorites/users.json", RequestType.GET);

		Gson gson = new Gson();

		Type t = new TypeToken<Response<List<UserObject>>>() {
		}.getType();
		Response<List<UserObject>> response = gson.fromJson(responseString, t);

		if (response!=null && "OK".equals(response.getStatus())) {
			for (UserObject d : response.getData()) {
				users.add(d.getUser());
			}
		} else {
			Log.e("GetUserFavoritesFailed", responseString+ "");
		}
		return users;
	}

	public List<WPTeam> getTeams() {
		ArrayList<WPTeam> teams = new ArrayList<WPTeam>();
		String responseString = RemoteRequest.DoRequest(Constants.HOST_NAME + "favorites/teams.json", RequestType.GET);

		Gson gson = new Gson();

		Type t = new TypeToken<Response<List<TeamObject>>>() {
		}.getType();
		Response<List<TeamObject>> response = gson.fromJson(responseString, t);

		if (response !=null && "OK".equals(response.getStatus())) {
			for (TeamObject d : response.getData()) {
				WPTeam team = d.getTeam();
				if(team!=null) {
					teams.add(team);
					team.setUsers(d.getUsers());
				}
			}
		} else {
			Log.e("GetTeamFavoritesFailed", responseString + "");
		}
		return teams;
	}

	public boolean add(WPTeam team) {
		if (team == null)
			return false;
		return addAsFavorite(true, team.getId());
	}

	public boolean add(WPUser user) {
		if (user == null)
			return false;
		return addAsFavorite(false, user.getId());
	}

	public boolean remove(WPTeam team) {
		if (team == null)
			return false;
		return removeAsFavorite(true, team.getId());
	}

	public boolean remove(WPUser user) {
		if (user == null)
			return false;
		return removeAsFavorite(false, user.getId());

	}

	private boolean addAsFavorite(boolean trueIfTeamFalseOtherwise, int id) {
		String dataKey = trueIfTeamFalseOtherwise ? "team_id" : "user_id";
		String jsonString = String.format("{ \"%s\" : %d }", dataKey, id);
		String responseString = RemoteRequest.DoRequest(Constants.HOST_NAME + "favorites/add.json", jsonString, RequestType.POST);

		Gson gson = new Gson();
		UserResponse response = gson.fromJson(responseString, UserResponse.class);

		if ("OK".equals(response.getStatus())) {
			return true;
		} else {
			Log.e("UserRetrieveFailed", responseString);
			return false;
		}
	}

	private boolean removeAsFavorite(boolean trueIfTeamFalseOtherwise, int id) {
		String dataKey = trueIfTeamFalseOtherwise ? "team_id" : "user_id";
		String jsonString = String.format("{ \"%s\" : %d }", dataKey, id);
		String responseString = RemoteRequest.DoRequest(Constants.HOST_NAME + "favorites/remove.json", jsonString, RequestType.POST);

		Gson gson = new Gson();
		UserResponse response = gson.fromJson(responseString, UserResponse.class);

		if ("OK".equals(response.getStatus())) {
			return true;
		} else {
			Log.e("UserRetrieveFailed", responseString);
			return false;
		}
	}
}

class UserObject {
	private WPUser user;

	public WPUser getUser() {
		return user;
	}

	public void setUser(WPUser user) {
		this.user = user;
	}
}
