package de.ea.winterpokalIBC;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import de.ea.winterpokalIBC.model.WPTeam;
import de.ea.winterpokalIBC.model.WPUser;
import de.ea.winterpokalIBC.persistence.ITeamDAO;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class TeamFragment extends ListFragment {

	private TextView tvTeam = null;
	private TextView tvPoints = null;
	private TextView tvTime = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.team, null);

		tvTeam = (TextView) v.findViewById(R.id.tvTeam);
		tvPoints = (TextView) v.findViewById(R.id.tvTeamPoints);
		tvTime = (TextView) v.findViewById(R.id.tvTeamDuration);

		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		Bundle b = getArguments();
		if (b == null) {
			return;
		}
		Object teamO = getArguments().get("team");
		if (teamO != null && (teamO instanceof WPTeam)) {
			updateUI((WPTeam) teamO);
		}
	}

	private void updateUI(WPTeam team) {

		if (App.getInstance().isOnline()) {
			ITeamDAO dao = App.getInstance().getDAOFactory().getTeamDAO();
			WPTeam t = dao.get(team.getId());
			if (t != null)
				team = t;
		}
		tvTeam.setText(team.getName());
		tvPoints.setText(team.getPoints() + " Punkte");
		tvTime.setText(team.getDurationAsHours());

		List<WPUser> users = team.getUsers();
		if (users == null) {
			users = new ArrayList<WPUser>();
		}
		users = new LinkedList<WPUser>(users);
		Collections.sort(users, new Comparator<WPUser>() {

			public int compare(WPUser lhs, WPUser rhs) {
				if (lhs == rhs)
					return 0;
				if (lhs.getPoints() != rhs.getPoints())
					return (lhs.getPoints() > rhs.getPoints()) ? -1 : 1;
				else
					return -lhs.getName().compareToIgnoreCase(rhs.getName());
			}
		}

		);
		ArrayAdapter<WPUser> adapter = new WPUserArrayAdapter(this.getActivity(), users);
		setListAdapter(adapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		WPUser item = (WPUser) getListAdapter().getItem(position);

		Bundle bundle = new Bundle();
		bundle.putSerializable("user", item);
		UserFragment uf = new UserFragment();
		uf.setArguments(bundle);
		getFragmentManager().beginTransaction().replace(R.id.content_frame, uf).addToBackStack(null).commit();
	}
}

// TODO refactoring to generic class with subclassing and overwriting view @see
// ListEntryActivity for similar adapter
// holder
class WPUserArrayAdapter extends ArrayAdapter<WPUser> {
	private final Activity context;
	private final List<WPUser> users;

	static class ViewHolder {
		public TextView name;
		public TextView points;
		public TextView duration;

	}

	public WPUserArrayAdapter(Activity context, List<WPUser> users) {
		super(context, R.layout.teammemberitem, users);
		this.context = context;
		this.users = users;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(R.layout.teammemberitem, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.name = (TextView) rowView.findViewById(R.id.tvTeamUserName);
			viewHolder.duration = (TextView) rowView.findViewById(R.id.tvTeamUserDuration);
			viewHolder.points = (TextView) rowView.findViewById(R.id.tvTeamUserPoints);
			rowView.setTag(viewHolder);
		}

		ViewHolder holder = (ViewHolder) rowView.getTag();
		WPUser user = users.get(position);
		holder.name.setText(user.getName());
		holder.duration.setText(user.getDurationAsHours());
		holder.points.setText(user.getPoints() + " Punkt(e)");

		return rowView;
	}
}
