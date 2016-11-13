package de.ea.winterpokalIBC.model;

public class WPRanking {
	private int position;
	private int preweek;

	private WPUser user;
	private WPTeam team;

	public int getPosition() {
		return position;
	}

	public void setPosition(int postion) {
		this.position = postion;
	}

	public int getPreweek() {
		return preweek;
	}

	public void setPreweek(int preweek) {
		this.preweek = preweek;
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
