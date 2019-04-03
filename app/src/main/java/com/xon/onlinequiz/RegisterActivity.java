package com.xon.onlinequiz;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.muddzdev.styleabletoast.StyleableToast;
import com.xon.onlinequiz.R;
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
    TextView tvlogin;
    Student student;
    Lecturer lecturer;
    ImageView imgprofile;
    SwipeButton mode;
    TextView matric;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mode = findViewById(R.id.swipe_btn);
        matric = findViewById(R.id.textView3);
        initView();

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Input validation before proceed registration
                if(validateInput() == true) {
                    registerUserInput();
                }
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


        String maclect, email, pass, phone, name, accommodation;
        maclect = edmatric.getText().toString();
        email = edEmail.getText().toString();
        pass = edPass.getText().toString();
        phone = edPhone.getText().toString();
        name = edName.getText().toString();
        accommodation = sploc.getSelectedItem().toString();
        student = new Student(maclect, email, pass, phone, name, accommodation);
        lecturer = new Lecturer(maclect,email,pass,phone,name,accommodation);
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
                //when mode is active (lecturer mode), the r.handler will request lecturer_registration.php
                // and vice versa when the mode in inactive
                if(mode.isActive()) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("lecturerID", lecturer.lecturerID);
                    hashMap.put("email", lecturer.email);
                    hashMap.put("password", lecturer.password);
                    hashMap.put("phone", lecturer.phone);
                    hashMap.put("name", lecturer.name);
                    hashMap.put("accommodation", lecturer.accommodation);
                    RequestHandler rh = new RequestHandler();
                    String s = rh.sendPostRequest
                            ("http://fussionspark.com/onlinequiz/lecturer_registration.php", hashMap);
                    //this php is used to access lecturer data
                    return s;
                }else{
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("matricNo", student.matricNo);
                    hashMap.put("email", student.email);
                    hashMap.put("password", student.password);
                    hashMap.put("phone", student.phone);
                    hashMap.put("name", student.name);
                    hashMap.put("accommodation", student.accommodation);
                    RequestHandler rh = new RequestHandler();
                    String s = rh.sendPostRequest
                            ("http://fussionspark.com/onlinequiz/insert_registration.php", hashMap);
                    //this php used to access student data
                    return s;
                }
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
                }else if (s.equalsIgnoreCase("MatricExist")){
                    StyleableToast.makeText(RegisterActivity.this, "This Matric ID already been registered, Please Login", Toast.LENGTH_SHORT,R.style.mytoast).show();
                }else if (s.equalsIgnoreCase("lectIdexist")){
                    StyleableToast.makeText(RegisterActivity.this, "This Lecturer ID already been Registered, Please Login", Toast.LENGTH_SHORT,R.style.mytoast).show();
                }
                else if (s.equalsIgnoreCase("nodata")) {
                    StyleableToast.makeText(RegisterActivity.this, "Please fill in data first", Toast.LENGTH_SHORT,R.style.mytoast).show();
                } else {
                    StyleableToast.makeText(RegisterActivity.this, "Registration Failed", Toast.LENGTH_SHORT,R.style.mytoast).show();
                }


            }
        }

        RegisterUser registerUser = new RegisterUser();
        registerUser.execute();
    }

    private void registerUserDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(this.getResources().getString(R.string.registerfor) + " " + student.name + "?");

        alertDialogBuilder
                .setMessage(this.getResources().getString(R.string.registerdialognew))
                .setCancelable(false)
                .setPositiveButton(this.getResources().getString(R.string.yesbutton), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Toast.makeText(getActivity(), "DELETE "+jobid, Toast.LENGTH_SHORT).show();
                        new Encode_image().execute(getDir(), student.phone + ".jpg");
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

}
