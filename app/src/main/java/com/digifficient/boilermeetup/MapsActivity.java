package com.digifficient.boilermeetup;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.melnykov.fab.FloatingActionButton;


public class MapsActivity extends ActionBarActivity implements OnMarkerClickListener{
    private static final LatLng MAP_HOME = new LatLng(40.424489, -86.921109);
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Marker home_marker;
    ImageButton runCommand;

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);

        return super.onCreateOptionsMenu(menu);
    }
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(getApplicationContext(), "FABulous Click!", Toast.LENGTH_SHORT).show();
                Intent nintent = new Intent(v.getContext(), AddEventActivity.class);
                //Intent urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.cs.purdue.edu"));
                startActivity(nintent);
            }
        });
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MAP_HOME, 12));
        home_marker = mMap.addMarker(new MarkerOptions().snippet("TEST").position(new LatLng(40.424489, -86.921109)).title("Purdue University"));
        mMap.setOnMarkerClickListener((OnMarkerClickListener) this);
    }



    @Override
    public boolean onMarkerClick(Marker marker){
        Log.i("GoogleMapActivity", "OnMarkerClick");
        Toast.makeText(getApplicationContext(), "Marker Clicked " + marker.getTitle() + "\nPosition " + marker.getPosition(), Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, EventInfoActivity.class);
        //Intent urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.cs.purdue.edu"));
        startActivity(intent);
        return true;
    }


}
