package de.ea.winterpokalIBC.persistence.remote;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import de.ea.winterpokalIBC.model.WPTeam;
import de.ea.winterpokalIBC.model.WPUser;
import de.ea.winterpokalIBC.persistence.ITeamDAO;
import de.ea.winterpokalIBC.persistence.remote.model.TeamObject;
import de.ea.winterpokalIBC.utils.web.Constants;
import de.ea.winterpokalIBC.utils.web.RemoteRequest;
import de.ea.winterpokalIBC.utils.web.RequestType;

public class TeamRemoteDAO implements ITeamDAO {

	public WPTeam get(int id) {
		String responseString = RemoteRequest.DoRequest(Constants.HOST_NAME + "teams/get/" + id + ".json", RequestType.GET);

		Gson gson = new Gson();
		Response<TeamObject> response = gson.fromJson(responseString, new TypeToken<Response<TeamObject>>() {
		}.getType());

		if ("OK".equals(response.getStatus())) {
			WPTeam team = response.getData().getTeam();
			team.setUsers(response.getData().getUsers());
			if (team.getUsers() != null)
				for (WPUser user : team.getUsers())
					user.setTeam(team);
			team.setDuration(team.getDurationComputed());
			team.setPoints(team.getPointsComputed());
			return team;
		} else {
			Log.e("RetrieveTeamFailed", responseString);
		}

		return null;
	}

	public boolean addAsFavorite(int id) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean removeFavorite(int id) {
		// TODO Auto-generated method stub
		return false;
	}

}


