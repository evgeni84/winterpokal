package de.ea.winterpokal.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "wpToken")
public class WPToken {

	@DatabaseField(canBeNull = false, id = true)
	private String token;
	@DatabaseField(canBeNull = false)
	private String description;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
