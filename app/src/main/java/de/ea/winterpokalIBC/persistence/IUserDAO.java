package de.ea.winterpokalIBC.persistence;

import java.util.List;

import de.ea.winterpokalIBC.model.WPTeam;
import de.ea.winterpokalIBC.model.WPUser;

public interface IUserDAO {
	public WPUser get(int id);

	public List<WPUser> find(String nameOrNamepart, int limit);

	
	public boolean addAsFavorite(WPTeam team);

	public boolean removeFavorite(WPTeam team);
	
	
}
