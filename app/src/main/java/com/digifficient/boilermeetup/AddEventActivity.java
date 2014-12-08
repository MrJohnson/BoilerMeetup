package com.digifficient.boilermeetup;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by taylor on 12/3/14.
 */
public class AddEventActivity extends ActionBarActivity implements OnMarkerDragListener{

    private static final LatLng MAP_HOME = new LatLng(40.423680, -86.921195);
    private GoogleMap mMap;
    private Marker home_marker;
    private Event e;
    Calendar myCalendar = Calendar.getInstance();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_event);
        setUpMapIfNeeded();
        mMap.setOnMarkerDragListener((OnMarkerDragListener) this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_event_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_accept_event:
                //send event info to server after checking
                Toast.makeText(getApplicationContext(), "Clicked Select", Toast.LENGTH_SHORT).show();
                return true;
            //case R.id.action_settings:
            //openSettings();
            //return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.add_event_map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MAP_HOME, 12));
        home_marker = mMap.addMarker(new MarkerOptions().position(MAP_HOME).draggable(true).title("Drag to location"));
        home_marker.showInfoWindow();
        mMap.setOnMarkerDragListener((GoogleMap.OnMarkerDragListener) this);
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        LatLng position=marker.getPosition();

        Log.d(getClass().getSimpleName(), String.format("Drag from %f:%f",
                position.latitude,
                position.longitude));
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        LatLng position=marker.getPosition();
        marker.hideInfoWindow();

        Log.d(getClass().getSimpleName(),
                String.format("Dragging to %f:%f", position.latitude,
                        position.longitude));

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        LatLng position=marker.getPosition();
        //  Toast.makeText(getApplicationContext(), "Marker Dragged to: " + marker.getPosition(), Toast.LENGTH_LONG).show();
        Log.d(getClass().getSimpleName(), String.format("Dragged to %f:%f",
                position.latitude,
                position.longitude));
        String address = getCompleteAddressString(position.latitude,position.longitude);
        EditText location = (EditText) findViewById(R.id.location_name_field);
        String customLocName = location.getText().toString();
        if(customLocName.trim().length() == 0 ){
            Toast.makeText(getApplicationContext(), "NO ADDRESS SPECIFIED BY USER" , Toast.LENGTH_SHORT).show();
            location.setText(address, TextView.BufferType.EDITABLE);
        }
        Toast.makeText(getApplicationContext(), "Marker Dragged to: " + position + "\nAddress:\n" + address , Toast.LENGTH_LONG).show();

    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current location address", "" + strReturnedAddress.toString());
            } else {
                Log.w("My Current location address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current location address", "Cannot get Address!");
        }
        return strAdd;
    }




}
