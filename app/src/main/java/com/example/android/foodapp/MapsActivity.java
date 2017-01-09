package com.example.android.foodapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, ChildEventListener {
    //members
    private GoogleMap mMap;
    private LatLngBounds.Builder mBuilder = new LatLngBounds.Builder();
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Firebase mFirebase;
    public String m_Text="";
    public boolean flag=false;

    //Permissions
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE
    };
    //Constants
    private static final int REQUEST_ACCESS = 1;
    private static final int REQUEST_PLACE_PICKER=1;
    private static final String FIREBASE_URL = "https://foodapp123.firebaseio.com/";
    private static final String FIREBASE_ROOT_NODE = "checkouts";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //preparing GoogleApiClient
        mGoogleApiClient=new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .build();
        mGoogleApiClient.connect();
        //Yes! finally Firebase :P
        Firebase.setAndroidContext(this);
        mFirebase=new Firebase(FIREBASE_URL);
        mFirebase.child(FIREBASE_ROOT_NODE).addChildEventListener(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        //Prepare button and add padding when placed in Map
        final Button button = (Button) findViewById(R.id.share_location);
        button.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mMap.setPadding(0, button.getHeight(), 0, 0);
            }
        });
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this,PERMISSIONS_STORAGE,REQUEST_ACCESS);
        }
        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                LatLng ll = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                addPointToView(ll);
                mLastLocation = null;
            }
        }catch (NullPointerException ne){
            ne.printStackTrace();
        }

    }
    public void shareLocation(View view)
    {
        try {
            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
            Intent intent = intentBuilder.build(this);
            Toast.makeText(this,"Sharing Location...",Toast.LENGTH_SHORT).show();
            startActivityForResult(intent, REQUEST_PLACE_PICKER);
        } catch (GooglePlayServicesRepairableException e) {
            GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(),
                    REQUEST_PLACE_PICKER);
        } catch (GooglePlayServicesNotAvailableException e) {
            Toast.makeText(this, "Please install Google Play Services!", Toast.LENGTH_LONG).show();
        }
    }
    public void clearMap(View view)
    {
        //setContentView(R.id.password);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("password");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
                //clear firebase
                if(m_Text.equals("akash@foodapp"))
                {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    databaseReference.setValue(null);
                    mMap.clear();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PLACE_PICKER) {
            if (resultCode == Activity.RESULT_OK) {
                Place place = PlacePicker.getPlace(this,data);
                Map<String,Object>shareLocation=new HashMap<>();
                shareLocation.put("time", ServerValue.TIMESTAMP);
                //get data from Login Activity
                Intent in=getIntent();
                Bundle b=in.getExtras();
                String name=b.getString("name");
                Long number=b.getLong("contact_no");
                Toast.makeText(this,"name: "+name+" number: "+number.toString(),Toast.LENGTH_SHORT).show();
                //Map<String,Long>userData=new HashMap<>();
                //userData.put(name,number);
                Map userData=new HashMap();
                userData.put(name,number);
                mFirebase.child(FIREBASE_ROOT_NODE).child(place.getId()).setValue(shareLocation);
                Firebase fire=new Firebase(FIREBASE_URL);
                Firebase userRef=fire.child(FIREBASE_ROOT_NODE);
                Firebase people=userRef.child(place.getId());
                people.updateChildren(userData);
                //mFirebase.child(FIREBASE_ROOT_NODE).child(place.getId());
                //mFirebase.setValue(userData);
                Toast.makeText(this,"added:"+name+" "+number,Toast.LENGTH_SHORT).show();
                //Toast.makeText(this,"added location",Toast.LENGTH_SHORT).show();
            } else if (resultCode == PlacePicker.RESULT_ERROR) {
                Toast.makeText(this, "Places API failure! Check that the API is enabled for your key",
                        Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    private void addPointToView(LatLng newPoint)
    {
        mBuilder.include(newPoint);
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(mBuilder.build(),findViewById(R.id.share_location).getHeight()));
        //Toast.makeText(this,"adding marker...",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        final String placeId=dataSnapshot.getKey();
        if(placeId!=null)
        {
            Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient,placeId)
                    .setResultCallback(new ResultCallback<PlaceBuffer>() {
                        @Override
                        public void onResult(@NonNull PlaceBuffer places) {
                            LatLng location=places.get(0).getLatLng();
                            //CharSequence userName=places.get(1).getName();

                            //Toast.makeText(getApplicationContext(),"reached onChildAdded",Toast.LENGTH_SHORT).show();
                            addPointToView(location);
                            mMap.addMarker(new MarkerOptions()
                                    .position(location));
                            //Toast.makeText(getApplicationContext(),"place added",Toast.LENGTH_SHORT).show();
                            places.release();
                        }
                    });
        }

    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {

    }

}
