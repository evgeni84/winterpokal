package de.ea.winterpokal;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import de.ea.winterpokal.UserFragment;
import de.ea.winterpokal.model.WPUser;

/**
 * Created by ea on 26.11.2017.
 */

public class UserActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.useractivity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        UserFragment fragment =  (UserFragment)getSupportFragmentManager().findFragmentById(R.id.user_fragment);
        Bundle b = new Bundle();
        WPUser user  = (WPUser)getIntent().getSerializableExtra("user");
        b.putSerializable("user", user);
        fragment.setArguments(b);

        // If not already added to the Fragment manager add it. If you don't do this a new Fragment will be added every time this method is called (Such as on orientation change)
/*
        if(savedInstanceState == null) {
            UserFragment fragment = new UserFragment();
            WPUser user  = (WPUser)getIntent().getSerializableExtra("user");
            Bundle b = new Bundle();
            b.putSerializable("user", user);
            fragment.setArguments(b);
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, fragment).commit();
        }
        */
    }
}
