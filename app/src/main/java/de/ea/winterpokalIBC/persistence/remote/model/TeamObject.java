package de.ea.winterpokalIBC.persistence.remote.model;

import java.util.List;

import de.ea.winterpokalIBC.model.WPTeam;
import de.ea.winterpokalIBC.model.WPUser;

public class TeamObject {
	private WPTeam team;
	private List<WPUser> users;

	public WPTeam getTeam() {
		return team;
	}

	public void setTeam(WPTeam team) {
		this.team = team;
	}

	public List<WPUser> getUsers() {
		return users;
	}

	public void setUsers(List<WPUser> users) {
		this.users = users;
	}
}
