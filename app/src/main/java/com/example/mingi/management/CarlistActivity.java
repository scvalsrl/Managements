package com.example.mingi.management;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CarlistActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carlist);



        Button managemetnButton = (Button) findViewById(R.id.managementButton);




        managemetnButton.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View view) {
                new BackgroundTask().execute();
            }

        });


    }


    class BackgroundTask extends AsyncTask<Void, Void, String> {

        String target;



        @Override
        protected void onPreExecute(){
            target = "http://scvalsrl.cafe24.com/CarList.php";
        }

        @Override
        protected String doInBackground(Void... voids) {
            try{
                URL url = new URL(target);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;
                StringBuilder stringBuilder = new StringBuilder();
                while( (temp = bufferedReader.readLine() ) != null ){

                    stringBuilder.append(temp + "\n");

                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();

            }catch (Exception e){

                e.printStackTrace();

            }

            return null;
        }

        @Override
        public void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        public void onPostExecute(String result){

            Intent intent = new Intent(CarlistActivity.this, CarManegementActivity.class);
            intent.putExtra("userList",result);
            CarlistActivity.this.startActivity(intent);


        }

    }
}
