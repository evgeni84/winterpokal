package de.ea.winterpokalIBC.persistence;

import java.util.List;

import de.ea.winterpokalIBC.model.WPTeam;
import de.ea.winterpokalIBC.model.WPUser;

public interface IFavoritesDAO {

	List<WPTeam> getTeams();

	List<WPUser> getUsers();

	boolean add(WPTeam team);

	boolean remove(WPTeam team);

	boolean add(WPUser user);

	boolean remove(WPUser user);
}
