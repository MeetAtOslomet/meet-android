package no.oslomet.meet;

import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationMenu;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import no.oslomet.meet.core.Helper;

public class ActivityMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((BottomNavigationView)findViewById(R.id.navigation)).setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private int SelectedNavigationItem = 1;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            int prevSelected = SelectedNavigationItem;

            Helper.forceTint(ActivityMain.this, menuItem, R.color.colorPrimaryDark);
            switch (menuItem.getItemId())
            {
                case R.id.navigation_profile:
                {
                    SelectedNavigationItem = 0;
                    //Navigate to profile overview
                    NavigateFragment(new FragmentProfile());
                    break;
                }
                case R.id.navigation_match:
                {
                    SelectedNavigationItem = 1;
                    NavigateFragment(new FragmentRecommended());
                    break;
                }
                case R.id.navigation_chat:
                {
                    SelectedNavigationItem = 2;
                    NavigateFragment(new FragmentChat());
                    break;
                }
            }

            if (SelectedNavigationItem != prevSelected)
            {
                MenuItem prev = ((BottomNavigationView)findViewById(R.id.navigation)).getMenu().getItem(prevSelected);
                Helper.forceTint(ActivityMain.this, prev, R.color.colorAccent);
                //Deselect color
            }

            return true;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        BottomNavigationView bnv = (BottomNavigationView)findViewById(R.id.navigation);
        bnv.getMenu().getItem(SelectedNavigationItem).setChecked(true);
        mOnNavigationItemSelectedListener.onNavigationItemSelected(bnv.getMenu().getItem(SelectedNavigationItem));
    }

    private Fragment prevf = null;
    public void NavigateFragment(Fragment f)
    {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (prevf == null)
        {
            ft.add(R.id.main_frame, f);
        }
        else
        {
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            ft.replace(R.id.main_frame, f);
        }
        ft.commitAllowingStateLoss();
        prevf = f;
    }

}
