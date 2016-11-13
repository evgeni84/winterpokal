package de.ea.winterpokalIBC.model;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import de.ea.winterpokalIBC.utils.Helper;

@DatabaseTable(tableName = "wpEntry")
public class WPEntry {

	protected WPEntry() {
	}

	public WPEntry(SportTypes sport, Date date, double duration, Integer distance, String description) {
		this.category = sport;
		this.date = date;
		this.duration = duration;
		this.description = description;
		this.distance = distance;
	}

	public WPEntry(WPUser user, SportTypes sport, Date date, Double duration, int distance, String description) {
		this(sport, date, duration, distance, description);
		this.user = user;	
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public SportTypes getCategory() {
		return category;
	}

	public void setCategory(SportTypes sport) {
		this.category = sport;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public double getDuration() {
		return duration;
	}
	
	public String getDurationAsHours() {
		return Helper.getDurationAsHours(getDuration());
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public WPUser getUser() {
		return user;
	}

	public void setUser(WPUser user) {
		this.user = user;
	}

	public boolean isSyncronized() {
		return isSyncronized;
	}

	public void setSyncronized(boolean isSyncronized) {
		this.isSyncronized = isSyncronized;
	}

	public int getPoints() {
		switch (category) {
		case radfahren:
		case skilanglauf:
			return (int)duration / 15;

		case laufen:
			return (int)duration / 20;
		default:
			return duration >= 30 ? 2 : 0;
		}
	}

	public Integer getDistance() {
		return distance;
	}

	public void setDistance(Integer distance) {
		this.distance = distance;
	}

	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField
	private SportTypes category;

	@DatabaseField(canBeNull = false)
	private Date date;

	@DatabaseField
	private double duration;

	@DatabaseField
	private String description;

	@DatabaseField
	private Integer distance;
	
	private WPUser user;

	@DatabaseField
	private boolean isSyncronized;

}
