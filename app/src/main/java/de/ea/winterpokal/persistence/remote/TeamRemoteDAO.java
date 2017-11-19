package de.ea.winterpokal.persistence.remote;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import de.ea.winterpokal.model.WPTeam;
import de.ea.winterpokal.model.WPUser;
import de.ea.winterpokal.persistence.ITeamDAO;
import de.ea.winterpokal.persistence.remote.model.TeamObject;
import de.ea.winterpokal.utils.web.Constants;
import de.ea.winterpokal.utils.web.RemoteRequest;
import de.ea.winterpokal.utils.web.RequestType;

public class TeamRemoteDAO implements ITeamDAO {

	public WPTeam get(int id) {
		String responseString = RemoteRequest.DoRequest(Constants.HOST_NAME + "teams/get/" + id + ".json", RequestType.GET);

		Gson gson = new Gson();
		Response<TeamObject> response = null;

		try {
			response = gson.fromJson(responseString, new TypeToken<Response<TeamObject>>() {}.getType());
		}catch (Exception ex) {
			Log.e("ParseTeamResponseFailed", ex.getMessage());
			return null;
		}

		if (response != null && "OK".equals(response.getStatus())) {
			TeamObject data = response.getData();
			if(data != null) {
				WPTeam team = data.getTeam();
				team.setUsers(data.getUsers());

				return team;
			}else {
				Log.e("RetrieveTeamFailed", "invalidData:"+responseString);
			}
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


