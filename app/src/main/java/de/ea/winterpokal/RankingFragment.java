package de.ea.winterpokal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import de.ea.winterpokal.model.SportTypes;
import de.ea.winterpokal.model.WPRanking;
import de.ea.winterpokal.model.WPTeam;
import de.ea.winterpokal.model.WPUser;
import de.ea.winterpokal.persistence.remote.DAORemoteException;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class RankingFragment extends Fragment implements AdapterView.OnItemClickListener,  TabHost.OnTabChangeListener, AbsListView.OnScrollListener {
	private List<WPRanking> entriesUser = new ArrayList<WPRanking>();
	private List<WPRanking> entriesTeam = new ArrayList<WPRanking>();

	private int limit = 50;
	private int pointerUser = 0;
	private int pointerTeam = 0;

	private boolean showTeamRanking = false;
	private RankingArrayAdapter adapterUser;
	private RankingArrayAdapter adapterTeam;

	private LoadRankingDataTask mTask = null;
	private Spinner mSpinner = null;

	private int mSpinnerInitializedCount = 0;
	private int mPos = 0;

	private final static String TAB_USER ="tUser";
    private final static String TAB_TEAM ="tTeam";
    ListView lvTeams, lvUsers;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	private ArrayAdapter<WPRanking> getAdapter() {
		return showTeamRanking ? adapterTeam : adapterUser;
	}

	private void setListAdapter(ArrayAdapter<?> adapter) {
		getListView().setAdapter(adapter);
	}

	private List<WPRanking> getEntries() {
		return showTeamRanking ? entriesTeam : entriesUser;
	}

	private void appendEntries(List<WPRanking> items) {
		List<WPRanking> existing = showTeamRanking ? entriesTeam : entriesUser ;
		existing.addAll(items);
		if(showTeamRanking) {
			pointerTeam += items.size();
		}
		else {
			pointerUser +=items.size();
		}
		getAdapter().notifyDataSetChanged();
		int pos = getListView().getFirstVisiblePosition();
		getListView().setSelectionFromTop(pos, 0);
	}

	private ListView getListView() {
		return showTeamRanking ? lvTeams : lvUsers;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
		final View v = inflater.inflate(R.layout.ranking, null);

        TabHost tabHost = (TabHost)v.findViewById(android.R.id.tabhost);
        tabHost.setup();
        tabHost.addTab(tabHost.newTabSpec(TAB_USER).setIndicator(getResources().getString(R.string.ranking_tab_header_users)).setContent(R.id.ranking_list_user));
        tabHost.addTab(tabHost.newTabSpec(TAB_TEAM).setIndicator(getResources().getString(R.string.ranking_tab_header_teams)).setContent(R.id.ranking_list_team));
        tabHost.setOnTabChangedListener(this);

        lvTeams = v.findViewById(R.id.ranking_list_team);
        lvUsers = v.findViewById(R.id.ranking_list_user);

        lvTeams.setOnScrollListener(this);
        lvUsers.setOnScrollListener(this);

        lvTeams.setOnItemClickListener(this);
        lvUsers.setOnItemClickListener(this);

		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		registerForContextMenu(lvUsers);
		registerForContextMenu(lvTeams);

		if (entriesUser != null)
			entriesUser.clear();
		pointerUser = 0;
		if (entriesTeam != null)
			entriesTeam.clear();
		pointerTeam = 0;
	}

	@Override
	public void onResume() {
		super.onResume();
		adapterUser = new RankingArrayAdapter(getActivity(), entriesUser);
		adapterTeam = new RankingArrayAdapter(getActivity(), entriesTeam);
		showTeamRanking = false;
		lvUsers.setAdapter(adapterUser);
		lvTeams.setAdapter(adapterTeam);
		LaunchTaskOrDoNothingIfRunning();
	}

	private void LaunchTaskOrDoNothingIfRunning() {
		if (mTask == null || mTask.getStatus().equals(AsyncTask.Status.FINISHED)) {
			mTask = new LoadRankingDataTask(showTeamRanking);
			mTask.execute();
		}
	}
        @Override
        public void onItemClick(AdapterView<?> var1, View var2, int position, long var4) {
            WPRanking item = (WPRanking) getAdapter().getItem(position);
            Class c = null;
            Bundle bundle = new Bundle();
            String keyword = null;
            Serializable object = null;
            if (showTeamRanking) {
                keyword = "team";
                object = item.getTeam();
                c = TeamActivity.class;
            } else {
                keyword = "user";
                object = item.getUser();
                c = UserActivity.class;
            }
            Intent intent = new Intent(getActivity(), c);
            if (keyword != null) {
                intent.putExtra(keyword, object);
                getActivity().startActivity(intent);
            }
        }

	public void onClick(View v) {
		LaunchTaskOrDoNothingIfRunning();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(RANKING_FRAGMENT_GROUP, MENU_OPTION_ADD_TO_FAVORITE, 0, R.string.makeFavorite);
	}

	public static final int RANKING_FRAGMENT_GROUP = 1;
	public static final int MENU_OPTION_ADD_TO_FAVORITE = 1;
	public static final int MENU_OPTION_REMOVE_FAVORITE = 2;

	public boolean onContextItemSelected(MenuItem item) {
		// only this fragment's context menus have group ID of -1
		if (item.getGroupId() == RANKING_FRAGMENT_GROUP) {
			switch (item.getItemId()) {
			case MENU_OPTION_ADD_TO_FAVORITE:
				AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
				makeFavorite(info.position);
				return true;
			}
		}
		return false;
	}

	private void makeFavorite(int positionInAdapter) {
		WPRanking ranking = getAdapter().getItem(positionInAdapter);
		if (ranking == null) {
			return;
		}
		Toast t = null;
		boolean res = false;
		if (showTeamRanking) {
			WPTeam team = ranking.getTeam();
			res = App.getInstance().getDAOFactory().getFavoritesDAO().add(team);
			t = Toast.makeText(getActivity(), res ? "Team hinzugefügt" : "Fehler", Toast.LENGTH_SHORT);
			t.show();
		} else {
			WPUser user = ranking.getUser();
			res = App.getInstance().getDAOFactory().getFavoritesDAO().add(user);
			t = Toast.makeText(getActivity(), res ? "User hinzugefügt" : "Fehler", Toast.LENGTH_SHORT);
			t.show();
		}
	}

    @Override
    public void onTabChanged(String s) {
        switch (s) {
            case TAB_USER:
                showTeamRanking = false;
                break;
            case TAB_TEAM:
                showTeamRanking = true;
                break;
                default:
                    return;
        }
        LaunchTaskOrDoNothingIfRunning();
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0) {
                LaunchTaskOrDoNothingIfRunning();
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        //nothing to do here
    }

    class LoadRankingDataTask extends AsyncTask<Void, Void, Void> {
		private ProgressDialog pDialog;

		private boolean teamRanking;
		List<WPRanking> moreEntries = new ArrayList<WPRanking>();

		public LoadRankingDataTask(boolean trueIfTeamfalseElse) {
			teamRanking = trueIfTeamfalseElse;
		}

		@Override
		protected void onPreExecute() {
			// Showing progress dialog before sending http request
			FragmentActivity a = RankingFragment.this.getActivity();
			pDialog = new ProgressDialog(a);
			pDialog.setMessage(a.getText(R.string.loading));
			pDialog.setIndeterminate(true);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected void onPostExecute(Void unused) {
			pDialog.dismiss();
			if (moreEntries != null) {
				appendEntries(moreEntries);
			}
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			try {
				if (teamRanking)
					moreEntries = App.getInstance().getDAOFactory().getRankingDAO().getByTeams(limit, pointerUser);
				else
					moreEntries = App.getInstance().getDAOFactory().getRankingDAO().get(SportTypes.total, "points", limit, pointerUser);
			} catch (DAORemoteException ex) {
				HashMap<String, String> errors = ex.getErrors();
				if (errors != null) {
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RankingFragment.this.getActivity());
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
					AlertDialog alertDialog = alertDialogBuilder.create();
					alertDialog.show();
				}

			} catch (Exception e) {
				Log.e("getRecent", e.getMessage(), e);
			}

			return null;
		}
	}
}

class RankingArrayAdapter extends ArrayAdapter<WPRanking> {
	private final Activity context;
	private final List<WPRanking> names;

	static class ViewHolder {
		public TextView place;
		public TextView points;
		public TextView duration;
		public TextView name;
		public TextView addInfo;
	}

	public RankingArrayAdapter(Activity context, List<WPRanking> names) {
		super(context, R.layout.rankingitem, names);
		this.context = context;
		this.names = names;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(R.layout.rankingitem, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.addInfo = (TextView) rowView.findViewById(R.id.tvAddInfo);
			viewHolder.duration = (TextView) rowView.findViewById(R.id.tvDuration);
			viewHolder.points = (TextView) rowView.findViewById(R.id.tvPoints);

			viewHolder.name = (TextView) rowView.findViewById(R.id.tvName);
			viewHolder.place = (TextView) rowView.findViewById(R.id.tvPlace);
			rowView.setTag(viewHolder);
		}

		ViewHolder holder = (ViewHolder) rowView.getTag();
		System.out.println("pos:" + position);
		System.out.println("items:" + names.size());
		WPRanking rankingItem = names.get(position);
		// holder.descr.setText(s.getDescription());

		boolean addInfoRequired = rankingItem.getUser() != null && rankingItem.getTeam() != null;
		// holder.addInfo.setVisibility(addInfoRequired ? View.INVISIBLE :
		// View.VISIBLE);
		holder.addInfo.setText(addInfoRequired ? rankingItem.getTeam().getName() : "");

		holder.name.setText(rankingItem.getUser() == null ? rankingItem.getTeam().getName() : rankingItem.getUser().getName());

		holder.place.setText((position + 1) + "");
		int points = rankingItem.getUser() == null ? rankingItem.getTeam().getPoints() : rankingItem.getUser().getPoints();
		holder.points.setText(points + "");

		String duration = rankingItem.getUser() == null ? rankingItem.getTeam().getDurationAsHours() : rankingItem.getUser().getDurationAsHours();
		holder.duration.setText(duration);

		return rowView;
	}
}
