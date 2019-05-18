package com.xon.onlinequiz;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class StoreActivity extends AppCompatActivity {

        ListView lvStore;
        ArrayList<HashMap<String, String>> storeList;
        Spinner sploc;
        String userid,name,phone, email;

        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_store);
            lvStore = findViewById(R.id.listviewStore);


            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            userid = bundle.getString("userid");
            name = bundle.getString("name");
            phone = bundle.getString("phone");
            email = bundle.getString("email");

            loadStore();

        }


        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.main_menu,menu);
            return true;
        }
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle item selection
            switch (item.getItemId()) {
                //case R.id.phoneNo:
                //loadCartData();
                //  return true;
                case R.id.myprofile:
                    Intent intent = new Intent(StoreActivity.this,ProfileActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("userid",userid);
                    bundle.putString("email",email);
                    bundle.putString("username",name);
                    bundle.putString("phone",phone);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }
        private void loadStore() {
            class LoadStore extends AsyncTask<Void,Void,String> {

                @Override
                protected String doInBackground(Void... voids) {
                    HashMap<String,String> hashMap = new HashMap<>();
                    RequestHandler rh = new RequestHandler();
                    storeList = new ArrayList<>();
                    String s = rh.sendPostRequest
                            ("https://www.fussionspark.com/onlinequiz/load_store.php",hashMap);
                    return s;
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    Toast.makeText(StoreActivity.this, s, Toast.LENGTH_LONG).show();
                    storeList.clear();
                    try{
                        JSONObject jsonObject = new JSONObject(s);
                        JSONArray restarray = jsonObject.getJSONArray("store");
                        Log.e("THINESH",jsonObject.toString());
                        for (int i=0;i<restarray.length();i++){
                            JSONObject c = restarray.getJSONObject(i);
                            //String no = c.getString("quesno");
                            String rfeature = c.getString("feature");
                            String rdescription = c.getString("description");
                            String rprice = c.getString("price");
                            HashMap<String,String> storelisthash = new HashMap<>();
                            //restlisthash.put("quesno",no);
                            storelisthash.put("feature",rfeature);
                            storelisthash.put("description",rdescription);
                            storelisthash.put("price",rprice);
                            storeList.add(storelisthash);

                        }
                    }catch (final JSONException e){
                        Log.e("JSONERROR",e.toString());
                    }

                    ListAdapter adapter = new CustomAdapterStore(
                            StoreActivity.this, storeList,
                            R.layout.cust_list_store, new String[]
                            {"feature","description","price"}, new int[]
                            {R.id.textViewF,R.id.textViewD,R.id.textViewP});
                    lvStore.setAdapter(adapter);
                }

            }
            LoadStore loadStore = new LoadStore();
            loadStore.execute();
    }
}

