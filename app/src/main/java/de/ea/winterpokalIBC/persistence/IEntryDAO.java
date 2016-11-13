package de.ea.winterpokalIBC.persistence;

import java.util.List;

import de.ea.winterpokalIBC.model.WPEntry;

public interface IEntryDAO {
	public void add(WPEntry entry);
	public List<WPEntry> get(int limit);
	public List<WPEntry> getForUser(int userId, int limit);
	public List<WPEntry> getForTeam(int teamId, int limit);
	public List<WPEntry> getRecent(int limit);
}
