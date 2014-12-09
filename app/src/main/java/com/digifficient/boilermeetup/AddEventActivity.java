package com.digifficient.boilermeetup;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
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
    private int hasChanged = 0;
    Calendar myCalendar = Calendar.getInstance();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_event);
        setUpMapIfNeeded();
        mMap.setOnMarkerDragListener((OnMarkerDragListener) this);

        EditText loc = (EditText) findViewById(R.id.location_name_field);
        loc.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                hasChanged = 1;
                //Toast.makeText(getApplicationContext(), "text has been edited" , Toast.LENGTH_SHORT).show();
            }
        });

        EditText edittext = (EditText) findViewById(R.id.event_date_field);
        edittext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //To show current date in the datepicker
                Calendar mcurrentDate=Calendar.getInstance();
                int mYear=mcurrentDate.get(Calendar.YEAR);
                int mMonth=mcurrentDate.get(Calendar.MONTH);
                int mDay=mcurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker=new DatePickerDialog(AddEventActivity.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        // TODO Auto-generated method stub
                        //Toast.makeText(getApplicationContext(), "year: " + selectedyear + " month: " + selectedmonth + " day: " + selectedday, Toast.LENGTH_LONG).show();
                        myCalendar.set(Calendar.YEAR, selectedyear);
                        myCalendar.set(Calendar.MONTH, selectedmonth);
                        myCalendar.set(Calendar.DAY_OF_MONTH, selectedday);
                        updateLabel();
                    }
                },mYear, mMonth, mDay);
                //mDatePicker.setTitle("Select date");
                mDatePicker.show();  }
        });


        final EditText edittime = (EditText) findViewById(R.id.time_field);
        edittime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //To show current date in the datepicker
                Calendar mcurrentTime=Calendar.getInstance();
                int hour=mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute=mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker=new TimePickerDialog(AddEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timepicker, int selectedhour, int selectedminute) {
                        // TODO Auto-generated method stub
                        //Toast.makeText(getApplicationContext(), "year: " + selectedyear + " month: " + selectedmonth + " day: " + selectedday, Toast.LENGTH_LONG).show();
                        myCalendar.set(Calendar.HOUR_OF_DAY, selectedhour);
                        myCalendar.set(Calendar.MINUTE, selectedminute);
                        //myCalendar.set(Calendar.MILLISECOND, );
                        //updateLabel();
                        String myFormat = "HH:mm"; //In which you need put here
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                        //EditText edittext = (EditText) findViewById(R.id.event_date_field);
                        edittime.setText(sdf.format(myCalendar.getTime()));
                    }
                },hour, minute, false);
                //mDatePicker.setTitle("Select date");
                mTimePicker.show();  }
        });




    }

    private void updateLabel(){
        String myFormat = "MM-dd-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        EditText edittext = (EditText) findViewById(R.id.event_date_field);
        edittext.setText(sdf.format(myCalendar.getTime()));
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
                e = new Event();
                EditText type = (EditText) findViewById(R.id.event_type_field);
                e.setType(type.getText().toString());
                EditText name = (EditText) findViewById(R.id.event_name_field);
                e.setName(name.getText().toString());
                EditText location = (EditText) findViewById(R.id.location_name_field);
                e.setLocation(location.getText().toString());
                EditText description = (EditText) findViewById(R.id.description_field);
                e.setDescription(description.getText().toString());
                e.setPosition(home_marker.getPosition());



                //Toast.makeText(getApplicationContext(), "name: " + e.getName() + "location: " + e.getLocation() + " descrip: " + e.getDesription() + "pos: " + e.getPositiion(), Toast.LENGTH_LONG).show();
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

        Log.d(((Object)this).getClass().getSimpleName(), String.format("Drag from %f:%f",
                position.latitude,
                position.longitude));
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        LatLng position=marker.getPosition();
        marker.hideInfoWindow();

        Log.d(((Object)this).getClass().getSimpleName(),
                String.format("Dragging to %f:%f", position.latitude,
                        position.longitude));

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        LatLng position=marker.getPosition();
        //  Toast.makeText(getApplicationContext(), "Marker Dragged to: " + marker.getPosition(), Toast.LENGTH_LONG).show();
        Log.d(((Object)this).getClass().getSimpleName(), String.format("Dragged to %f:%f",
                position.latitude,
                position.longitude));
        String address = getCompleteAddressString(position.latitude,position.longitude);
        EditText location = (EditText) findViewById(R.id.location_name_field);
        String customLocName = location.getText().toString();
        if(customLocName.trim().length() == 0){
            hasChanged = 0;
        }
        if(hasChanged == 0){
           // Toast.makeText(getApplicationContext(), "ADDRESS STRING EMPTY" , Toast.LENGTH_SHORT).show();
           location.setText(address, TextView.BufferType.EDITABLE);
            hasChanged = 0;
        }
        //Toast.makeText(getApplicationContext(), "Marker Dragged to: " + position + "\nAddress:\n" + address , Toast.LENGTH_LONG).show();

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
                Log.w("Marker at", "" + strReturnedAddress.toString());
            } else {
                Log.w("Marker at", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current location address", "Cannot get Address!");
        }
        return strAdd;
    }






}
