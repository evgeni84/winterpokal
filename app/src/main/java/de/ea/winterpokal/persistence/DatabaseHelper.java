package de.ea.winterpokal.persistence;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import de.ea.winterpokal.model.WPEntry;
import de.ea.winterpokal.model.WPTeam;
import de.ea.winterpokal.model.WPToken;
import de.ea.winterpokal.model.WPUser;

/**
 * Database helper class used to manage the creation and upgrading of your
 * database. This class also usually provides the DAOs used by the other
 * classes.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	// name of the database file for your application -- change to something
	// appropriate for your app
	private static final String DATABASE_NAME = "winterpokal.db";
	// any time you make changes to your database objects, you may have to
	// increase the database version
	private static final int DATABASE_VERSION = 1;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * This is called when the database is first created. Usually you should
	 * call createTable statements here to create the tables that will store
	 * your data.
	 */
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			Log.i(DatabaseHelper.class.getName(), "onCreate");
			TableUtils.createTable(connectionSource, WPEntry.class);
			TableUtils.createTable(connectionSource, WPToken.class);
			TableUtils.createTable(connectionSource, WPUser.class);
			TableUtils.createTable(connectionSource, WPTeam.class);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}

	}

	/**
	 * This is called when your application is upgraded and it has a higher
	 * version number. This allows you to adjust the various data to match the
	 * new version number.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		onCreate(db, connectionSource);
	}

	/**
	 * Close the database connections and clear any cached DAOs.
	 */
	@Override
	public void close() {
		super.close();
	}

	private Dao<WPToken, Integer> tokenDao = null;
	private Dao<WPEntry, Integer> entryDao = null;
	private Dao<WPUser, Integer> userDao = null;

	public Dao<WPToken, Integer> getTokenDao() throws SQLException {
		if (tokenDao == null) {
			tokenDao = getDao(WPToken.class);
		}
		return tokenDao;
	}

	public Dao<WPEntry, Integer> getEntryDao() throws SQLException {
		if (entryDao == null) {
			entryDao = getDao(WPEntry.class);
		}
		return entryDao;
	}

	public Dao<WPUser, Integer> getUserDao() throws SQLException {
		if (userDao == null) {
			userDao = getDao(WPUser.class);
		}
		return userDao;
	}

	public boolean ClearTokenTable(){
		try {
			TableUtils.clearTable(getConnectionSource(), WPToken.class);
			return true;
		}
		catch(Exception ex){
			
			return false;
		}
	}
}
