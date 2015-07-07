package com.mightycircuit.www.spotifystreamer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import com.mightycircuit.www.spotifystreamer.MusicService.MusicBinder;


import java.util.List;

//ActionBarActivity deprecated so used AppCompatActivity instead....
public class MainActivity extends AppCompatActivity implements ItemFragment.OnFragmentInteractionListener, MainActivityFragment.DataPassListener {
    List<ElementAdapter> placeholder;
    String id;

    private MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound=false;

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

    @Override
    public void onFragmentInteraction(int id){

        //TODO: pass the item id to the new frag
//        ItemFragment Obj=(ItemFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentTracks);
//        Obj.setTrackList(placeholder);
    };


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
    public void passData(String selectedArtist) {
        ItemFragment itemFragment = new ItemFragment();

        //http://stackoverflow.com/questions/16036572/how-to-pass-values-between-fragments

        Bundle args = new Bundle();
        args.putString(itemFragment.DATA_RECEIVE, selectedArtist);
        itemFragment .setArguments(args);

        getFragmentManager().beginTransaction()
                .add(R.id.fragmentMain, itemFragment)
                .addToBackStack(null)
                .commit();
    }
    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicBinder binder = (MusicBinder)service;
            //get service
            musicSrv = binder.getService();
            //pass list
            //musicSrv.setList(songList);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };
    @Override
    protected void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

}
