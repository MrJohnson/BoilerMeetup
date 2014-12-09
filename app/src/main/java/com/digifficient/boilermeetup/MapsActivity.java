package com.digifficient.boilermeetup;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class MapsActivity extends ActionBarActivity implements OnMarkerClickListener {
    private static final LatLng MAP_HOME = new LatLng(40.423680, -86.921195);
    private static final LatLng LWSN = new LatLng(40.427679, -86.916946);
    String serverAddress = "128.10.25.212";
    private BufferedReader in;
    private PrintWriter out;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    int numEvents;
    //Event[] eventArray;
    List<Marker> markers = new ArrayList<Marker>();
    private Marker home_marker;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.maps_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_search:
                //openSearch();
                Toast.makeText(getApplicationContext(), "Clicked Search", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_refresh:
                Toast.makeText(getApplicationContext(), "Clicked Refresh", Toast.LENGTH_LONG).show();
                return true;
            //case R.id.action_settings:
            //openSettings();
            //return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
/*
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(activity.getResources().getColor(R.color.example_color));
        */

        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        //  getWindow().setStatusBarColor();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "FABulous Click!", Toast.LENGTH_SHORT).show();
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
        refreshEvents();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MAP_HOME, 12));
        home_marker = mMap.addMarker(new MarkerOptions().snippet("0000").position(MAP_HOME).title("Purdue University"));
        Marker lawson = mMap.addMarker(new MarkerOptions().snippet("0003").position(LWSN).title("Lawson Computer Science Building"));
        mMap.setOnMarkerClickListener((OnMarkerClickListener) this);
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.i("GoogleMapActivity", "OnMarkerClick");
        //Toast.makeText(getApplicationContext(), "Marker Clicked: " + marker.getTitle() + "\nPosition " + marker.getPosition(), Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, EventInfoActivity.class);
        Bundle args = new Bundle();
        args.putString("id", marker.getSnippet());
        args.putParcelable("position", marker.getPosition());
        intent.putExtra("bundle", args);
        startActivity(intent);
        return true;
    }


    public void refreshEvents() {
        //communicate with server, populate array of events
        numEvents = 2;
        /*
        eventArray = new Event[numEvents];

        eventArray[0].setEventId(1111);
        eventArray[0].setName("Purdue Event");
        eventArray[0].setLocation("Purdue University");
        eventArray[0].setStartTime("Dec. 25, 2014 @ 12:00 am");
        eventArray[0].setPosition(MAP_HOME);
        eventArray[0].setDescription("This is Purdue University");
        eventArray[0].setNumAttendees(40000);

        eventArray[1].setEventId(1010);
        eventArray[1].setName("CS252 Coding Session");
        eventArray[1].setLocation("LWSN B146");
        eventArray[1].setStartTime("Dec. 8, 2014 @ 2:00 pm");
        eventArray[1].setPosition(LWSN);
        eventArray[1].setDescription("We will meet to continue working on our app!");
        eventArray[1].setNumAttendees(1);
        */

        /*
        for(int i = 0; i < numEvents; i++){
            //Marker snippet is index of event in a
            Marker marker = mMap.addMarker(new MarkerOptions().snippet(Integer.toString(i)).position(eventArray[i].getPositiion()).title(eventArray[i].getName()));
            markers.add(marker);
        }

        markers.size();

        */

    }

    public String getEventsFromServer() throws IOException {
        String line = "";
        StringBuilder total = new StringBuilder();
        Socket socket = new Socket(serverAddress, 3112);
        try {

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            while ((line = in.readLine()) != null) {
                total.append(line);
            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
            return total.toString();
        }

    }


}
