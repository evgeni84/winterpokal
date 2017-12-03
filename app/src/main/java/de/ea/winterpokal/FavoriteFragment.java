package de.ea.winterpokal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import de.ea.winterpokal.model.WPRanking;
import de.ea.winterpokal.model.WPTeam;
import de.ea.winterpokal.model.WPUser;
import de.ea.winterpokal.persistence.remote.DAORemoteException;
import de.ea.winterpokal.utils.web.auth.Auth;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class FavoriteFragment extends Fragment implements AdapterView.OnItemClickListener, TabHost.OnTabChangeListener {
	private WPTeamArrayAdapter adapterTeam = null;
	private WPUserArrayAdapter adapterUser = null;
	private ArrayList<WPTeam> teamFavorites = new ArrayList<WPTeam>();
	private ArrayList<WPUser> userFavorites = new ArrayList<WPUser>();
	private boolean showFavoriteTeams = false;
	private Spinner mSpinner = null;
	private GetDataTask mTask = null;
	private final static String TAB_USER = "tUser";
	private final static String TAB_TEAM = "tTeam";
	ListView lvTeams, lvUsers;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
		final View v = inflater.inflate(R.layout.favorites, null);

		TabHost tabHost = (TabHost) v.findViewById(android.R.id.tabhost);
		tabHost.setup();
		tabHost.addTab(tabHost.newTabSpec(TAB_USER).setIndicator(getResources().getString(R.string.favorites_tab_header_users)).setContent(R.id.favorites_list_user));
		tabHost.addTab(tabHost.newTabSpec(TAB_TEAM).setIndicator(getResources().getString(R.string.favorites_tab_header_teams)).setContent(R.id.favorites_list_team));
		tabHost.setOnTabChangedListener(this);

		lvTeams = v.findViewById(R.id.favorites_list_team);
		lvUsers = v.findViewById(R.id.favorites_list_user);
		lvTeams.setOnItemClickListener(this);
		lvUsers.setOnItemClickListener(this);
/*
		lvTeams.setOnScrollListener(this);
		lvUsers.setOnScrollListener(this);
*/
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		registerForContextMenu(lvTeams);
		registerForContextMenu(lvUsers);
	}

	@Override
	public void onResume() {
		super.onResume();
		adapterTeam = new WPTeamArrayAdapter(this.getActivity(), teamFavorites);
		adapterUser = new WPUserArrayAdapter(this.getActivity(), userFavorites);
		lvUsers.setAdapter(adapterUser);
		lvTeams.setAdapter(adapterTeam);
		new GetDataTask(showFavoriteTeams).execute();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(FAVORITE_FRAGMENT_GROUP, MENU_OPTION_REMOVE_FAVORITE, 0, R.string.removeFavorite);
	}

	public static final int FAVORITE_FRAGMENT_GROUP = 2;
	public static final int MENU_OPTION_REMOVE_FAVORITE = 1;

	public boolean onContextItemSelected(MenuItem item) {
		// only this fragment's context menus have group ID of -1
		if (item.getGroupId() == FAVORITE_FRAGMENT_GROUP) {
			switch (item.getItemId()) {
				case MENU_OPTION_REMOVE_FAVORITE:
					AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
					makeFavorite(info.position);
					return true;

			}
		}
		return false;
	}

	private void makeFavorite(int positionInAdapter) {
		Toast t = null;
		boolean res = false;
		if (showFavoriteTeams) {
			WPTeam team = adapterTeam.getItem(positionInAdapter);
			if (team == null) {
				return;
			}
			res = App.getInstance().getDAOFactory().getFavoritesDAO().remove(team);
			t = Toast.makeText(getActivity(), res ? "Team entfernt" : "Fehler", Toast.LENGTH_SHORT);
			if (res) {
				teamFavorites.remove(positionInAdapter);
				adapterTeam.notifyDataSetChanged();
			}
			t.show();
		} else {
			WPUser user = adapterUser.getItem(positionInAdapter);
			if (user == null) {
				return;
			}
			res = App.getInstance().getDAOFactory().getFavoritesDAO().remove(user);
			t = Toast.makeText(getActivity(), res ? "User entfernt" : "Fehler", Toast.LENGTH_SHORT);
			if (res) {
				userFavorites.remove(positionInAdapter);
				adapterUser.notifyDataSetChanged();
			}
			t.show();
		}
	}

	private void LaunchTaskOrDoNothingIfRunning() {
		if (mTask == null || mTask.getStatus().equals(AsyncTask.Status.FINISHED)) {
			mTask = new GetDataTask(showFavoriteTeams);
			mTask.execute();
		}
	}

	@Override
	public void onTabChanged(String tabId) {
		switch (tabId) {
			case TAB_USER:
				showFavoriteTeams = false;
				break;
			case TAB_TEAM:
				showFavoriteTeams = true;
				break;
			default:
				return;
		}
		teamFavorites.clear();
		userFavorites.clear();
		LaunchTaskOrDoNothingIfRunning();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Serializable obj = (Serializable) parent.getItemAtPosition(position);
		String keyword = null;

		Class activityClass;
		if (obj instanceof WPTeam) {
			keyword = "team";
			activityClass = TeamActivity.class;
		} else {
			keyword = "user";
			activityClass = UserActivity.class;
		}
		if (keyword != null) {
			Intent intent = new Intent(getActivity(), activityClass);
			intent.putExtra(keyword, obj);
			getActivity().startActivity(intent);
		}
	}

	private void updateTeamData(List<WPTeam> teamList) {
		teamFavorites.clear();
		teamFavorites.addAll(teamList);
		adapterTeam.notifyDataSetChanged();
		lvTeams.setSelectionFromTop(0, 0);
	}

	private void updateUserData(List<WPUser> userList) {
		userFavorites.clear();
		userFavorites.addAll(userList);
		adapterUser.notifyDataSetChanged();
		lvUsers.setSelectionFromTop(0, 0);
	}

	class GetDataTask extends AsyncTask<Void, Void, Void> {

		private boolean teamRanking;
		List<WPUser> userList = null;
		List<WPTeam> teamList = null;
		private ProgressDialog pDialog;


		public GetDataTask(boolean trueIfTeamfalseElse) {
			teamRanking = trueIfTeamfalseElse;
		}

		@Override
		protected void onPreExecute() {
			// Showing progress dialog before sending http request
			FragmentActivity a = FavoriteFragment.this.getActivity();
			pDialog = new ProgressDialog(a);
			pDialog.setMessage(a.getText(R.string.loading));
			pDialog.setIndeterminate(true);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected void onPostExecute(Void unused) {
			pDialog.dismiss();
			if (teamRanking) {
				updateTeamData(teamList);
			} else {
				updateUserData(userList);
			}
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			try {
				WPUser user = Auth.getUser();
				if (teamRanking) {
					teamList = App.getInstance().getDAOFactory().getFavoritesDAO().getTeams();
					if (user.getTeam() != null) {
						teamList.add(0, user.getTeam());
					}
				} else {
					userList = App.getInstance().getDAOFactory().getFavoritesDAO().getUsers();
					userList.add(0, user);
				}
			} catch (DAORemoteException ex) {
				HashMap<String, String> errors = ex.getErrors();
				if (errors != null) {
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FavoriteFragment.this.getActivity());

					// set title
					alertDialogBuilder.setTitle(R.string.error);

					StringBuilder sb = new StringBuilder();

					sb.append(getString(R.string.errorOnLoad) + ":\n");
					for (Entry<String, String> entry : errors.entrySet()) {
						sb.append(String.format("- %s (%s)\n", entry.getValue(), entry.getKey()).toString());
					}
					alertDialogBuilder.setMessage(sb.toString()).setCancelable(false).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
						}
					});

					// create alert dialog
					AlertDialog alertDialog = alertDialogBuilder.create();

					// show it
					alertDialog.show();
				}

			} catch (Exception e) {
				Log.e("getRecent", e.getMessage(), e);
			}

			return null;
		}
	}
}


