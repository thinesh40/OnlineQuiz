package com.xon.onlinequiz;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.ebanx.swipebtn.OnActiveListener;
import com.ebanx.swipebtn.OnStateChangeListener;
import com.ebanx.swipebtn.SwipeButton;
import com.muddzdev.styleabletoast.StyleableToast;


import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    Button login;
    TextView regtxt,resetPass;
    EditText edemail,edpassword;
    SwipeButton mode;
    SharedPreferences pref;
    CheckBox cbox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        cbox= findViewById(R.id.checkBox);
        mode = findViewById(R.id.swipe_btn);
        edemail = findViewById(R.id.editTextEmail);
        edpassword = findViewById(R.id.editTextPassword);
        login = findViewById(R.id.buttonLogin);
        regtxt = findViewById(R.id.tvRegister);
        resetPass = findViewById(R.id.forgetpass);


        mode.setOnActiveListener(new OnActiveListener() {
            @Override
            public void onActive() {
                StyleableToast.makeText(LoginActivity.this, "Lecturer Mode Activated", Toast.LENGTH_SHORT,R.style.mytoast).show();
                login.setText("Login as Lecturer");
            }
        });
        mode.setOnStateChangeListener(new OnStateChangeListener() {
            @Override
            public void onStateChange(boolean inactive) {
                StyleableToast.makeText(LoginActivity.this, "Student Mode Activated", Toast.LENGTH_SHORT,R.style.mytoast).show();
                login.setText("Login as Student");
            }
        });

        regtxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);    }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edemail.getText().toString();
                String pass = edpassword.getText().toString();

                loginUser(email,pass);
            }
        });

        cbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cbox.isChecked()){
                    String email = edemail.getText().toString();
                    String pass = edpassword.getText().toString();
                    savePref(email,pass);
                }
            }


        });
        loadPref();

        resetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder resetd = new AlertDialog.Builder(LoginActivity.this);
                View resetv = getLayoutInflater().inflate(R.layout.dialog_window,null);

                resetd.setView(resetv);
                AlertDialog dialog = resetd.create();
                dialog.show();
            }
        });

    }
    private void savePref(String e, String p) {
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("email", e);
        editor.putString("password", p);
        editor.commit();
        StyleableToast.makeText(this, "Preferences has been saved", Toast.LENGTH_LONG,R.style.mytoast).show();


    }

    private void loadPref() {
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        String premail = pref.getString("email", "");
        String prpass = pref.getString("password", "");
        if (premail.length()>0){
            cbox.setChecked(true);
            edemail.setText(premail);
            edpassword.setText(prpass);
        }
    }

    private void loginUser(final String email, final String pass) {
        class LoginUser extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(LoginActivity.this,
                        "Login user", "...", false, false);
            }

            @Override
            protected String doInBackground(Void... voids) {
                if(mode.isActive()) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("email", email);
                    hashMap.put("password", pass);
                    RequestHandler rh = new RequestHandler();
                    String s = rh.sendPostRequest
                            ("http://fussionspark.com/onlinequiz/lecturer_login.php", hashMap);
                    return s;
                }else{
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("email", email);
                    hashMap.put("password", pass);
                    RequestHandler rh = new RequestHandler();
                    String s = rh.sendPostRequest
                            ("http://fussionspark.com/onlinequiz/login.php", hashMap);
                    return s;
                }
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                if (s.equalsIgnoreCase("success")){

                    StyleableToast.makeText(LoginActivity.this, "Login Success",Toast.LENGTH_SHORT,R.style.mytoast).show();
                }

                if (s.equalsIgnoreCase("failed")) {
                    StyleableToast.makeText(LoginActivity.this, "Wrong Email or Password", Toast.LENGTH_SHORT,R.style.mytoast).show();
                } else {
                    //Toast.makeText(LoginActivity.this, s, Toast.LENGTH_LONG).show();
                    if(mode.isActive()){
                    Intent intent = new Intent(LoginActivity.this, LecturerMainActivity.class);
                    startActivity(intent);}
                    else {
                       Intent intent1 = new Intent(LoginActivity.this,StudentMainActivity.class);
                        startActivity(intent1);
                    }



                }
            }
        }
        LoginUser loginUser = new LoginUser();
        loginUser.execute();
    }
}
