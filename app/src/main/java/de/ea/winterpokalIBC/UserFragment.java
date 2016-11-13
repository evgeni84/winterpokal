package de.ea.winterpokalIBC;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import de.ea.winterpokalIBC.model.WPEntry;
import de.ea.winterpokalIBC.model.WPTeam;
import de.ea.winterpokalIBC.model.WPUser;
import de.ea.winterpokalIBC.utils.web.auth.Auth;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import android.widget.TextView;

public class UserFragment extends ListFragment implements OnClickListener {

	private View teamContainer;
	private TextView teamLabel, pointsLabel, userNameLabel, durationLabel;
	private WPUser user;
	private ProgressDialog pDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.user, null);

		teamContainer = v.findViewById(R.id.trTeam);
		teamLabel = (TextView) v.findViewById(R.id.tvTeam);
		pointsLabel = (TextView) v.findViewById(R.id.tvPoints);
		userNameLabel = (TextView) v.findViewById(R.id.tvUserName);
		durationLabel = (TextView) v.findViewById(R.id.tvTotalTime);
		teamContainer.setOnClickListener(this);

		return v;
	}

	public void onClick(View v) {
		if (user != null && user.getTeam() != null) {
			TeamFragment tf = new TeamFragment();
			Bundle bundle = new Bundle();
			bundle.putSerializable("team", user.getTeam());
			tf.setArguments(bundle);
			getFragmentManager().beginTransaction().replace(R.id.content_frame, tf).addToBackStack(null).commit();

		}
	}

	@Override
	public void onResume() {
		super.onResume();

		user = null;

		Object userO = null;

		if (getArguments() != null)
			userO = getArguments().get("user");
		if (userO == null)
			user = Auth.getUser();
		else if (userO instanceof WPUser) {
			user = (WPUser) userO;
		}
		if (user != null)
			new GetDataTask().execute(user);
	}

	private void updateUI(WPUser user, List<WPEntry> entries) {
		this.user = user;
		WPTeam team = user.getTeam();
		teamLabel.setText(team != null ? team.getName() : "");
		userNameLabel.setText(user.getName());
		pointsLabel.setText(user.getPoints() + " Punkte");
		durationLabel.setText(user.getDurationAsHours());

		ArrayAdapter<WPEntry> adapter = new MyEntryArrayAdapter(this.getActivity(), entries);
		setListAdapter(adapter);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		WPUser authUser = Auth.getUser();
		if (authUser == null || user == null || authUser.getId() != user.getId()) {
			return;
		}
		inflater.inflate(R.menu.activity_user, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// handle item selection
		switch (item.getItemId()) {
		case R.id.action_add_entry:
			getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new AddEntryFragment()).addToBackStack(null).commit();

			// do s.th.
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	class GetDataTask extends AsyncTask<WPUser, Void, Void> {

		private List<WPEntry> entries = null;
		private WPUser updatedUser = null;

		public GetDataTask() {
		}

		@Override
		protected void onPreExecute() {
			// Showing progress dialog before sending http request
			FragmentActivity a = UserFragment.this.getActivity();
			pDialog = new ProgressDialog(a);
			pDialog.setMessage(a.getText(R.string.loading));
			pDialog.setIndeterminate(true);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected void onPostExecute(Void unused) {
			pDialog.dismiss();
			UserFragment.this.updateUI(updatedUser, entries);
		}

		@Override
		protected Void doInBackground(final WPUser... arg0) {

			try {
				if (user == null)
					user = Auth.getUser();

			} catch (Exception ex) {
				Log.e("retreaveUserData", "init", ex);
			}
			try {
				entries = App.getInstance().getDAOFactory().getEntryDAO().getForUser(user.getId(), 50);
			} catch (Exception ex) {
				Log.e("GettingUserEntries", ex.getMessage(), ex);
			}

			entries = entries == null ? new LinkedList<WPEntry>() : new LinkedList<WPEntry>(entries);


			try {
				updatedUser = App.getInstance().getDAOFactory().getUserDAO().get(user.getId());
			} catch (Exception ex) {
				Log.e("GettingUser", ex.getMessage(), ex);
			}

			return null;
		}
	}

}

class MyEntryArrayAdapter extends ArrayAdapter<WPEntry> {
	private final Activity context;
	private final List<WPEntry> entries;
	private SimpleDateFormat format;

	static class ViewHolder {
		public TextView user;
		public TextView duration;
		public TextView points;
		public TextView descr;
		public TextView date;
		public ImageView sportType;

	}

	public MyEntryArrayAdapter(Activity context, List<WPEntry> users) {
		super(context, R.layout.entrylistitem, users);
		this.context = context;
		this.entries = users;
		format = new SimpleDateFormat("dd.MM.yyyy");
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(R.layout.entrylistitem, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.user = (TextView) rowView.findViewById(R.id.tvUser);
			viewHolder.date = (TextView) rowView.findViewById(R.id.tvDate);
			viewHolder.descr = (TextView) rowView.findViewById(R.id.tvDescr);
			viewHolder.duration = (TextView) rowView.findViewById(R.id.tvDuration);
			viewHolder.points = (TextView) rowView.findViewById(R.id.tvPoints);
			viewHolder.sportType = (ImageView) rowView.findViewById(R.id.ivSportType);
			rowView.setTag(viewHolder);
		}

		ViewHolder holder = (ViewHolder) rowView.getTag();

		WPEntry entry = entries.get(position);
		holder.user.setText("");
		holder.duration.setText(entry.getDurationAsHours() + "");
		holder.points.setText(entry.getPoints() + "");
		holder.descr.setText(entry.getDescription());
		holder.date.setText(format.format(entry.getDate()));

		switch (entry.getCategory()) {
		case radfahren:
			holder.sportType.setImageResource(R.drawable.cycling);
			break;
		case laufen:
			holder.sportType.setImageResource(R.drawable.running);
			break;
		case skilanglauf:
			holder.sportType.setImageResource(R.drawable.skiing);
			break;
		default:
			holder.sportType.setImageResource(R.drawable.empty);
			break;
		}

		return rowView;
	}

}