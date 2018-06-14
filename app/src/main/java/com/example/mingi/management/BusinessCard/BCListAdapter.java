package com.example.mingi.management.BusinessCard;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mingi.management.R;

import java.io.InputStream;
import java.util.List;


public class BCListAdapter extends BaseAdapter {

    private Context context;
    private List<BC> userList;
    private Activity parentActivity;
    private String isGPSEnable;
    private String nowLat;
    private String nowLon;
    private String nowName;


    public BCListAdapter(Context context, List<BC> userList, Activity parentActivity, String isGPSEnable, String nowLat, String nowLon, String nowName) {
        this.context = context;
        this.userList = userList;
        this.parentActivity = parentActivity;
        this.isGPSEnable = isGPSEnable;
        this.nowLat = nowLat;
        this.nowLon = nowLon;
        this.nowName = nowName;
    }


    @Override
    public int getCount() {
       // Log.d("김민기1", " getCount() : "+userList.size());
        return userList.size();
    }

    @Override
    public Object getItem(int i) {
      //  Log.d("김민기2", " getItem() : "+userList.get(i));
        return userList.get(i);
    }

    @Override
    public long getItemId(int i) {
       // Log.d("김민기3", " getItemId() : "+i);
        return i;
    }


    @Override
    public View getView(final int i, View v, ViewGroup viewGroup) {

        PersonViewHolder viewHolder;

        if(v==null) {
            v = View.inflate(context, R.layout.bc, null);
            viewHolder = new PersonViewHolder();

            viewHolder. bc_name = (TextView) v.findViewById(R.id.bc_name);
            viewHolder. bc_level = (TextView) v.findViewById(R.id.bc_level);
            viewHolder. bc_com = (TextView) v.findViewById(R.id.bc_com);
            viewHolder. no = (TextView) v.findViewById(R.id.no2);

            v.setTag(viewHolder);
        }
        else
        {
            viewHolder = (PersonViewHolder) v.getTag();
        }


        new DownloadImageTask((ImageView) v.findViewById(R.id.img)).execute("http://scvalsrl.cafe24.com/uploads/" + userList.get(i).getBC_photo());
        viewHolder.bc_name.setText(userList.get(i).getBC_name());
        Log.d("김민기", " bc_name : "+  viewHolder.bc_name.getText());
        viewHolder. bc_level.setText(userList.get(i).getBC_level());
        viewHolder.bc_com.setText(userList.get(i).getBC_com());
        viewHolder.no.setText((String.valueOf(userList.get(i).getNo())));
        return v;


    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }


    public class PersonViewHolder
    {
        public TextView bc_name ;
        public TextView bc_level ;
        public TextView bc_com ;
        public TextView no ;

    }

}
