package com.xon.onlinequiz;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class StoreActivity extends AppCompatActivity {
        Dialog myDialogWindow,myDialogCart;
        ListView lvStore;
        ArrayList<HashMap<String, String>> storeList;
        ArrayList<HashMap<String, String>> cartlist;
        Spinner sploc;
        double total;
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
            cartlist = new ArrayList<>();
            loadStore();



            lvStore.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    showQuizDetail(position);
                }
            });
        }

    private void showQuizDetail(int p) {

        myDialogWindow = new Dialog(this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth);//Theme_DeviceDefault_Dialog_NoActionBar
        myDialogWindow.setContentView(R.layout.dialog1);
        myDialogWindow.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView tvfname,tvfprice,tvfcode;
        Button btnorder = myDialogWindow.findViewById(R.id.button2);
        tvfname= myDialogWindow.findViewById(R.id.textView12a);
        tvfprice = myDialogWindow.findViewById(R.id.textView13a);
        tvfcode = myDialogWindow.findViewById(R.id.textView14a);
        tvfname.setText(storeList.get(p).get("feature"));
        tvfprice.setText(storeList.get(p).get("price"));
        tvfcode.setText(storeList.get(p).get("code"));
        final String quizname = storeList.get(p).get("feature");
        final String quizprice = storeList.get(p).get("price");
        final String quizid =(storeList.get(p).get("code"));
        Toast.makeText(this, quizid, Toast.LENGTH_SHORT).show();


        btnorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String fquan = spquan.getSelectedItem().toString();
                dialogOrder(quizid,quizname,quizprice);
            }
        });
        myDialogWindow.show();
        }

    private void dialogOrder(final String quizid, final String quizname, final String quizprice) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Order "+quizname);

        alertDialogBuilder
                .setMessage("Are you sure")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        insertCart(quizid,quizname,quizprice);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    private void insertCart(final String quizid, final String quizname, final String quizprice) {
        class InsertCart extends AsyncTask<Void,Void,String>{

            @Override
            protected String doInBackground(Void... voids) {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("quizid",quizid);
                hashMap.put("userid",userid);
                hashMap.put("quizprice",quizprice);
                hashMap.put("quizname",quizname);

                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest("http://fussionspark.com/onlinequiz/insert_cart.php",hashMap);
                return s;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s.equalsIgnoreCase("success")){
                    Toast.makeText(StoreActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    myDialogWindow.dismiss();
                   // loadQuiz(restid);
                }else if(s.equalsIgnoreCase("Failed")){
                    Toast.makeText(StoreActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }

        }
        InsertCart insertCart = new InsertCart();
        insertCart.execute();
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
                case R.id.mycart:
                loadCartData();
                return true;
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
                            String rfeature = c.getString("feature");
                            String rdescription = c.getString("description");
                            String rcode = c.getString("code");
                            Toast.makeText(StoreActivity.this, rcode, Toast.LENGTH_SHORT).show();
                            String rprice = c.getString("price");
                            HashMap<String,String> storelisthash = new HashMap<>();
                            storelisthash.put("feature",rfeature);
                            storelisthash.put("description",rdescription);
                            storelisthash.put("code",rcode);
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


    public String convertime24h(String value) {
        String _12hourformat = "";
        try {
            //Log.e("DATE", value);
            SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd hh:mm");
            Date date = dt.parse(value.substring(0, 16));
            SimpleDateFormat dt1 = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
            return _12hourformat = dt1.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return _12hourformat;
    }
    private void loadCartWindow() {
        myDialogCart = new Dialog(this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth);//Theme_DeviceDefault_Dialog_NoActionBar
        myDialogCart.setContentView(R.layout.cart_window);
        myDialogCart.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        ListView lvcart = myDialogCart.findViewById(R.id.lvmycart);
        TextView tvtotal = myDialogCart.findViewById(R.id.textViewTotal);
        TextView tvorderid = myDialogCart.findViewById(R.id.textOrderId);
        Button btnpay = myDialogCart.findViewById(R.id.btnPay);
        //Log.e("THINESH","SIZE:"+cartlist.size());
        lvcart.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                dialogDeleteFood(position);
                return false;
            }
        });
        btnpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPay();
            }
        });
        ListAdapter adapter = new CustomAdapterCart(
                StoreActivity.this, cartlist,
                R.layout.activity_cart_list, new String[]
                {"quizname","code","orderid","quizprice"}, new int[]
                {R.id.textViewc,R.id.textView2c,R.id.textView3c,R.id.textView4c});
        lvcart.setAdapter(adapter);
        tvtotal.setText("RM "+total);
        tvorderid.setText("Scret");
        myDialogCart.show();

    }

    private void dialogDeleteFood(final int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Delete Food "+cartlist.get(position).get("foodname")+"?");
        alertDialogBuilder
                .setMessage("Are you sure")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        deleteCart(position);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void deleteCart(final int position) {
        class DeleteCartFood extends AsyncTask<Void,Void,String>{

            @Override
            protected String doInBackground(Void... voids) {
                String code = cartlist.get(position).get("code");
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("userid",userid);
                hashMap.put("code",code);
                RequestHandler requestHandler = new RequestHandler();
                String s = requestHandler.sendPostRequest("http://fussionspark.com/onlinequiz/delete_cart.php",hashMap);
                return s;
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s.equalsIgnoreCase("success")){
                    myDialogCart.dismiss();
                    loadCartData();
                    Toast.makeText(StoreActivity.this, "Success", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(StoreActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        }
        DeleteCartFood deleteCartFood = new DeleteCartFood();
        deleteCartFood.execute();
    }

    private void dialogPay() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Proceed with payment?");

        alertDialogBuilder
                .setMessage("Are you sure")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(StoreActivity.this,PaymentActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("userid",userid);
                        bundle.putString("name",name);
                        bundle.putString("phone",phone);
                        bundle.putString("total", String.valueOf(total));
                        bundle.putString("orderid", cartlist.get(0).get("orderid"));
                        intent.putExtras(bundle);
                        myDialogCart.dismiss();
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    private void loadCartData() {
        class LoadCartData extends AsyncTask<Void,Void,String>{

            @Override
            protected String doInBackground(Void... voids) {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("userid",userid);
                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest("http://fussionspark.com/onlinequiz/load_cart.php",hashMap);
                return s;
            }

            @Override
            protected void onPostExecute(String s) {
              cartlist.clear();
                total = 0;
                try{
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray cartarray = jsonObject.getJSONArray("cart");
                    if(s.equalsIgnoreCase("success")){
                        Toast.makeText(StoreActivity.this, "Mudd", Toast.LENGTH_SHORT).show();
                    }
                    for (int i=0;i<cartarray .length();i++) {
                        JSONObject c = cartarray .getJSONObject(i);
                        String jfid = c.getString("code");
                        String jfn = c.getString("userid");
                        String jfp = c.getString("quizname");
                        String jfq = c.getString("quizprice");
                        String jst = c.getString("status");
                        String joid = c.getString("orderid");
                        HashMap<String,String> cartlisthash = new HashMap<>();
                        cartlisthash .put("code",jfid);
                        cartlisthash .put("userid",jfn);
                        cartlisthash .put("quizname",jfp);
                        cartlisthash .put("quizprice",jfq);
                        cartlisthash .put("status",jst);
                        cartlisthash .put("orderid",joid);
                        cartlist.add(cartlisthash);
                        Toast.makeText(StoreActivity.this, jfp, Toast.LENGTH_SHORT).show();
                        total = 34;

                    }
                }catch (JSONException e){}
                super.onPostExecute(s);
                if (total>0){
                    loadCartWindow();
                }else{

                 //   Toast.makeText(StoreActivity.this, "Cart is feeling empty", Toast.LENGTH_SHORT).show();
                }

            }
        }
        LoadCartData loadCartData = new LoadCartData();
        loadCartData.execute();
    }
}

