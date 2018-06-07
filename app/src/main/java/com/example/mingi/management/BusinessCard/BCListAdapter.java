package com.example.mingi.management.BusinessCard;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
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
        return userList.size();
    }

    @Override
    public Object getItem(int i) {
        return userList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {


        View v = View.inflate(context, R.layout.bc, null);

        TextView bc_name = (TextView) v.findViewById(R.id.bc_name);
        TextView bc_level = (TextView) v.findViewById(R.id.bc_level);
        TextView bc_com = (TextView) v.findViewById(R.id.bc_com);
        final TextView no = (TextView) v.findViewById(R.id.no2);

        new DownloadImageTask((ImageView) v.findViewById(R.id.img))
                .execute("http://scvalsrl.cafe24.com/uploads/" + userList.get(i).getBC_photo());

        bc_name.setText(userList.get(i).getBC_name());
        bc_level.setText(userList.get(i).getBC_level());
        bc_com.setText(userList.get(i).getBC_com());
        no.setText((String.valueOf(userList.get(i).getNo())));

        v.setTag(userList.get(i).getNo());


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


}
