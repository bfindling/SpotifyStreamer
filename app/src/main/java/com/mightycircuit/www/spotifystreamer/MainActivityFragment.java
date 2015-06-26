package com.mightycircuit.www.spotifystreamer;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistsPager;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements TextView.OnEditorActionListener {
    public static final String LOG_TAG = "SpotifyStreamer";
    public ArrayAdapter<String> mArtistAdapter;
    private EditText editText;
    public FetchArtistTask fetchArtistTask;
    public SpotifyApi api;
    SpotifyService spotify;
    public ArtistsPager results;
    public List<String> mTemporary;

    public MainActivityFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        fetchArtistTask = new FetchArtistTask();
        // fetchWeatherTask.execute("91364");

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Create some dummy data for the ListView.  Here's a sample weekly forecast
        String[] data = {
                "Mon 0/0",
                "Tue 0/0",
                "Wed 0/0",
                "Thurs 0/0",
                "Fri 0/0",
                "Sat 0/0",
                "Sun 0/0"
        };
        mTemporary = new ArrayList<String>(Arrays.asList(data));


        // Now that we have some dummy forecast data, create an ArrayAdapter.
        // The ArrayAdapter will take data from a source (like our dummy forecast) and
        // use it to populate the ListView it's attached to.
        mArtistAdapter =
                new ArrayAdapter<String>(
                        getActivity(), // The current context (this activity)
                        R.layout.list_item_artist, // The name of the layout ID.
                        R.id.list_item_artist_textview, // The ID of the textview to populate.
                        mTemporary);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        Log.d(LOG_TAG, "setAdapter");

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listView_Artists);
        listView.setAdapter(mArtistAdapter);

        // Get a ref to the editText and set a listener to it
        editText = (EditText) rootView.findViewById(R.id.editText);
        editText.setOnEditorActionListener(this);


        //setup spotify wrapper api
        api = new SpotifyApi();


        //test
//        String mArtistName = "beyonce";
//        SpotifyService spotify = api.getService();
//        results = spotify.searchArtists(mArtistName);
//        Log.d(LOG_TAG, results.toString());SpotifyService spotify





        //return inflater.inflate(R.layout.fragment_main, container, false);
        return rootView;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        fetchArtistTask.execute(editText.getText().toString());

        return false;
    }


    public class FetchArtistTask extends AsyncTask<String,Void,ArtistsPager> {




        public void FetchArtistTask(){

        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            spotify = api.getService();

        }



        @Override
        protected ArtistsPager doInBackground(String... params) {



            //remove hardcoded and pass to asycnch
            String mArtistName = "Beyonce";

                    //String mArtistName = editText.getText().toString();

            ArtistsPager results = spotify.searchArtists(mArtistName);
            Log.d(LOG_TAG, "doing in background...");




                //Get the data from the server and return it
                return results;


        }

        @Override
        protected void onPostExecute(ArtistsPager artistsResults) {
            super.onPostExecute(artistsResults);

            mArtistAdapter.clear();

            //convert to string
            //mArtistAdapter.addAll(artistsResults);

            Log.d(LOG_TAG, "artist:" + artistsResults.artists);

            //mForecastAdapter.notifyDataSetChanged();

        }



    }

}
