package com.mightycircuit.www.spotifystreamer;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TracksPager;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements TextView.OnEditorActionListener, AdapterView.OnItemClickListener {
    public static final String LOG_TAG = "SpotifyStreamer";

    private ArtistCustomAdapter artistCustomAdapter;
    private EditText editText;
    private FetchArtistTask fetchArtistTask;
    private List<ElementAdapter> artists;


    //temp for debugging
    private List<Artist> artistsCheatGrab;


    private SpotifyApi api;
    private SpotifyService spotify;

    private ListView listView;

    //temporary for testing....delete me!!!!!!!!!!!!!!!!!!
    public FetchTracksTask fetchTracksTask;
    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!


    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        artists = new ArrayList<ElementAdapter>();

        ElementAdapter adapterElement = new ElementAdapter("a", "Artist");
        artists.add(adapterElement);
        artistCustomAdapter = new ArtistCustomAdapter(getActivity(), artists);

        //setup spotify wrapper api
        api = new SpotifyApi();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a ref to the editText and set a listener to it
        editText = (EditText) rootView.findViewById(R.id.editText);
        editText.setOnEditorActionListener(this);

        // Get a reference to the ListView, and attach this adapter to it.
        listView = (ListView) rootView.findViewById(R.id.listview_artist);
        listView.setAdapter(artistCustomAdapter);
        listView.setOnItemClickListener(this);


        return rootView;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        Log.d(LOG_TAG, "Search for: " + editText.getText().toString());

        //start the asynch task to query the spotify api
        //fetchArtistTask.execute(editText.getText().toString());

        // fetchArtistTask.execute("Motley Crue");
//        fetchArtistTask.getStatus();
//        fetchArtistTask = new FetchArtistTask();
        // new  fetchArtistTask.execute(editText.getText().toString());


        if (fetchArtistTask == null) {
            // --- create a new task --
            fetchArtistTask = new FetchArtistTask();
            fetchArtistTask.execute(editText.getText().toString());
        } else if (fetchArtistTask.getStatus() == AsyncTask.Status.FINISHED) {
            // --- the task finished, so start another one --
            fetchArtistTask = new FetchArtistTask();
            fetchArtistTask.execute(editText.getText().toString());
        }


        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ItemFragment itemFragment = new ItemFragment();


       //TODO: pass artist name on to frag
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment, itemFragment)
                .addToBackStack(null)
                .commit();




    }


    public class FetchArtistTask extends AsyncTask<String, Void, ArtistsPager> {

        List<ArtistsPager> mArtistPagerTemp = new ArrayList<ArtistsPager>();
//        public void FetchArtistTask(){
//
//        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(LOG_TAG, "onPreExecute");
            spotify = api.getService();

        }


        @Override
        protected ArtistsPager doInBackground(String... params) {
            Log.d(LOG_TAG, "doInBackGround");


            //remove hardcoded and pass to asycnch
            String mArtistName = params[0];

            ArtistsPager results = spotify.searchArtists(mArtistName);


            //Get the data from the server and return it
            return results;


        }

        @Override
        protected void onPostExecute(ArtistsPager artistsResults) {
            super.onPostExecute(artistsResults);
            int IMAGE_SIZE_INDEX = 1;
            String imageUrl;
            String name;
            // Artist element;

            Log.d(LOG_TAG, "onPostExecute");
            //  mArtistAdapter.clear();

            artistCustomAdapter.clear();
            //   07-02 10:18:20.778: E/AndroidRuntime(12528): java.lang.IndexOutOfBoundsException: Invalid index 2, size is 0

            //com.mightycircuit.www.spotifystreamer
            List<Artist> listOfArtists = artistsResults.artists.items;


            //temp for debugging... del me!!!!!!!!!!!!!!!!!!!!
            artistsCheatGrab = listOfArtists;

            //!!!!!!!!!!!!


            Log.d(LOG_TAG, "Artist results size=" + listOfArtists.size());

            for (Artist element : listOfArtists) {
                //for (int i=0; i< listOfArtists.size(); i++) {
                //Log.d(LOG_TAG, "------- size=" + listOfArtists.size());

                //element= listOfArtists.get(i);


                Log.d(LOG_TAG, "images size=" + element.images.size());


                if (element.images == null) {
                    Log.d(LOG_TAG, "null detected.");
                    break;
                }
                if (element.name == null) {
                    name = "ARTIST NOT FOUND";
                } else {
                    name = element.name;
                }

                if (element.images.size() < IMAGE_SIZE_INDEX)

                {
                    //blank place holder image
                    imageUrl = "https://placeimg.com/100/100/people";
                    Log.d(LOG_TAG, "null image detected.");

                } else {
                    imageUrl = element.images.get(IMAGE_SIZE_INDEX).url;
                }


                //mArtistAdapter.add(name);
                ElementAdapter adapterElement = new ElementAdapter(imageUrl, name);
                artists.add(adapterElement);


                Log.d(LOG_TAG, "Artist added to list:" + name);
            }
            //effectively hide the stupid keyboard after listView is populated
            listView.requestFocus();

            Log.d(LOG_TAG, "Adapter updated.");

            //mForecastAdapter.notifyDataSetChanged();

        }


    }

    public class FetchTracksTask extends AsyncTask<String, Void, TracksPager> {


//        public void FetchArtistTask(){
//
//        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(LOG_TAG, "onPreExecute");
            spotify = api.getService();

        }


        @Override
        protected TracksPager doInBackground(String... params) {
            Log.d(LOG_TAG, "doInBackGround");


            //remove hardcoded and pass to asycnch
            String mArtistName = params[0];

            Log.d(LOG_TAG, "passed artist name=" + mArtistName);

            TracksPager results = spotify.searchTracks(mArtistName);


            //Get the data from the server and return it
            return results;


        }

        @Override
        protected void onPostExecute(TracksPager tracksResults) {
            super.onPostExecute(tracksResults);
            int IMAGE_SIZE_INDEX = 1;
            String imageUrl;
            String trackName;
            // Artist element;

            Log.d(LOG_TAG, "onPostExecute");
            //  mArtistAdapter.clear();

            artistCustomAdapter.clear();
            //   07-02 10:18:20.778: E/AndroidRuntime(12528): java.lang.IndexOutOfBoundsException: Invalid index 2, size is 0

            //com.mightycircuit.www.spotifystreamer
            // List<Artist> listOfArtists = tracksResults.artists.items;
            List<Track> topTracks = tracksResults.tracks.items;

            Log.d(LOG_TAG, "Artist results size=" + topTracks.size());

            for (Track element : topTracks) {
                //for (int i=0; i< listOfArtists.size(); i++) {
                //Log.d(LOG_TAG, "------- size=" + listOfArtists.size());

                //element= listOfArtists.get(i);


                Log.d(LOG_TAG, "images size=" + element.preview_url);


                if (element.preview_url == null) {
                    Log.d(LOG_TAG, "null detected.");
                    break;
                }
                if (element.name == null) {
                    trackName = "ARTIST NOT FOUND";
                } else {
                    trackName = element.name;
                }

                if (element.album.images.size() < IMAGE_SIZE_INDEX)

                {
                    //blank place holder image
                    imageUrl = "https://placeimg.com/100/100/people";
                    Log.d(LOG_TAG, "null image detected.");

                } else {
                    imageUrl = element.album.images.get(IMAGE_SIZE_INDEX).url;
                }


                //mArtistAdapter.add(name);
                ElementAdapter adapterElement = new ElementAdapter(imageUrl, trackName);
                artists.add(adapterElement);


                Log.d(LOG_TAG, "track added to list:" + trackName);
            }
            //effectively hide the stupid keyboard after listView is populated
            listView.requestFocus();

            Log.d(LOG_TAG, "Adapter updated.");

            //mForecastAdapter.notifyDataSetChanged();

        }


    }

}
