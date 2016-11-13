package de.ea.winterpokalIBC.persistence.remote;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import de.ea.winterpokalIBC.model.SportTypes;
import de.ea.winterpokalIBC.model.WPEntry;
import de.ea.winterpokalIBC.model.WPRanking;
import de.ea.winterpokalIBC.model.WPTeam;
import de.ea.winterpokalIBC.model.WPUser;
import de.ea.winterpokalIBC.model.WinterpokalException;
import de.ea.winterpokalIBC.persistence.IRankingDAO;
import de.ea.winterpokalIBC.utils.web.Constants;
import de.ea.winterpokalIBC.utils.web.RemoteRequest;
import de.ea.winterpokalIBC.utils.web.RequestType;

public class RankingRemoteDAO implements IRankingDAO {

	public List<WPRanking> get(SportTypes cat, String mode, int limit, int start) {

		HashMap<String, String> args = new HashMap<String, String>();

		args.put("filter", cat == null ? "total" : cat.name());
		args.put("mode", "points");
		args.put("limit", (limit == 0 ? 100 : limit) + "");
		args.put("start", (start < 1 ? 1 : start) + "");

		String responseString = RemoteRequest.DoRequest(Constants.HOST_NAME + "user_ranking/get.json", args, RequestType.GET);

		Gson gson = new Gson();
		Response<List<RankingObject>> response = gson.fromJson(responseString, new TypeToken<Response<List<RankingObject>>>() {
		}.getType());

		if (response != null && "OK".equals(response.getStatus())) {
			List<WPRanking> rankingList = new ArrayList<WPRanking>();
			for (RankingObject ro : response.getData()) {
				WPRanking r = ro.getRanking();
				r.setTeam(ro.getTeam());
				r.setUser(ro.getUser());
				if (r.getUser() != null)
					r.getUser().setTeam(r.getTeam());
				rankingList.add(r);
			}
			return rankingList;
		} else {
			Log.e("Ranking.get.Failed", responseString);
			throw new DAORemoteException("ErrorGetRanking", response != null ? response.getMessagesAsMap() : null);
			// throw new WinterpokalException(message)
		}

	}

	public List<WPRanking> getByTeams(int limit, int start) {
		HashMap<String, String> args = new HashMap<String, String>();

		args.put("limit", (limit == 0 ? 100 : Math.min(100, limit)) + "");
		args.put("start", (start < 1 ? 1 : start) + "");

		String responseString = RemoteRequest.DoRequest(Constants.HOST_NAME + "team_ranking/get.json", args, RequestType.GET);

		Gson gson = new Gson();
		Response<List<RankingObject>> response = gson.fromJson(responseString, new TypeToken<Response<List<RankingObject>>>() {
		}.getType());

		if ("OK".equals(response.getStatus())) {
			List<WPRanking> rankingList = new ArrayList<WPRanking>();
			for (RankingObject ro : response.getData()) {
				WPRanking r = ro.getRanking();
				r.setTeam(ro.getTeam());
				rankingList.add(r);
			}
			return rankingList;
		} else {
			Log.e("Ranking.getByTeams.Failed", responseString);
			throw new DAORemoteException("ErrorGetRanking", response != null ? response.getMessagesAsMap() : null);
		}
	}

	public WPRanking get(int id) {
		String responseString = RemoteRequest.DoRequest(Constants.HOST_NAME + "user_ranking/user/" + id + ".json", RequestType.GET);
		Gson gson = new Gson();
		Response<RankingObject> response = gson.fromJson(responseString, new TypeToken<Response<RankingObject>>() {
		}.getType());

		if ("OK".equals(response.getStatus())) {
			RankingObject ro = response.getData();
			{
				WPRanking r = ro.getRanking();
				r.setTeam(ro.getTeam());
				return r;
			}
		} else {
			Log.e("Ranking.get.Failed", responseString);
			throw new DAORemoteException("ErrorGetRanking", response != null ? response.getMessagesAsMap() : null);
		}
	}

	public WPRanking getForTeam(int id) {
		String responseString = RemoteRequest.DoRequest(Constants.HOST_NAME + "team_ranking/team/" + id + ".json", RequestType.GET);
		Gson gson = new Gson();
		Response<RankingObject> response = gson.fromJson(responseString, new TypeToken<Response<RankingObject>>() {
		}.getType());

		if ("OK".equals(response.getStatus())) {
			RankingObject ro = response.getData();
			{
				WPRanking r = ro.getRanking();
				r.setTeam(ro.getTeam());
				return r;
			}
		} else {
			Log.e("Ranking.get.Failed", responseString);
			throw new DAORemoteException("ErrorGetRanking", response != null ? response.getMessagesAsMap() : null);
		}
	}

}

class RankingObject {
	private WPRanking ranking;
	private WPUser user;
	private WPTeam team;

	public WPRanking getRanking() {
		return ranking;
	}

	public void setRanking(WPRanking ranking) {
		this.ranking = ranking;
	}

	public WPUser getUser() {
		return user;
	}

	public void setUser(WPUser user) {
		this.user = user;
	}

	public WPTeam getTeam() {
		return team;
	}

	public void setTeam(WPTeam team) {
		this.team = team;
	}
}
