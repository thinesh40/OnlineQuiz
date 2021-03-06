package com.xon.onlinequiz;


import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class ProfileActivity extends AppCompatActivity{
    String userid,email, name, phone, location, latitude, longitude;
    Spinner sploc;
    TextView tvphone,tvlocation;
    EditText tvuserid, tvname, edoldpass, ednewpass,tvemail;
    ImageView imgprofile;
    Button btnUpdate;
    ImageButton btnloc;
    Dialog myDialogMap;
    private GoogleMap mMap;
    String slatitude, slongitude;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        imgprofile = findViewById(R.id.imageview_old);
        tvuserid = findViewById(R.id.matricNo_nw);
        tvname = findViewById(R.id.txtname_nw);
        tvphone = findViewById(R.id.phoneNo_nw);
        tvemail= findViewById(R.id.txtEmail_nw);
        edoldpass = findViewById(R.id.txtpassword_nw);
        ednewpass = findViewById(R.id.txtpassword_nw);
        sploc = findViewById(R.id.spinner_nw);
        btnUpdate = findViewById(R.id.btn_update);
       btnloc = findViewById(R.id.btnMap);
        //tvlocation  = findViewById(R.id);
        userid = bundle.getString("userid");//email
        name = bundle.getString("username");  //full name
        phone = bundle.getString("phone"); //phone */
        Toast.makeText(this, userid, Toast.LENGTH_SHORT).show();
        tvphone.setText(phone);
        String image_url = "http://fussionspark.com/onlinequiz/profileimages/" + phone + ".jpg";
        Picasso.with(this).load(image_url)
                .resize(400, 400).into(imgprofile);
       loadUserProfile();
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newemail = tvuserid.getText().toString();
                String newname = tvname.getText().toString();
                String oldpass = edoldpass.getText().toString();
                String newpass = ednewpass.getText().toString();
                String newloc = sploc.getSelectedItem().toString();
               dialogUpdate(newemail, newname, newloc, oldpass, newpass);

            }
        });
       btnloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMapWindow();
            }
        });
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }
/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("userid", userid);
                bundle.putString("name", name);
                bundle.putString("phone", phone);
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        } */


      void loadUserProfile(){
            class LoadUserProfile extends AsyncTask<Void, Void, String> {

                @Override
                protected String doInBackground(Void... voids) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("userId", userid);
                    RequestHandler rh = new RequestHandler();
                    String s = rh.sendPostRequest("http://fussionspark.com/onlinequiz/load_user.php", hashMap);
                    return s;
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        JSONArray restArray = jsonObject.getJSONArray("user");
                        JSONObject c = restArray.getJSONObject(0);
                        name = c.getString("name");
                        email = c.getString("email");
                        location = c.getString("location");
                        latitude = c.getString("latitude");
                        longitude = c.getString("longitude");
                    } catch (JSONException e) {

                    }
                    //Log.e("LOC",location);
                    for (int i = 0; i < sploc.getCount(); i++) {
                        if (sploc.getItemAtPosition(i).toString().equalsIgnoreCase(location)) {
                            sploc.setSelection(i);
                        }
                    }
                    tvuserid.setText(userid);
                    tvname.setText(name);
                    tvemail.setText(email);


                    //tvlocation.setText("https://www.google.com/maps/@" + latitude + "," + longitude + ",15z");
                    Toast.makeText(ProfileActivity.this, longitude, Toast.LENGTH_SHORT).show();
                }
            }
            LoadUserProfile loadUserProfile = new LoadUserProfile();
            loadUserProfile.execute();

        }
        void updateProfile ( final String newemail, final String newname, final String newloc,
       final String oldpass, final String newpass){
           class UpdateProfile extends AsyncTask<Void, Void, String> {

                @Override
                protected String doInBackground(Void... voids) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("userId", userid);
                    hashMap.put("email", newemail);
                    hashMap.put("name", newname);
                    hashMap.put("phone", phone);
                    hashMap.put("opassword", oldpass);
                    hashMap.put("npassword", newpass);
                    hashMap.put("newloc", newloc);
                    hashMap.put("latitude", latitude);
                    hashMap.put("longitude", longitude);
                    RequestHandler rh = new RequestHandler();
                    String s = rh.sendPostRequest("http://fussionspark.com/onlinequiz/update_profile.php", hashMap);
                    return s;
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    if (s.equalsIgnoreCase("success")) {
                        Toast.makeText(ProfileActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("userid", userid);
                        bundle.putString("name", name);
                        bundle.putString("phone", phone);
                        intent.putExtras(bundle);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                    } else if(s.equalsIgnoreCase("failed")) {
                        Toast.makeText(ProfileActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
           UpdateProfile updateProfile = new UpdateProfile();
            updateProfile.execute();
        }

    private void loadMapWindow() {
        myDialogMap = new Dialog(this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth);//Theme_DeviceDefault_Dialog_NoActionBar
        myDialogMap.setContentView(R.layout.map_window);
        myDialogMap.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        Button btnsavemap = myDialogMap.findViewById(R.id.btnclosemap);
        MapView mMapView = myDialogMap.findViewById(R.id.mapView);
        MapsInitializer.initialize(this);
        mMapView.onCreate(myDialogMap.onSaveInstanceState());
        mMapView.onResume();
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                LatLng allpos;
                LatLng posisiabsen = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)); ////your lat lng
                googleMap.addMarker(new MarkerOptions().position(posisiabsen).title("HOME").icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))).showInfoWindow();
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(posisiabsen));
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(13.0f));
                if (ActivityCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                googleMap.setMyLocationEnabled(true);
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        googleMap.clear();
                        slatitude = String.valueOf(latLng.latitude);
                        slongitude = String.valueOf(latLng.longitude);
                        googleMap.addMarker(new MarkerOptions().position(latLng).title("New Home").icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_BLUE))).showInfoWindow();
                    }
                });
            }
        });
        btnsavemap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slatitude.length()>5){
                    latitude = slatitude;
                    longitude = slongitude;
                    myDialogMap.dismiss();
                   // tvlocation.setText("https://www.google.com/maps/@"+latitude+","+longitude+",15z");
                    //locationManager.removeUpdates(mMapView.this);
                    Toast.makeText(ProfileActivity.this, longitude, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ProfileActivity.this, "Please select home location", Toast.LENGTH_SHORT).show();
                }

            }
        });
        myDialogMap.show();
        enableLocation();
    }


    private void enableLocation() {
        if (!checkLocationPermission()) {
            Toast.makeText(this, "PLEASE ALLOW PERMISSION FOR APP TO ACCESS GPS", Toast.LENGTH_SHORT).show();
        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (checkPermission()) {
            //linkloc.setText(getResources().getString(R.string.waithomeloc));
          // locationManager.requestLocationUpdates(this);
        } else {
            //requestPermission();
        }
        if (!checkLocationPermission()) {
            //Toast.makeText(this, "PLEASE ALLOW PERMISSION FOR APP TO ACCESS GPS", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
    }

    public boolean checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    ActivityCompat.requestPermissions(ProfileActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            99);

                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            99);
                }
                return false;
            } else {
                return true;
            }
        }
        return true;
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (result == PackageManager.PERMISSION_GRANTED) {
                //LocationAvailable = true;
                return true;
            } else {
                //LocationAvailable = false;
                return false;
            }
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 999:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkPermission()) {
                        if (locationManager!=null){
                           // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1, this);
                        }

                    } else {
                        //requestPermission();
                    }
                } else {
                }
                break;
        }
    }

    public void onLocationChanged(Location location) {
        Toast.makeText(this, "Location Changed", Toast.LENGTH_SHORT).show();
        latitude = String.valueOf(location.getLatitude());
        longitude = String.valueOf(location.getLongitude());
        //locationManager.removeUpdates((LocationListener) this);
    }




    private void dialogUpdate(final String newemail, final String newname, final String newloc, final String oldpass, final String newpass) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Profile");

        alertDialogBuilder
                .setMessage("Update this profile")
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        updateProfile(newemail, newname, newloc, oldpass, newpass);
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}