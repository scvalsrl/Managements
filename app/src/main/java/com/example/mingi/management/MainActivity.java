package com.example.mingi.management;

import android.content.AsyncQueryHandler;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout = null;
    private ActionBarDrawerToggle mToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_drawer);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("BNK 운행일지");


        BottomNavigationView bottomnav = findViewById(R.id.bottom_navigation);
        bottomnav.setOnNavigationItemSelectedListener(navListener);


        ////////////////////////////////


        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

       NavigationView navigationView = (NavigationView)findViewById(R.id.navigation_view);
        Log.d("1", "onNavigationItemSelected: 요왔");
       navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
           public boolean onNavigationItemSelected(MenuItem item){
                 item.setChecked(true);
               Log.d("1", "onNavigationItemSelected: 요왔으");
                mDrawerLayout.closeDrawers();

                int id = item.getItemId();
                Intent drawer_intent;


                switch (id){
                    case R.id.navigation_item_carjoin:
                        Log.d("1", "onNavigationItemSelected: 여기요2");
                        drawer_intent = new Intent(getApplicationContext(), CarJoinActivity.class);
                        startActivity(drawer_intent);
                        overridePendingTransition(0, 0);
                        Log.d("1", "onNavigationItemSelected: 여기요3");
                        finish();

                        break;
                    case R.id.navigation_item_carlist:

                        break;
                    case R.id.navigation_item_peoplelist:


                        Intent intent = getIntent();
                        String userID =intent.getExtras().getString("userID");
                        String userPassword =intent.getExtras().getString("userID");

                        drawer_intent = new Intent(getApplicationContext(), CarlistActivity.class);


                        drawer_intent.putExtra("userID", userID);
                        drawer_intent.putExtra("userPassword", userPassword);

                        startActivity(drawer_intent);
                        overridePendingTransition(0, 0);

                        finish();

                        break;
                    case R.id.navigation_item_logout:

                        break;
                }

                return true;
            }
        });





    }


    class BackgroundTask extends AsyncTask<Void, Void, String> {

        String target;



        @Override
        protected void onPreExecute(){
            target = "http://scvalsrl.cafe24.com/List.php";
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

            Intent intent = new Intent(MainActivity.this, CarManegementActivity.class);
            intent.putExtra("userList",result);
            MainActivity.this.startActivity(intent);


        }




    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            Intent drawer_intent = new Intent(getApplicationContext(), CarJoinActivity.class);
                            startActivity(drawer_intent);
                            overridePendingTransition(0, 0);
                            finish();
                            break;

                        case R.id.nav_favorites:

                            Intent intent = getIntent();
                            String userID =intent.getExtras().getString("userID");
                            String userPassword =intent.getExtras().getString("userID");

                            Intent drawer_intent2 = new Intent(getApplicationContext(), CarlistActivity.class);

                            drawer_intent2.putExtra("userID", userID);
                            drawer_intent2.putExtra("userPassword", userPassword);


                            startActivity(drawer_intent2);
                            overridePendingTransition(0, 0);

                            finish();

                            break;
                        case R.id.nav_search:

                            break;
                    }


                    return true;
                }
            };


}
