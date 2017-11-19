package de.ea.winterpokal.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import de.ea.winterpokal.utils.Helper;

@DatabaseTable(tableName = "wpTeam")
public class WPTeam implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6118921751595800490L;
	@DatabaseField(id = true)
	private int id;
	@DatabaseField
	private String name;
	@DatabaseField
	private String description;
	private WPUser leader;
	private List<WPUser> users;
	
	@DatabaseField
	private int points;
	@DatabaseField
	private double duration;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public WPUser getLeader() {
		return leader;
	}

	public void setLeader(WPUser leader) {
		this.leader = leader;
	}

	public List<WPUser> getUsers() {
		return users;
	}

	public void setUsers(List<WPUser> users) {
		this.users = users;
		if (users != null) {
			for (WPUser user : users) {
				user.setTeam(this);
			}
		}
		setDuration(getDurationComputed());
		setPoints(getPointsComputed());
	}

	public void addUser(WPUser user) {
		if (users == null)
			users = new ArrayList<WPUser>();
		users.add(user);
	}

	public int getDurationComputed() {
		if (users == null)
			return 0;
		int duration = 0;
		for (WPUser user : users) {
			duration += user.getDuration();
		}
		return duration;
	}

	public String getDurationComputedAsHours() {
		return Helper.getDurationAsHours(getDurationComputed());
	}

	public int getPointsComputed() {
		if (users == null)
			return 0;
		int points = 0;
		for (WPUser user : users) {
			points += user.getPoints();
		}
		return points;
	}

	public double getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}
	
	public String getDurationAsHours() {
		return Helper.getDurationAsHours(getDuration());
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}
	
	

}
