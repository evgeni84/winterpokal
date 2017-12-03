package de.ea.winterpokal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import de.ea.winterpokal.utils.web.auth.Auth;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
	NavigationView navigationView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);


		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
		drawer.addDrawerListener(toggle);
		toggle.syncState();

		navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);
		setFirstItemNavigationView();
	}

	private void setFirstItemNavigationView() {
		navigationView.setCheckedItem(R.id.nav_home);
		onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_home));
	}

	@Override
	public void onBackPressed() {
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			FragmentManager fragmentManager = getSupportFragmentManager();
			if (fragmentManager.getBackStackEntryCount() > 1) {
				fragmentManager.popBackStackImmediate();
			} else {
				finish();
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
			// Respond to the action bar's Up/Home button
			case android.R.id.home:
				onBackPressed();
				//noinspection SimplifiableIfStatement
			case R.id.action_settings:
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		// Handle navigation view item clicks here.
		int id = item.getItemId();
		Class fragmentClass = null;
		if (id == R.id.nav_home) {
			fragmentClass = UserFragment.class;
		} else if (id == R.id.nav_ranking) {
			fragmentClass = RankingFragment.class;
		} else if (id == R.id.nav_favorites) {
			fragmentClass = FavoriteFragment.class;
		} else if (id == R.id.nav_logout) {
			if(Auth.logout()){
				Intent newActivity = new Intent(this, StartupActivity.class);
				newActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				this.startActivity(newActivity);
				finish();
				return true;
			}
		}

		if(fragmentClass!=null) {
			Fragment fragment = null;
			try {
				fragment = (Fragment) fragmentClass.newInstance();
				FragmentManager fragmentManager = getSupportFragmentManager();
				fragmentManager.beginTransaction().replace(R.id.flContent, fragment).addToBackStack(null).commit();
				item.setChecked(true);
				setTitle(item.getTitle());
			} catch (Exception e) {
			}
		}

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}
}
