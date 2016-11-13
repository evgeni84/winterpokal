package de.ea.winterpokalIBC.model;

import java.io.Serializable;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import de.ea.winterpokalIBC.utils.Helper;

@DatabaseTable(tableName = "wpUser")
public class WPUser implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 584265402205442931L;
	@DatabaseField(id = true)
	private int id;
	@DatabaseField
	private String name;
	@DatabaseField
	private int entries;
	@DatabaseField
	private int points;
	@DatabaseField
	private double duration;
	@DatabaseField(dataType = DataType.SERIALIZABLE)
	private WPTeam team;
	@DatabaseField
	private boolean appUser;

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

	public int getEntries() {
		return entries;
	}

	public void setEntries(int entries) {
		this.entries = entries;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public double getDuration() {
		return duration;
	}

	public String getDurationAsHours() {
		return Helper.getDurationAsHours(getDuration());
	}

	public void setDuration(double duration) {
		this.duration = duration;
	}

	public WPTeam getTeam() {
		return team;
	}

	public void setTeam(WPTeam team) {
		this.team = team;
	}

	@Override
	public String toString() {
		return name;
	}

	public WPUser() {

	}

	public WPUser(String name) {
		this();
		this.name = name;
	}

	public boolean isAppUser() {
		return appUser;
	}

	public void setAppUser(boolean appUser) {
		this.appUser = appUser;
	}
}
