package de.ea.winterpokalIBC.persistence;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import de.ea.winterpokalIBC.R;
import de.ea.winterpokalIBC.model.WPEntry;

public class WPSqliteManager extends OrmLiteSqliteOpenHelper {

	// name of the database file for your application -- change to something
	// appropriate for your app
	private static final String DATABASE_NAME = "wp.db";
	// any time you make changes to your database objects, you may have to
	// increase the database version
	private static final int DATABASE_VERSION = 1;

	// the DAO object we use to access the SimpleData table
	private Dao<WPEntry, Integer> simpleDao = null;
	private RuntimeExceptionDao<WPEntry, Integer> simpleRuntimeDao = null;

	public WPSqliteManager(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION,
				R.raw.ormlite_config);
	}

	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			Log.i(WPSqliteManager.class.getName(), "onCreate");
			TableUtils.createTable(connectionSource, WPEntry.class);
		} catch (SQLException e) {
			Log.e(WPSqliteManager.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
			int oldVersion, int newVersion) {
		try {
			Log.i(WPSqliteManager.class.getName(), "onUpgrade");
			TableUtils.dropTable(connectionSource, WPSqliteManager.class, true);
			// after we drop the old databases, we create the new ones
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			Log.e(WPSqliteManager.class.getName(), "Can't drop databases", e);
			throw new RuntimeException(e);
		}

	}

	public Dao<WPEntry, Integer> getDao() throws SQLException {
		if (simpleDao == null) {
			simpleDao = getDao(WPEntry.class);
		}
		return simpleDao;
	}

	public RuntimeExceptionDao<WPEntry, Integer> getSimpleDataDao() {
		if (simpleRuntimeDao == null) {
			simpleRuntimeDao = getRuntimeExceptionDao(WPEntry.class);
		}
		return simpleRuntimeDao;
	}

	@Override
	public void close() {
		super.close();
		simpleRuntimeDao = null;
	}
}
