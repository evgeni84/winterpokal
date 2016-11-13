package de.ea.winterpokal;

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
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class FavoriteFragment extends ListFragment implements OnItemSelectedListener {
	private WPTeamArrayAdapter adapterTeam = null;
	private WPUserArrayAdapter adapterUser = null;
	private ArrayList<WPTeam> teamFavorites = new ArrayList<WPTeam>();
	private ArrayList<WPUser> userFavorites = new ArrayList<WPUser>();
	private boolean showFavoriteTeams = false;
	private Spinner mSpinner = null;
	private GetDataTask mTask = null;

	private boolean mShowTeamFavorites;

	private int mSpinnerInitializedCount = 0;
	private int mPos = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		registerForContextMenu(getListView());
	}

	@Override
	public void onResume() {
		super.onResume();
		adapterTeam = new WPTeamArrayAdapter(this.getActivity(), teamFavorites);
		adapterUser = new WPUserArrayAdapter(this.getActivity(), userFavorites);
		setListAdapter(adapterUser);
		new GetDataTask(showFavoriteTeams).execute();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.activity_ranking, menu);
		MenuItem mi = menu.findItem(R.id.menuObjectType);
		if (mi == null) {
			return;
		}
		View v = MenuItemCompat.getActionView(mi);
		if (v == null) {
			return;
		}
		mSpinner = (Spinner) v.findViewById(R.id.objectTypeFilter);
		if (mSpinner == null) {
			return;
		}
		if (mSpinner != null) {
			SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(((ActionBarActivity) getActivity()).getSupportActionBar().getThemedContext(), R.array.object_type,
					android.R.layout.simple_expandable_list_item_1); // create
																		// the
																		// adapter
																		// from
																		// a
																		// StringArray
			mSpinner.setAdapter(mSpinnerAdapter); // set the adapter
			mSpinner.setOnItemSelectedListener((OnItemSelectedListener) this);
		}
	}

	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

		if (mSpinnerInitializedCount < 1) {
			mSpinnerInitializedCount++;
		} else if (position != mPos) {
			mPos = position;
			String o = (String) parent.getItemAtPosition(position);
			if ("Team".equalsIgnoreCase(o)) {
				mShowTeamFavorites = true;
			} else if ("User".equalsIgnoreCase(o)) {
				mShowTeamFavorites = false;
			}

			teamFavorites.clear();
			userFavorites.clear();
			LaunchTaskOrDoNothingIfRunning();
		}
	}

	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

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

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Fragment f = null;
		Bundle bundle = new Bundle();
		Object obj = getListAdapter().getItem(position);
		if (obj instanceof WPTeam ) {
			WPTeam item = (WPTeam) obj;
			bundle.putSerializable("team", item);
			f = new TeamFragment();
		} else {
			WPUser item = (WPUser) obj;
			bundle.putSerializable("user", item);
			f = new UserFragment();
		}
		f.setArguments(bundle);
		getFragmentManager().beginTransaction().replace(R.id.content_frame, f).addToBackStack(null).commit();
	}
	
	private void makeFavorite(int positionInAdapter) {
		Toast t = null;
		boolean res = false;
		if (mShowTeamFavorites) {
			WPTeam team = adapterTeam.getItem(positionInAdapter);
			if (team == null) {
				return;
			}
			res = App.getInstance().getDAOFactory().getFavoritesDAO().remove(team);
			t = Toast.makeText(getActivity(), res ? "Team entfernt" : "Fehler", Toast.LENGTH_SHORT);
			if(res){
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
			if(res){
				userFavorites.remove(positionInAdapter);
				adapterUser.notifyDataSetChanged();
			}
			t.show();
		}
	}

	private void LaunchTaskOrDoNothingIfRunning() {
		if (mTask == null || mTask.getStatus().equals(AsyncTask.Status.FINISHED)) {
			mTask = new GetDataTask(mShowTeamFavorites);
			mTask.execute();
		}
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

			ArrayAdapter<?> adapter = null;

			if (teamList != null) {
				teamFavorites.clear();
				teamFavorites.addAll(teamList);
				adapter = adapterTeam;

			} else if (userList != null) {
				userFavorites.clear();
				userFavorites.addAll(userList);
				adapter = adapterUser;

			}
			if (adapter != null) {
				setListAdapter(adapter);
				adapter.notifyDataSetChanged();
				FavoriteFragment.this.getListView().setSelectionFromTop(0, 0);
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

class WPTeamArrayAdapter extends ArrayAdapter<WPTeam> {
	private final Activity context;
	private final List<WPTeam> teams;

	static class ViewHolder {
		public TextView name;
		public TextView points;
		public TextView duration;

	}

	public WPTeamArrayAdapter(Activity context, List<WPTeam> users) {
		super(context, R.layout.teammemberitem, users);
		this.context = context;
		this.teams = users;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(R.layout.teammemberitem, null);
			ViewHolder viewHolder = initViewHolder(rowView);
			rowView.setTag(viewHolder);
		}

		ViewHolder holder = (ViewHolder) rowView.getTag();
		if(holder==null) {
			holder = initViewHolder(rowView);
			rowView.setTag(holder);
		}
		WPTeam team = teams.get(position);
		if (team != null) {
			holder.name.setText(team.getName());
			holder.duration.setText(team.getDurationAsHours());
			holder.points.setText(team.getPoints() + " Punkt(e)");
		}else {
			Log.e("exo", "team is null");
		}

		return rowView;
	}
	
	private static ViewHolder initViewHolder(View rowView){
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.name = (TextView) rowView.findViewById(R.id.tvTeamUserName);
		viewHolder.duration = (TextView) rowView.findViewById(R.id.tvTeamUserDuration);
		viewHolder.points = (TextView) rowView.findViewById(R.id.tvTeamUserPoints);
		return viewHolder;
	}
}
