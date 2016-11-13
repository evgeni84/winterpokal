package de.ea.winterpokal.persistence;

import java.util.List;

import de.ea.winterpokal.model.SportTypes;
import de.ea.winterpokal.model.WPRanking;

public interface IRankingDAO {

	List<WPRanking> get(SportTypes cat, String mode, int limit, int start);

	List<WPRanking> getByTeams(int limit, int start);

	WPRanking get(int id);

	WPRanking getForTeam(int id);
}
