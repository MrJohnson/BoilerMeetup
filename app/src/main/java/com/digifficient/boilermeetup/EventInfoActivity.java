package com.digifficient.boilermeetup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by taylor on 12/3/14.
 */
public class EventInfoActivity extends ActionBarActivity implements GoogleMap.OnMarkerClickListener {
    private static final LatLng MAP_HOME = new LatLng(40.424489, -86.921109);
    private GoogleMap eMap;
    private Marker home_marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_view);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (eMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            eMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (eMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        eMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MAP_HOME, 15));
        home_marker = eMap.addMarker(new MarkerOptions().snippet("TEST").position(new LatLng(40.424489, -86.921109)).title("Purdue University"));
        eMap.setOnMarkerClickListener((GoogleMap.OnMarkerClickListener) this);
    }

    @Override
    public boolean onMarkerClick(Marker marker){
        Log.i("GoogleMapActivity", "OnMarkerClick");
        Toast.makeText(getApplicationContext(), "Marker Clicked: " + marker.getTitle() + "\nPosition " + marker.getPosition(), Toast.LENGTH_LONG).show();
        return true;
    }
}
