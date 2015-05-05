package se.greatbrain.sats.activity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import io.realm.RealmResults;
import se.greatbrain.sats.R;
import se.greatbrain.sats.adapter.DrawerItemClickListener;
import se.greatbrain.sats.adapter.DrawerMenuAdapter;
import se.greatbrain.sats.model.DrawerMenuItem;
import se.greatbrain.sats.model.realm.Center;
import se.greatbrain.sats.realm.RealmClient;

public class GoogleMapActivity extends ActionBarActivity
{
    private GoogleMap map ;
    private Map<Marker, Center> markerCenterMap;
    private AtomicBoolean wasBackPressed = new AtomicBoolean(false);
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);

        map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(
                R.id.map)).getMap();
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.setMyLocationEnabled(true);
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        markerCenterMap = addCenterMarkers(map);

        findCenterDetailView();
        moveCameraToMyLocation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        ActionBar actionBar = getSupportActionBar();
        View actionBarView = getLayoutInflater().inflate(R.layout.action_bar_menu, null);
        actionBar.setCustomView(actionBarView);
        actionBar.setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);

        // remove left actionbar padding
        Toolbar parent = (Toolbar) actionBarView.getParent();
        parent.setContentInsetsAbsolute(0, 0);

        TextView actionBarTitle = (TextView) findViewById(R.id.action_bar_text_view);
        actionBarTitle.setText("HITTA CENTER");
        setOnClickHomeButton();
        setupSlidingMenu();

        return super.onCreateOptionsMenu(menu);
    }

    private void setupSlidingMenu()
    {
        ListView drawerMenu = (ListView) findViewById(R.id.drawer_menu);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerMenu.setAdapter(new DrawerMenuAdapter(this, populateDrawerList()));
        drawerMenu.setOnItemClickListener(new DrawerItemClickListener(drawerLayout));
    }

    private List<DrawerMenuItem> populateDrawerList()
    {
        List<DrawerMenuItem> items = new ArrayList<>();
        items.add(new DrawerMenuItem(R.drawable.my_training, "min träning"));
        items.add(new DrawerMenuItem(R.drawable.sats_pin_drawer_menu, "hitta center"));

        return items;
    }

    private void findCenterDetailView() {
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener()
        {
            @Override
            public void onInfoWindowClick(Marker marker)
            {
                Intent intent = new Intent(getApplicationContext(), FindCenterDetailActivity.class);
                intent.putExtra("centerUrl", markerCenterMap.get(marker).getUrl());
                startActivity(intent);
            }
        });
    }

    private void moveCameraToMyLocation()
    {
        GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap
                .OnMyLocationChangeListener()
        {
            boolean moveToMyLocation = true;
            @Override
            public void onMyLocationChange(Location location)
            {
                LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());

                if (map != null && moveToMyLocation)
                {
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 10.0f));
                    moveToMyLocation=false;
                }
            }
        };
        map.setOnMyLocationChangeListener(myLocationChangeListener);
    }

    private Map<Marker, Center> addCenterMarkers(GoogleMap map)
    {
        Map<Marker, Center> markerCenterMap = new HashMap<>();

        if (map != null)
        {
            RealmClient realmClient = RealmClient.getInstance(this);
            RealmResults<Center> centers = realmClient.getAllCenters();

            for (Center center : centers)
            {
                LatLng satsLocation = new LatLng(center.getLat(), center.getLng());
                Marker satsMarker = map.addMarker(new MarkerOptions()
                        .position(satsLocation)
                        .title(center.getName())
                        .flat(true)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.sats_pin_normal)));
                markerCenterMap.put(satsMarker, center);
            }
        }
        return markerCenterMap;
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
                        if(drawer.isDrawerOpen(GravityCompat.START))
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
        if(wasBackPressed.getAndSet(true))
        {
            super.onBackPressed();
        }
        else
        {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }
}

