package com.mightycircuit.www.spotifystreamer;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import android.media.Image;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;


import com.squareup.picasso.Picasso;

import java.util.List;

//ActionBarActivity deprecated so used AppCompatActivity instead....
//public class MainActivity extends AppCompatActivity implements ItemFragment.OnFragmentInteractionListener, DataPassListener {
    public class MainActivity extends AppCompatActivity implements DataPassListener {
    List<ElementAdapter> placeholder;
    String id;

    public DataPassListener mCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         //Do I need this?
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment, new MainActivityFragment())
                    .addToBackStack(null)
                    .commit();
        }

    }
//
//    @Override
//    public void onFragmentInteraction(int id){
//
//        //TODO: pass the item id to the new frag
////        ItemFragment Obj=(ItemFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentTracks);
////        Obj.setTrackList(placeholder);
//
//
//    };


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
//    getSupportFragmentManager().addOnBackStackChangedListener(
//            new FragmentManager.OnBackStackChangedListener() {
//        public void onBackStackChanged() {
//            // Update your UI here.
//        }
//    });


    @Override
    public void passData(String selectedArtist, Fragment fragment) {


        //http://stackoverflow.com/questions/16036572/how-to-pass-values-between-fragments

        Bundle args = new Bundle();
        args.putString(DataPassListener.DATA_RECEIVE, selectedArtist);
        fragment .setArguments(args);

        getFragmentManager().beginTransaction()
                .add(R.id.fragment, fragment)
                .addToBackStack(null)
                .commit();
    }


    @Override
    public void passDataImage(String image, String selectedArtist, Fragment fragment) {

        Bundle args = new Bundle();
        args.putString(DataPassListener.DATA_RECEIVE, selectedArtist);
        args.putString(DataPassListener.IMAGE_RECEIVE, image);
        fragment .setArguments(args);

        getFragmentManager().beginTransaction()
                .add(R.id.fragment, fragment)
                .addToBackStack(null)
                .commit();

    }
}
