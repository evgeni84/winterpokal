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

/**
 * Created by ea on 26.11.2017.
 */

public class WPTeamArrayAdapter extends ArrayAdapter<WPTeam> {
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