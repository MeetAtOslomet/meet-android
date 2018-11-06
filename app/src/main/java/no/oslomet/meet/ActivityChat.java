package no.oslomet.meet;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.text.SpannableString;

public class ActivityChat extends AppCompatActivity {


    private MenuItem menuChat;
    private Drawable iconChat;
    private BottomNavigationItemView launchMyProfile;
    private BottomNavigationItemView launchMatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        launchMyProfile = findViewById(R.id.navigation_profile);
        launchMyProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(ActivityChat.this, ActivityMyProfile.class));
            }
        });

        launchMatch = findViewById(R.id.navigation_match);
        launchMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityChat.this, ActivityMatch.class));
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.top_menu, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        menuChat = menu.findItem(R.id.navigation_chat);
        iconChat = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_chat_yellow_24dp);
        iconChat = DrawableCompat.wrap(iconChat);
        DrawableCompat.setTint(iconChat, ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
        menuChat.setIcon(iconChat);

        SpannableString spanString = new SpannableString(menuChat.getTitle().toString());
        spanString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getApplicationContext(), R.color.colorSecondary)), 0, spanString.length(), 0);
        menuChat.setTitle(spanString);

        return true;
    }



}
