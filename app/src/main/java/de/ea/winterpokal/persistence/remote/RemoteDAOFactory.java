package de.ea.winterpokal.persistence.remote;

import de.ea.winterpokal.persistence.DAOFactory;
import de.ea.winterpokal.persistence.IEntryDAO;
import de.ea.winterpokal.persistence.IFavoritesDAO;
import de.ea.winterpokal.persistence.IRankingDAO;
import de.ea.winterpokal.persistence.ITeamDAO;
import de.ea.winterpokal.persistence.IUserDAO;

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
