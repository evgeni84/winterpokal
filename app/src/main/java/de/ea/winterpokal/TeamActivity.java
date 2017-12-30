package de.ea.winterpokal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import de.ea.winterpokal.model.WPTeam;
import de.ea.winterpokal.model.WPUser;
import de.ea.winterpokal.persistence.ITeamDAO;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class TeamActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

	private TextView tvTeam = null;
	private TextView tvPoints = null;
	private TextView tvTime = null;
	private ListView teamEntryList = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.team);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		tvTeam = (TextView) findViewById(R.id.tvTeam);
		tvPoints = (TextView) findViewById(R.id.tvTeamPoints);
		tvTime = (TextView) findViewById(R.id.tvTeamDuration);
		teamEntryList = (ListView)findViewById(R.id.teamEntryList);
		teamEntryList.setOnItemClickListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		Object teamO = getIntent().getSerializableExtra("team");
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
		tvPoints.setText(team.getPoints()+"");
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
		ArrayAdapter<WPUser> adapter = new WPUserArrayAdapter(this, users);
		teamEntryList.setAdapter(adapter);
	}
	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
		WPUser item = (WPUser) teamEntryList.getAdapter().getItem(i);
		Intent intent = new Intent(this, UserActivity.class);
		intent.putExtra("user", item);
		startActivity(intent);

	}
}

// TODO refactoring to generic class with subclassing and overwriting view @see
// ListEntryActivity for similar adapter
// holder
class WPUserArrayAdapter2 extends ArrayAdapter<WPUser> {
	private final Activity context;
	private final List<WPUser> users;

	static class ViewHolder {
		public TextView name;
		public TextView points;
		public TextView duration;

	}

	public WPUserArrayAdapter2(Activity context, List<WPUser> users) {
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
		holder.points.setText(user.getPoints() + "");

		return rowView;
	}
}
