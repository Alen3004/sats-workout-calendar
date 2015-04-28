package se.greatbrain.sats;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.HashSet;

import de.greenrobot.event.EventBus;
import se.greatbrain.sats.event.JsonParseCompleteEvent;
import se.greatbrain.sats.event.ServerErrorEvent;
import se.greatbrain.sats.fragment.WorkoutListFragment;
import se.greatbrain.sats.ion.IonClient;

public class MainActivity extends ActionBarActivity
{
    private static final String TAG_LOG = "MainActivity";
    private MenuItem reloadButton;
    private WorkoutListFragment workoutListFragment;
    private HashSet<String> finishedJsonParseEvents = new HashSet<>();
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadJsonDataFromWeb();
        EventBus.getDefault().register(this);

        FragmentManager manager = getFragmentManager();
        workoutListFragment = new WorkoutListFragment();
        manager.beginTransaction().add(R.id.bottom_fragment_container,
                workoutListFragment).commit();
    }

    @Override
    protected void onDestroy()
    {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void loadJsonDataFromWeb()
    {
        Log.d(TAG_LOG, "loadJsonDataFromWeb");
        IonClient.getInstance(this).getAllData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.

        this.menu = menu;

        getMenuInflater().inflate(R.menu.menu_main, menu);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);

        View actionBarView = getLayoutInflater().inflate(R.layout.action_bar_menu, null);
        actionBar.setCustomView(actionBarView);
        actionBar.setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);

        reloadButton = menu.findItem(R.id.action_bar_refresh_button);
        setupReloadItemMenu();

        return true;
    }

    public void onEventMainThread(ServerErrorEvent event)
    {
        Toast.makeText(this, event.getMessage(), Toast.LENGTH_LONG).show();
    }

    private void setupReloadItemMenu()
    {
        final Animation reloadAnimation = AnimationUtils.loadAnimation(this, R.anim.reload_rotate);
        reloadButton.setActionView(R.layout.action_bar_reloading);

        final ImageView imageView = (ImageView) reloadButton.getActionView().findViewById(R.id
                .action_bar_refresh_button_reloading);
        imageView.startAnimation(reloadAnimation);

        reloadButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                reloadButton.setActionView(R.layout.action_bar_reloading);
                imageView.startAnimation(reloadAnimation);
                loadJsonDataFromWeb();
                return true;
            }
        });
    }

    private void setReloadButtonListener()
    {
        final Animation reloadAnimation = AnimationUtils.loadAnimation(this, R.anim.reload_rotate);
        reloadButton.setActionView(R.layout.action_bar_reloading);

        final ImageView imageView = (ImageView) reloadButton.getActionView().findViewById(R.id
                .action_bar_refresh_button_reloading);

        reloadButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                reloadButton.setActionView(R.layout.action_bar_reloading);
                imageView.startAnimation(reloadAnimation);
                loadJsonDataFromWeb();
                return true;
            }
        });
    }

    public void onEventMainThread(JsonParseCompleteEvent event)
    {
        Log.d("jsonEvent", event.getSourceEvent());
        if (finishedJsonParseEvents.add(event.getSourceEvent()))
        {
            if (finishedJsonParseEvents.size() == 6)
            {
                updateWorkoutListFragment();
            }
        }
    }

    private void updateWorkoutListFragment()
    {
        Toast.makeText(this, "Updated list with new data", Toast.LENGTH_LONG).show();
        reloadButton.setActionView(null);
        setReloadButtonListener();
        workoutListFragment.refreshList();
    }
}
