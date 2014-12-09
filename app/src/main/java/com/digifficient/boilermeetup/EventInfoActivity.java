package com.digifficient.boilermeetup;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

/**
 * Created by taylor on 12/3/14.
 */
public class EventInfoActivity extends ActionBarActivity implements GoogleMap.OnMarkerClickListener {
    private static final LatLng MAP_HOME = new LatLng(40.424489, -86.921109);
    private GoogleMap eMap;
    private Marker home_marker;
    private Event e;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_view);
        e = new Event();
        getEventInfo();
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
        eMap.moveCamera(CameraUpdateFactory.newLatLngZoom(e.getPositiion(), 15));
        Marker eMarker = eMap.addMarker(new MarkerOptions().snippet(Integer.toString(e.getEventId())).position(e.getPositiion()).title("EVENT NAME"));
        eMap.setOnMarkerClickListener((GoogleMap.OnMarkerClickListener) this);
    }

    @Override
    public boolean onMarkerClick(Marker marker){
        Log.i("GoogleMapActivity", "OnMarkerClick");
        String address = getCompleteAddressString(marker.getPosition().latitude, marker.getPosition().longitude);

        //Intent openDirections = new Intent(android.content.Intent.ACTION_VIEW,
                //Uri.parse("google.navigation:q=an+" + address));
        Intent openDirections = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?f=d&daddr="+ String.valueOf(marker.getPosition().latitude)
                            + "," + String.valueOf(marker.getPosition().longitude)));
        startActivity(openDirections);

        //Toast.makeText(getApplicationContext(), "Address: " + address + "\nPosition " + marker.getPosition(), Toast.LENGTH_LONG).show();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.event_info_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_event_info_refresh:
                //send event info to server after checking
                //Toast.makeText(getApplicationContext(), "Clicked Refresh Event", Toast.LENGTH_SHORT).show();
                return true;
            //case R.id.action_settings:
            //openSettings();
            //return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void getEventInfo(){
        Bundle bundle = getIntent().getParcelableExtra("bundle");
        e.setEventId(Integer.parseInt(bundle.getString("id")));
        e.setPosition((LatLng) bundle.getParcelable("position"));
        //Toast.makeText(getApplicationContext(), "Id" + e.getEventId(), Toast.LENGTH_LONG).show();

        //GET STUFF FROM SERVER
        e.setName("TEST NAME");
        e.setType("Generic type");
        e.setDescription("Generic description");
        e.setLocation("Generic location name");
        e.setStartTime("December 99, 9999 25:64 pm");
        e.setNumAttendees(69);

        //Update GUI with values from server
        TextView name = (TextView) findViewById(R.id.EventName);
        name.setText(e.getName());
        TextView type = (TextView) findViewById(R.id.EventType);
        type.setText(e.getType());
        TextView description = (TextView) findViewById(R.id.EventDescription);
        description.setText(e.getDesription());
        TextView date = (TextView) findViewById(R.id.EventDate);
        date.setText(e.getStartTime());
        TextView location = (TextView) findViewById(R.id.EventLocationName);
        location.setText(e.getLocation());
        TextView rsvp = (TextView) findViewById(R.id.rsvp);
        rsvp.setText(e.getNumAttendees() + " have RSVP'd");
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
                Log.w("My Current loction address", "" + strReturnedAddress.toString());
            } else {
                Log.w("My Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current loction address", "Canont get Address!");
        }
        return strAdd;
    }

}
