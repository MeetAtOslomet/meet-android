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
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class ActivityMatch extends AppCompatActivity {


    private MenuItem menuMatch;
    private Drawable iconMatch;
    private BottomNavigationItemView launchMyProfile;
    private BottomNavigationItemView lauchChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        launchMyProfile = findViewById(R.id.navigation_profile);
        launchMyProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityMatch.this, ActivityMyProfile.class));
            }
        });

        lauchChat = findViewById(R.id.navigation_chat);
        lauchChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityMatch.this, ActivityChat.class));
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

        menuMatch = menu.findItem(R.id.navigation_match);
        iconMatch = ContextCompat.getDrawable(this, R.drawable.ic_match_cards_yellow);
        iconMatch = DrawableCompat.wrap(iconMatch);
        DrawableCompat.setTint(iconMatch, ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
        menuMatch.setIcon(iconMatch);

        SpannableString spanString = new SpannableString(menuMatch.getTitle().toString());
        spanString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getApplicationContext(), R.color.colorSecondary)), 0, spanString.length(), 0);
        menuMatch.setTitle(spanString);


    return true;
    }
}
