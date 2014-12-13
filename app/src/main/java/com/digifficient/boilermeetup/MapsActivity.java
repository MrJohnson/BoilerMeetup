package com.digifficient.boilermeetup;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.melnykov.fab.FloatingActionButton;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;


public class MapsActivity extends ActionBarActivity implements OnMarkerClickListener {
    private static final LatLng MAP_HOME = new LatLng(40.423680, -86.921195);
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.



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
            /*
            case R.id.action_search:
                //openSearch();
                Toast.makeText(getApplicationContext(), "Clicked Search", Toast.LENGTH_SHORT).show();
                return true;
                */
            case R.id.action_refresh:
                //get events from server
                //Toast.makeText(getApplicationContext(), "Clicked Refresh", Toast.LENGTH_LONG).show();
                try {
                    FetchEventsTask eventTask = new FetchEventsTask();
                    eventTask.execute();
                }
                catch(Exception e){
                    Log.d(((Object)this).getClass().getSimpleName(), "Exception in getEventsFromServer call - refresh click");
                }
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
        //refreshEvents();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MAP_HOME, 12));
        //home_marker = mMap.addMarker(new MarkerOptions().snippet("0000").position(MAP_HOME).title("Purdue University"));
        //Marker lawson = mMap.addMarker(new MarkerOptions().snippet("0003").position(LWSN).title("Lawson Computer Science Building"));
        mMap.setOnMarkerClickListener((OnMarkerClickListener) this);
       // FetchEventsTask fetchTask
        //Log.d(((Object)this).getClass().getSimpleName(), "IOException in getEventsFromServer call - setUpMap");
        FetchEventsTask eventTask = new FetchEventsTask();
        eventTask.execute();

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



        public class FetchEventsTask extends AsyncTask<Void, Void, LinkedList<Event>>{

        String serverAddress = getString(R.string.server_address);
        private BufferedReader in;
        private PrintWriter out;

        //@Override
        protected LinkedList<Event> doInBackground(Void... params){
            LinkedList<Event> events = new LinkedList<Event>();
            try {
                String line = "";
                StringBuilder total = new StringBuilder();
                //Log.d(((Object)this).getClass().getSimpleName(), "BEFORE SOCKET");
                //Toast.makeText(getApplicationContext(), "Right before try", Toast.LENGTH_LONG).show();
                //Socket socket = null;
                Socket socket = new Socket(serverAddress, 3112);
                //Log.d(((Object)this).getClass().getSimpleName(), "AFTER SOCKET");
                try {
                    //socket = new Socket(serverAddress, 3112);
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    out = new PrintWriter(socket.getOutputStream(), true);
                    //Object ob = null;
                   // Log.d(((Object)this).getClass().getSimpleName(), "BEFORE SENDING");
                    JSONObject obj = new JSONObject();
                    obj.put("command", "GET-ALL-EVENTS");
                    out.println(obj.toString());
                    //Log.d(((Object)this).getClass().getSimpleName(), "AFTER SENDING");
                    //Toast.makeText(getApplicationContext(), "Right after sending to server", Toast.LENGTH_LONG).show();

                    JSONObject jsonObj;

                    while ((line = in.readLine()) != null) {
                        try {
                            Event event = new Event();
                            //Log.d(((Object)this).getClass().getSimpleName(), "LINE " + line);
                            jsonObj = new JSONObject(line);

                            //ob = parser.parse(line);
                            //jsonObj = (JSONObject) ob;
                            event.setEventId(jsonObj.getInt("id"));
                            //Log.d(((Object)this).getClass().getSimpleName(), "TTEESSTTT. LAT: " + jsonObj.getString("lat"));
                            event.setPosition(new LatLng(Double.parseDouble(jsonObj.getString("lat")),Double.parseDouble(jsonObj.getString("longe"))));
                            //Log.d(((Object)this).getClass().getSimpleName(), "AFTER SENDING. ID: " + event.getEventId() + "POS: " + event.getPositiion());
                            //MarkerOptions marker = new MarkerOptions().snippet(Integer.toString(event.getEventId())).position(event.getPositiion());
                            events.add(event);
                        }
                        catch(Exception e){
                            Log.d(((Object)this).getClass().getSimpleName(), "EXCEPTION " + e.getMessage());
                            //e.printStackTrace();
                        }
                    }

                } catch (Exception e) {
                    Log.d(((Object)this).getClass().getSimpleName(), "CAUGHT EXCEPTION");
                    //e.printStackTrace();
                } finally {
                    socket.close();
                    return events;
                }
            }
            catch(IOException e){
                Log.d(((Object)this).getClass().getSimpleName(), "IOException in getEventsFromServer call - setUpMap");
            }
            return events;
        }


        protected void onPostExecute(LinkedList<Event> events){
            for(int i = 0; i < events.size(); i++){
                mMap.addMarker(new MarkerOptions().snippet(Integer.toString(events.get(i).getEventId())).position(events.get(i).getPositiion()));
            }
        }

    }




}
