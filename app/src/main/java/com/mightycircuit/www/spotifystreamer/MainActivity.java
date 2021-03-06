package com.mightycircuit.www.spotifystreamer;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import android.content.res.Configuration;
import android.media.Image;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


import com.squareup.picasso.Picasso;

import java.util.List;



//Stage 1 submission 1 FINAL
//This is a back up copy to be kept for reference

//ActionBarActivity deprecated so used AppCompatActivity instead....
    public class MainActivity extends AppCompatActivity implements DataPassListener {
    List<ElementAdapter> placeholder;
    String id;
    public static final String LOG_TAG = "SpotifyStreamer";

    public DataPassListener mCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(LOG_TAG, "MainActivity onCreate !!!!!!!!!!!!!!!!!!!");

        //first fragment
        if (savedInstanceState == null) {
            Log.d(LOG_TAG, "--------MainActivity creating frag cause NULL!");

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment, new MainActivityFragment())
                    .commit();

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void passData(String selectedArtist, Fragment fragment) {

        FragmentManager fm = getFragmentManager();
        Bundle args = new Bundle();
        args.putString(DataPassListener.DATA_RECEIVE, selectedArtist);
        fragment.setArguments(args);

        getFragmentManager().beginTransaction()
                //rename fragment
                .add(R.id.fragment, fragment)
                .addToBackStack("MAIN_FRAG")
                .commit();
        fm.executePendingTransactions();
        Log.d(LOG_TAG, "pass Data1 Frag count=" + fm.getBackStackEntryCount());
    }


    @Override
    public void passDataImage(String image, String selectedArtist, Fragment fragment) {
        FragmentManager fm = getFragmentManager();

        Bundle args = new Bundle();
        args.putString(DataPassListener.DATA_RECEIVE, selectedArtist);
        args.putString(DataPassListener.IMAGE_RECEIVE, image);
        fragment .setArguments(args);

        getFragmentManager().beginTransaction()
                .add(R.id.fragment, fragment)
                .addToBackStack("ITEM_FRAG")
                .commit();
        fm.executePendingTransactions();
        Log.d(LOG_TAG, "pass data2 Frag count=" + fm.getBackStackEntryCount());
    }
    @Override
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();
        Log.d(LOG_TAG, "BackButton Frag count=" + fm.getBackStackEntryCount());


        if (getFragmentManager().getBackStackEntryCount() > 0){

            Log.d(LOG_TAG, "Frag stack POPPED!");
            boolean done = getFragmentManager().popBackStackImmediate();
        } else{
            finish();
            Log.d(LOG_TAG, "back button finished");
        }

    }

}
