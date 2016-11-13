package de.ea.winterpokalIBC.persistence.remote;

import de.ea.winterpokalIBC.persistence.DAOFactory;
import de.ea.winterpokalIBC.persistence.IEntryDAO;
import de.ea.winterpokalIBC.persistence.IFavoritesDAO;
import de.ea.winterpokalIBC.persistence.IRankingDAO;
import de.ea.winterpokalIBC.persistence.ITeamDAO;
import de.ea.winterpokalIBC.persistence.IUserDAO;

public class RemoteDAOFactory extends DAOFactory {

	@Override
	public IEntryDAO getEntryDAO() {
		return instantiateDAO(EntryRemoteDAO.class);
	}

	@Override
	public IUserDAO getUserDAO() {
		return instantiateDAO(UserRemoteDAO.class);
	}

	private <T> T instantiateDAO(Class<T> daoClass) {
		try {
			return (T) daoClass.newInstance();
		} catch (Exception ex) {
			throw new RuntimeException("Can not instantiate DAO: " + daoClass, ex);
		}
	}

	@Override
	public ITeamDAO getTeamDAO() {
		return instantiateDAO(TeamRemoteDAO.class);
	}

	@Override
	public IRankingDAO getRankingDAO() {
		return instantiateDAO(RankingRemoteDAO.class);
	}
	
	@Override
	public IFavoritesDAO getFavoritesDAO() {
		return instantiateDAO(FavoritesRemoteDAO.class);
	}
	
}
