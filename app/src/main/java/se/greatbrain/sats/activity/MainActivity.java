package se.greatbrain.sats.activity;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import de.greenrobot.event.EventBus;
import se.greatbrain.sats.R;
import se.greatbrain.sats.adapter.DrawerMenuAdapter;
import se.greatbrain.sats.adapter.DrawerMenuListener;
import se.greatbrain.sats.event.JsonParseCompleteEvent;
import se.greatbrain.sats.event.ServerErrorEvent;
import se.greatbrain.sats.fragment.GraphColumnFragment;
import se.greatbrain.sats.fragment.GraphFragment;
import se.greatbrain.sats.fragment.TopViewPagerFragment;
import se.greatbrain.sats.fragment.WorkoutListFragment;
import se.greatbrain.sats.ion.IonClient;
import se.greatbrain.sats.model.DrawerMenuItem;

public class MainActivity extends AppCompatActivity implements GraphColumnFragment
        .OnPageClickedListener
{
    private static final String TAG = "MainActivity";
    private DrawerLayout drawerLayout;
    private MenuItem reloadButton;
    private WorkoutListFragment workoutListFragment;
    private GraphFragment graphFragment;
    private TopViewPagerFragment topViewPagerFragment;
    private HashSet<String> finishedJsonParseEvents = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadJsonDataFromWeb();
        EventBus.getDefault().register(this);

        FragmentManager manager = getFragmentManager();
        android.support.v4.app.FragmentManager supportManager = getSupportFragmentManager();

        workoutListFragment = new WorkoutListFragment();
        graphFragment = new GraphFragment();
        topViewPagerFragment = new TopViewPagerFragment();
        supportManager.beginTransaction()
                .add(R.id.top_fragment_container, topViewPagerFragment)
                .add(R.id.bottom_fragment_container, workoutListFragment)
                .commit();
    }

    @Override
    protected void onDestroy()
    {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void loadJsonDataFromWeb()
    {
        Log.d(TAG, "loadJsonDataFromWeb");
        IonClient.getInstance(this).getAllData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_main, menu);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);

        View actionBarView = getLayoutInflater().inflate(R.layout.action_bar_menu, null);
        actionBar.setCustomView(actionBarView);
        actionBar.setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);

        // remove left actionbar padding
        android.support.v7.widget.Toolbar parent = (android.support.v7.widget.Toolbar)
                actionBarView.getParent();
        parent.setContentInsetsAbsolute(0, 0);

        reloadButton = menu.findItem(R.id.action_bar_refresh_button);
        setupReloadItemMenu();
        setupSlidingMenu();
        setOnClickHomeButton();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.action_bar_refresh_button)
        {
            setupReloadItemMenu();
            loadJsonDataFromWeb();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupReloadItemMenu()
    {
        Animation reloadAnimation = AnimationUtils.loadAnimation(this, R.anim.reload_rotate);
        reloadButton.setActionView(R.layout.action_bar_reloading);

        ImageView imageView = (ImageView) reloadButton.getActionView()
                .findViewById(R.id.action_bar_refresh_button_reloading);

        imageView.startAnimation(reloadAnimation);
    }

    public void onEventMainThread(JsonParseCompleteEvent event)
    {
        Log.d("jsonEvent", event.getSourceEvent());
        if (finishedJsonParseEvents.add(event.getSourceEvent()))
        {
            if (finishedJsonParseEvents.size() == 6)
            {
                finishedJsonParseEvents.clear();
                updateWorkoutListFragment();
            }
        }
    }

    public void onEventMainThread(ServerErrorEvent event)
    {
        Toast.makeText(this, event.getMessage(), Toast.LENGTH_LONG).show();
    }

    private void updateWorkoutListFragment()
    {
        reloadButton.setActionView(null);
        workoutListFragment.refreshList();
    }

    @Override
    public void onPageClicked(int page)
    {
        topViewPagerFragment.onPageClicked(page);
    }

    private void setupSlidingMenu()
    {
        ListView drawerMenu = (ListView) findViewById(R.id.drawer_menu);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerMenu.setAdapter(new DrawerMenuAdapter(this, populateDrawerList()));
        DrawerMenuListener listener = new DrawerMenuListener(this);
        drawerMenu.setOnItemClickListener(listener);
        drawerLayout.setDrawerListener(listener);
    }

    private List<DrawerMenuItem> populateDrawerList()
    {
        List<DrawerMenuItem> items = new ArrayList<>();
        items.add(new DrawerMenuItem(R.drawable.my_training, "min träning"));
        items.add(new DrawerMenuItem(R.drawable.sats_pin_drawer_menu, "hitta center"));

        return items;
    }

    private void setOnClickHomeButton()
    {
        ImageView menuIcon = (ImageView) findViewById(R.id.btn_dots_logo_sats_menu);
        menuIcon.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START))
                        {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                        else
                        {
                            drawer.openDrawer(GravityCompat.START);
                        }
                    }
                });
    }

    @Override
    public void onBackPressed()
    {
        if (DrawerMenuListener.wasBackPressed)
        {
            super.onBackPressed();
            DrawerMenuListener.wasBackPressed = false;
        }
        else
        {
            if (drawerLayout.isDrawerOpen(GravityCompat.START))
            {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
            else
            {
                drawerLayout.openDrawer(GravityCompat.START);
                DrawerMenuListener.wasBackPressed = true;
            }
        }
    }
}

