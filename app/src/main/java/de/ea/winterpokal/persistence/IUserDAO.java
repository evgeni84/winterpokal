package de.ea.winterpokal.persistence;

import java.util.List;

import de.ea.winterpokal.model.WPTeam;
import de.ea.winterpokal.model.WPUser;

public interface IUserDAO {
	public WPUser get(int id);

	public List<WPUser> find(String nameOrNamepart, int limit);

	
	public boolean addAsFavorite(WPTeam team);

	public boolean removeFavorite(WPTeam team);
	
	
}
