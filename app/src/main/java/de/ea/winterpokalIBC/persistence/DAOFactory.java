package de.ea.winterpokalIBC.persistence;

public abstract class DAOFactory {

	public static final Class REMOTE = de.ea.winterpokalIBC.persistence.remote.RemoteDAOFactory.class;

	/**
	 * Factory method for instantiation of concrete factories.
	 */
	public static DAOFactory instance(Class factory) {
		try {
			return (DAOFactory) factory.newInstance();
		} catch (Exception ex) {
			throw new RuntimeException("Couldn't create DAOFactory: " + factory);
		}
	}

	// Add your DAO interfaces here
	public abstract IEntryDAO getEntryDAO();

	public abstract IUserDAO getUserDAO();

	public abstract ITeamDAO getTeamDAO();

	public abstract IRankingDAO getRankingDAO();
	
	public abstract IFavoritesDAO getFavoritesDAO();

}
