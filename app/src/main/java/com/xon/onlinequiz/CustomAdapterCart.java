package com.xon.onlinequiz;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomAdapterCart extends SimpleAdapter {

    private Context mContext;
    public LayoutInflater inflater=null;
    public CustomAdapterCart(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        mContext = context;
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        try{
            if(convertView==null)
                vi = inflater.inflate(R.layout.activity_cart_list, null);
            HashMap<String, Object> data = (HashMap<String, Object>) getItem(position);
            TextView tvname = vi.findViewById(R.id.textViewc);
            TextView tvcode = vi.findViewById(R.id.textView2c);
            TextView tvorderid = vi.findViewById(R.id.textView3c);
            TextView tvprice= vi.findViewById(R.id.textView4c);
            String dfcode = (String) data.get("code");
            String dfname =(String) data.get("quizname");
            String dfprice =(String) data.get("quizprice");
            String dforder =(String) data.get("orderid");
            String dfst=(String) data.get("status");
            tvname.setText(dfname);
            tvcode.setText(dfcode);
            tvprice.setText(dfprice);
            tvorderid.setText(dforder);


        }catch (IndexOutOfBoundsException e){

        }

        return vi;
    }
}