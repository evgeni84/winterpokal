package de.ea.winterpokal.persistence;

import java.util.List;

import de.ea.winterpokal.model.WPTeam;
import de.ea.winterpokal.model.WPUser;

public interface IFavoritesDAO {

	List<WPTeam> getTeams();

	List<WPUser> getUsers();

	boolean add(WPTeam team);

	boolean remove(WPTeam team);

	boolean add(WPUser user);

	boolean remove(WPUser user);
}
