package de.ea.winterpokal;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import de.ea.winterpokal.model.WPTeam;
import de.ea.winterpokal.model.WPUser;

/**
 * Created by ea on 26.11.2017.
 */

public class WPUserArrayAdapter  extends ArrayAdapter<WPUser> {
    private final Activity context;
    private final List<WPUser> teams;

    static class ViewHolder {
        public TextView name;
        public TextView points;
        public TextView duration;

    }

    public WPUserArrayAdapter(Activity context, List<WPUser> users) {
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
            WPTeamArrayAdapter.ViewHolder viewHolder = initViewHolder(rowView);
            rowView.setTag(viewHolder);
        }

        WPTeamArrayAdapter.ViewHolder holder = (WPTeamArrayAdapter.ViewHolder) rowView.getTag();
        if(holder==null) {
            holder = initViewHolder(rowView);
            rowView.setTag(holder);
        }
        WPUser team = teams.get(position);
        if (team != null) {
            holder.name.setText(team.getName());
            holder.duration.setText(team.getDurationAsHours());
            holder.points.setText(team.getPoints() + " Punkt(e)");
        }else {
            Log.e("exo", "team is null");
        }

        return rowView;
    }

    private static WPTeamArrayAdapter.ViewHolder initViewHolder(View rowView){
        WPTeamArrayAdapter.ViewHolder viewHolder = new WPTeamArrayAdapter.ViewHolder();
        viewHolder.name = (TextView) rowView.findViewById(R.id.tvTeamUserName);
        viewHolder.duration = (TextView) rowView.findViewById(R.id.tvTeamUserDuration);
        viewHolder.points = (TextView) rowView.findViewById(R.id.tvTeamUserPoints);
        return viewHolder;
    }
}