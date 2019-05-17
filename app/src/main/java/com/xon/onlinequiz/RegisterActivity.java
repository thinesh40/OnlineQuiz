package com.xon.onlinequiz;
import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.ebanx.swipebtn.OnActiveListener;
import com.ebanx.swipebtn.OnStateChangeListener;
import com.ebanx.swipebtn.SwipeButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.muddzdev.styleabletoast.StyleableToast;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    Spinner sploc;
    EditText edEmail, edPass, edPhone, edName, edmatric;
    Button btnReg;
    ImageView mapButton;
    TextView tvlogin,backtologin;
    User user;
    ImageView imgprofile;
    SwipeButton mode;
    TextView matric;
    Dialog myDialogMap;
    String hlatitude,hlongitude;
    TextView text;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mode = findViewById(R.id.swipe_btn);
        matric = findViewById(R.id.textView3);
        backtologin = findViewById(R.id.tvregister);
        mapButton = findViewById(R.id.btnMap);
        initView();


        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Input validation before proceed registration
                registerUserInput();
                /*if(validateInput() == true) {

                }*/
            }


        });
        imgprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogTakePicture();
            }
        });


        //This Function just to Notify user about their current mode
      mode.setOnActiveListener(new OnActiveListener() {
          @Override
          public void onActive() {
              StyleableToast.makeText(RegisterActivity.this, "Register as Lecturer", Toast.LENGTH_SHORT,R.style.mytoast).show();
                  btnReg.setText("Register as Lecturer");
                  matric.setText("Lecturer ID");

          }
      });
      //Reverse back to student mode
        mode.setOnStateChangeListener(new OnStateChangeListener() {
            @Override
            public void onStateChange(boolean inactive) {
                StyleableToast.makeText(RegisterActivity.this, "Register as Student", Toast.LENGTH_SHORT,R.style.mytoast).show();
                btnReg.setText("Register as Student");
                matric.setText("Matric No");
            }
        });

        backtologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMapWindow();
            }
        });


    }

    private Boolean validateInput() {
        String emailPattern = "[a-zA-Z0-9._-]+@[gmail]+\\.+[a-z]+";
        String emailval = edEmail.getText().toString();
        if(!(edmatric.length() == 6 )) {

           edmatric.setError("Matric No or Lecturer ID must be six Digit. Eg.254532");
           return false;
       }else if(edPass.length() < 8){
            edPass.setError("Password too short !");
           return false;
        }else if(!(emailval.matches(emailPattern))){
            edEmail.setError("Invalid Email,Required Email Eg. user@gmail.com ");
            return false;
       }else {
         return true;
       }
    }
    public void initView() {
        sploc = findViewById(R.id.spinner);
        edEmail = findViewById(R.id.txtEmail);
        edPass = findViewById(R.id.txtpassword);
        edmatric = findViewById(R.id.matricNo);
        edPhone = findViewById(R.id.phoneNo);
        edName = findViewById(R.id.txtname);
        btnReg = findViewById(R.id.btn_register);
        tvlogin = findViewById(R.id.tvregister);
        imgprofile = findViewById(R.id.imageview);

    }

    private void dialogTakePicture() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(this.getResources().getString(R.string.dialogtakepicture));

        alertDialogBuilder
                .setMessage(this.getResources().getString(R.string.dialogtakepicturea))
                .setCancelable(false)
                .setPositiveButton(this.getResources().getString(R.string.yesbutton), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(takePictureIntent, 1);
                        }
                    }
                })
                .setNegativeButton(this.getResources().getString(R.string.nobutton), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageBitmap = ThumbnailUtils.extractThumbnail(imageBitmap, 400, 500);
            imgprofile.setImageBitmap(imageBitmap);
            imgprofile.buildDrawingCache();
            ContextWrapper cw = new ContextWrapper(this);
            File pictureFileDir = cw.getDir("basic", Context.MODE_PRIVATE);
            if (!pictureFileDir.exists()) {
                pictureFileDir.mkdir();
            }
            Log.e("FILE NAME", "" + pictureFileDir.toString());
            if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {
                return;
            }
            FileOutputStream outStream = null;
            String photoFile = "profile.jpg";
            File outFile = new File(pictureFileDir, photoFile);
            try {
                outStream = new FileOutputStream(outFile);
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                outStream.flush();
                outStream.close();
                //hasimage = true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void registerUserInput() {


        String id, email, pass, phone, name, accommodation,role;
        id = edmatric.getText().toString();
        email = edEmail.getText().toString();
        pass = edPass.getText().toString();
        phone = edPhone.getText().toString();
        name = edName.getText().toString();
        accommodation = sploc.getSelectedItem().toString();
        if(mode.isActive()){
            role = "1";
            user = new User(id,role, email, pass, phone, name, accommodation,hlatitude,hlongitude);
        }else {
            role = "2";
            user = new User(id,role, email, pass, phone, name, accommodation,hlatitude,hlongitude);
        }

        registerUserDialog();

    }

    private void insertData() {
        class RegisterUser extends AsyncTask<Void, Void, String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(RegisterActivity.this,
                        "Registration", "...", false, false);
            }

            @Override
            protected String doInBackground(Void... voids) {

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("id",user.id);
                    hashMap.put("role", user.role);
                    hashMap.put("email", user.email);
                    hashMap.put("password", user.password);
                    hashMap.put("phone", user.phone);
                    hashMap.put("name", user.name);
                    hashMap.put("accommodation", user.accommodation);
                    hashMap.put("latitude",user.latitude);
                    hashMap.put("longitude",user.longitude);
                    RequestHandler rh = new RequestHandler();
                    String s = rh.sendPostRequest
                            ("http://fussionspark.com/onlinequiz/insert_registration.php", hashMap);
                    return s;

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                if (s.equalsIgnoreCase("success")) {
                    StyleableToast.makeText(RegisterActivity.this, "Registration Success", Toast.LENGTH_SHORT,R.style.mytoast).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    RegisterActivity.this.finish();
                    startActivity(intent);
                }else if (s.equalsIgnoreCase("EmailExist")){
                    //To Avoid Email Duplications
                    StyleableToast.makeText(RegisterActivity.this, "Email Already Exist, Please try different Email", Toast.LENGTH_SHORT,R.style.mytoast).show();
                }else if (s.equalsIgnoreCase("IdExist")){
                    StyleableToast.makeText(RegisterActivity.this, "This ID already been registered, Please Login", Toast.LENGTH_SHORT,R.style.mytoast).show();
                }
                else if (s.equalsIgnoreCase("nodata")) {
                    StyleableToast.makeText(RegisterActivity.this, "Please fill in data first", Toast.LENGTH_SHORT,R.style.mytoast).show();
                } else if(s.equalsIgnoreCase("failed")) {
                    StyleableToast.makeText(RegisterActivity.this, "Registration Failed", Toast.LENGTH_SHORT,R.style.mytoast).show();
                }


            }
        }

        RegisterUser registerUser = new RegisterUser();
        registerUser.execute();
    }

    private void registerUserDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(this.getResources().getString(R.string.registerfor) + " " + user.name + "?");

        alertDialogBuilder
                .setMessage(this.getResources().getString(R.string.registerdialognew))
                .setCancelable(false)
                .setPositiveButton(this.getResources().getString(R.string.yesbutton), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Toast.makeText(getActivity(), "DELETE "+jobid, Toast.LENGTH_SHORT).show();
                        new Encode_image().execute(getDir(), user.phone + ".jpg");
                        StyleableToast.makeText(RegisterActivity.this, getResources().getString(R.string.registrationprocess), Toast.LENGTH_SHORT,R.style.mytoast).show();

                    }
                })
                .setNegativeButton(this.getResources().getString(R.string.nobutton), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }



    public String getDir(){
        ContextWrapper cw = new ContextWrapper(this);
        File pictureFileDir = cw.getDir("basic", Context.MODE_PRIVATE);
        if (!pictureFileDir.exists()) {
            pictureFileDir.mkdir();
        }
        Log.d("GETDIR",pictureFileDir.getAbsolutePath());
        return pictureFileDir.getAbsolutePath()+"/profile.jpg";
    }
    public class Encode_image extends AsyncTask<String,String,Void> {
        private String encoded_string, image_name;
        Bitmap bitmap;

        @Override
        protected Void doInBackground(String... args) {
            String filname = args[0];
            image_name = args[1];
            bitmap = BitmapFactory.decodeFile(filname);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
            byte[] array = stream.toByteArray();
            encoded_string = Base64.encodeToString(array, 0);
            return null;
        }

        @Override
        protected void onPostExecute(Void avoid) {
            makeRequest(encoded_string, image_name);
        }

        private void makeRequest(final String encoded_string, final String image_name) {
            class UploadAll extends AsyncTask<Void, Void, String> {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected String doInBackground(Void... params) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("encoded_string", encoded_string);
                    map.put("image_name", image_name);
                    RequestHandler rh = new RequestHandler();//request server connection
                    String s = rh.sendPostRequest("http://fussionspark.com/onlinequiz/upload_image.php", map);
                    return s;
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    if (s.equalsIgnoreCase("Success")) {
                        insertData();
                        // Toast.makeText(RegisterActivity.this, "Success Upload Image", Toast.LENGTH_SHORT).show();
                    }else{
                        StyleableToast.makeText(RegisterActivity.this, "Failed Registration", Toast.LENGTH_SHORT,R.style.mytoast).show();
                    }
                }
            }
            UploadAll uploadall = new UploadAll();
            uploadall.execute();
        }
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
                LatLng posisiabsen = new LatLng(6.413395, 100.426868); ////your lat lng
                googleMap.addMarker(new MarkerOptions().position(posisiabsen).title("HOME").icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))).showInfoWindow();
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(posisiabsen));
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(13.0f));
                if (ActivityCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                googleMap.setMyLocationEnabled(true);
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        googleMap.clear();
                        hlatitude = String.valueOf(latLng.latitude);
                        hlongitude = String.valueOf(latLng.longitude);
                        Toast.makeText(RegisterActivity.this, hlongitude, Toast.LENGTH_SHORT).show();
                        googleMap.addMarker(new MarkerOptions().position(latLng).title("New Home").icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_BLUE))).showInfoWindow();
                    }
                });
            }
        });
        btnsavemap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (6>5){
                    Toast.makeText(RegisterActivity.this, hlongitude, Toast.LENGTH_SHORT).show();
                    myDialogMap.dismiss();
                  // text.setText("https://www.google.com/maps/@"+hlatitude+","+hlongitude+",15z");


                }else{
                    Toast.makeText(RegisterActivity.this, "Please select home location", Toast.LENGTH_SHORT).show();
                }

            }
        });
        myDialogMap.show();
    }

}
