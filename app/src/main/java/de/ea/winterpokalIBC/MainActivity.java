package de.ea.winterpokalIBC;

import java.util.ArrayList;

import de.ea.winterpokalIBC.model.WPTeam;
import de.ea.winterpokalIBC.model.WPUser;
import de.ea.winterpokalIBC.utils.nav.*;
import de.ea.winterpokalIBC.utils.web.auth.Auth;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AbstractNavDrawerActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new UserFragment()).commit();
		}
	}  

	
	@Override
	protected NavDrawerActivityConfiguration getNavDrawerConfiguration() {

		ArrayList<NavDrawerItem> menuArray = new ArrayList<NavDrawerItem>();
		menuArray.add(NavMenuItem.create(101, "Home", "home", true, this));
		WPUser user = Auth.getUser();
		if (user != null) {
			if (user.getTeam() != null) {
				menuArray.add(NavMenuItem.create(103, "Mein Team", "team", true, this));
			}
		}
		// NavMenuSection.create( 100, "Demos"),
		menuArray.add(NavMenuItem.create(102, "Ranking", "podium", true, this));
		// NavMenuSection.create(200, "General"),

		menuArray.add(NavMenuItem.create(104, "Favoriten", "star", true, this));
		menuArray.add(NavMenuItem.create(105, "Abmelden", "logout", true, this));
		NavDrawerActivityConfiguration navDrawerActivityConfiguration = new NavDrawerActivityConfiguration();
		navDrawerActivityConfiguration.setMainLayout(R.layout.activity_main);
		navDrawerActivityConfiguration.setDrawerLayoutId(R.id.drawer_layout);
		navDrawerActivityConfiguration.setLeftDrawerId(R.id.left_drawer);

		NavDrawerItem[] menu = menuArray.toArray(new NavDrawerItem[] {});

		navDrawerActivityConfiguration.setNavItems(menu);
		navDrawerActivityConfiguration.setDrawerShadow(R.drawable.drawer_shadow);
		navDrawerActivityConfiguration.setDrawerOpenDesc(R.string.drawer_open);
		navDrawerActivityConfiguration.setDrawerCloseDesc(R.string.drawer_close);
		navDrawerActivityConfiguration.setBaseAdapter(new NavDrawerAdapter(this, R.layout.navdrawer_item, menu));
		return navDrawerActivityConfiguration;
	}

	@Override
	protected void onNavItemSelected(int id) {
		switch ((int) id) {
		case 101:
			getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new UserFragment()).commit();
			break;
		case 102:
			getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new RankingFragment()).commit();
			break;
		case 103:
			WPUser user = Auth.getUser();
			if (user != null) {

				WPTeam team = user.getTeam();
				if (team != null) {
					TeamFragment tf = new TeamFragment();
					Bundle b = new Bundle();
					b.putSerializable("team", team);
					tf.setArguments(b);
					getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, tf).commit();
				}
			}
			break;

		case 104:
			getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new FavoriteFragment()).commit();
			break;
		
		case 105:
			if(Auth.logout()){
				Intent newActivity = new Intent(this, StartupActivity.class);
				newActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				this.startActivity(newActivity);
				finish();
			}
			break;
		}
	}

	
}
