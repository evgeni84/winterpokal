package de.ea.winterpokal.persistence.remote;

import java.util.List;

import de.ea.winterpokal.model.WPTeam;
import de.ea.winterpokal.model.WPUser;

public class UserResponse extends Response<UserData> {

}

class UserListResponse extends Response<List<UserData>> {

}

class UserData {
	private WPUser user;
	private WPTeam team;

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
