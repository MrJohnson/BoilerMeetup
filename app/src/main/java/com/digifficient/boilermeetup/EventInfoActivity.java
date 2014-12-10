package com.digifficient.boilermeetup;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
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

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Created by taylor on 12/3/14.
 */
public class EventInfoActivity extends ActionBarActivity implements GoogleMap.OnMarkerClickListener {
    private static final LatLng MAP_HOME = new LatLng(40.424489, -86.921109);
    private GoogleMap eMap;
    private Marker home_marker;
    public Event e = new Event();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_view);
        //e = new Event();
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
        try {
            FetchEventTask eventTask = new FetchEventTask();
            eventTask.execute(e.getEventId());
        }
        catch(Exception e){
            Log.d(((Object)this).getClass().getSimpleName(), "Exception in getEventsFromServer call - refresh click");
        }

        //READ THIS
        //in onPostExecute I just set the event i made in background = to global event
        //then I kept the code we already had to set event parameters on screen
        //all values i set for the event in background are now null though, not sure why

        //Update GUI with values from server
        TextView name = (TextView) findViewById(R.id.EventName);
        name.setText(e.getName());
        Log.d(((Object)this).getClass().getSimpleName(), "!!!NAME: " + e.getName() + " ID: " + e.getEventId());
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


    public class FetchEventTask extends AsyncTask<Integer, Void, Event> {

        String serverAddress = "128.10.12.141";
        private BufferedReader in;
        private PrintWriter out;

        //@Override
        protected Event doInBackground(Integer... params){
            Event event = new Event();
            try {
                String line = "";
                Socket socket = new Socket(serverAddress, 3112);
                try {
                    //socket = new Socket(serverAddress, 3112);
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    out = new PrintWriter(socket.getOutputStream(), true);
                    JSONObject obj = new JSONObject();
                    obj.put("command", "GET-EVENT-INFO");
                    int x = 5;
                    obj.put("id", (int)params[0]);
                    //Log.d(((Object)this).getClass().getSimpleName(), "!!!PARAMS: " + (int)params[0]);
                    out.println(obj.toString());

                    JSONObject jsonObj;

                    line = in.readLine();
                    //Log.d(((Object)this).getClass().getSimpleName(), "!!!LINE: " + line);
                    try {
                        jsonObj = new JSONObject(line);
                        event.setEventId(jsonObj.getInt("id"));
                        event.setPosition(new LatLng(Double.parseDouble(jsonObj.getString("lat")),Double.parseDouble(jsonObj.getString("longe"))));
                        event.setName(jsonObj.getString("name"));
                        //Log.d(((Object)this).getClass().getSimpleName(), "!!!NAME: " + event.getName());
                        event.setLocation(jsonObj.getString("location"));
                        //Log.d(((Object)this).getClass().getSimpleName(), "!!!LOCATION: " + event.getLocation());
                        event.setDescription(jsonObj.getString("description"));
                        //Log.d(((Object)this).getClass().getSimpleName(), "!!!DESCRIPTION: " + event.getDesription());
                        event.setStartTime(jsonObj.getString("startTime"));
                        //Log.d(((Object)this).getClass().getSimpleName(), "!!!STARTTIME: " + event.getStartTime());
                        event.setNumAttendees(jsonObj.getInt("numAttendees"));
                        //Log.d(((Object)this).getClass().getSimpleName(), "!!!NUMATTENDEES: " + event.getNumAttendees());
                        //Log.d(((Object)this).getClass().getSimpleName(), "AFTER SENDING. ID: " + event.getEventId() + "POS: " + event.getPositiion());
                    }
                    catch(Exception e){
                        Log.d(((Object)this).getClass().getSimpleName(), "EXCEPTION " + e.getMessage());
                    }

                } catch (Exception e) {
                    Log.d(((Object)this).getClass().getSimpleName(), "CAUGHT EXCEPTION");
                    //e.printStackTrace();
                } finally {
                    socket.close();
                    return event;
                }
            }
            catch(IOException e){
                Log.d(((Object)this).getClass().getSimpleName(), "IOException in getEventsFromServer call - setUpMap");
            }
            return event;
        }


        protected void onPostExecute(Event event){
            //eMap.addMarker(new MarkerOptions().snippet(Integer.toString(event.getEventId())).position(event.getPositiion()));
            Log.d(((Object)this).getClass().getSimpleName(), "EVENT !!!NAME: " + event.getName() + " ID: " + event.getEventId());
            e = event;
            Log.d(((Object)this).getClass().getSimpleName(), "E !!!NAME: " + e.getName() + " ID: " + e.getEventId());
            //this is setting e correctly? just not when it's accessed in the other function

        }

    }

}
