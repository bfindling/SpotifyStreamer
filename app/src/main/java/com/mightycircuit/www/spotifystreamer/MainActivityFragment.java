package com.mightycircuit.www.spotifystreamer;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements TextView.OnEditorActionListener, AdapterView.OnItemClickListener {
    public static final String LOG_TAG = "SpotifyStreamer";

    private static final String PARCLE_SAVE_KEY = "PARCEL_SAVE_KEY";
    private static final String PARCLE_SAVE_KEY_ID = "PARCEL_SAVE_KEY_ID";

    public DataPassListener mCallback;

    private ArtistCustomAdapter artistCustomAdapter;
    private EditText editText;
    private FetchArtistTask fetchArtistTask;
    private ArrayList<ElementAdapter> artists;   //the raw-full list of artists + data
    private List<String> artistIdList; //save a local list of the artists results

    private SpotifyApi api;
    private SpotifyService spotify;

    private ListView listView;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "MainActivijava.lang.StringtyFragment onCreate");
        Log.d(LOG_TAG, "artistCustomAdapter=" + artistCustomAdapter);
        setHasOptionsMenu(true);

        artistIdList = new ArrayList<>();
        artists = new ArrayList<>();

        //setup spotify wrapper api
        api = new SpotifyApi();


        if (savedInstanceState != null) {
            // read the  list from the saved state
            artists = savedInstanceState.getParcelableArrayList(PARCLE_SAVE_KEY);
            Log.d(LOG_TAG, "onCreateView saved instance NOT null");

        } else {
            //NA
            //test

        }

        artistCustomAdapter = new ArtistCustomAdapter(getActivity(), artists);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // android.support.v4.app.FragmentManager fm = getFragmentManager();

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        Log.d(LOG_TAG, "MainFrag count=" + getFragmentManager().getBackStackEntryCount());
        if (getFragmentManager().getBackStackEntryCount() < 1 ) {

            Log.d(LOG_TAG, "MainActivityFragment onCreateView inside backstack =0");


            if (savedInstanceState == null) {

                // Get a ref to the editText and set a listener to it
                editText = (EditText) rootView.findViewById(R.id.editText);
                editText.setOnEditorActionListener(this);
            } else {
                Log.d(LOG_TAG, "trying to update the adapter..");
                artists = savedInstanceState.getParcelableArrayList(PARCLE_SAVE_KEY);
            }
            // Get a reference to the ListView, and attach this adapter to it.
            listView = (ListView) rootView.findViewById(R.id.listview_artist);
            listView.setAdapter(artistCustomAdapter);
            listView.setOnItemClickListener(this);
            return rootView;
        } else return null;
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(LOG_TAG, "MainActivityFragment onAttach");

        // Make sure that main activity implements the callback interface
        try {
            mCallback = (DataPassListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement DataPassListener in main Activity");
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //outState.putParcelableArrayList(PARCLE_SAVE_KEY, artists);
        outState.putParcelableArrayList(PARCLE_SAVE_KEY, artists);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

        Log.d(LOG_TAG, "search for:" + editText.getText().toString());
        spotifySearch(editText.getText().toString());


//        if (fetchArtistTask == null) {
//            // --- create a new task --
//            fetchArtistTask = new FetchArtistTask();
//            fetchArtistTask.execute(editText.getText().toString());
//        } else if (fetchArtistTask.getStatus() == AsyncTask.Status.FINISHED) {
//            // --- the task finished, so start another one --
//            fetchArtistTask = new FetchArtistTask();
//            fetchArtistTask.execute(editText.getText().toString());
//        }


        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ItemFragment itemFragment = new ItemFragment();

        //pass the selected artist name to the mainActivity and
        //trigger the second fragment to launch

        String selectedArtist;

//        ElementAdapter artistPosition = artistCustomAdapter.getItem(position);
//        selectedArtist = artistPosition.artist;
//        mCallback.passData(selectedArtist, itemFragment);
        mCallback.passData(getArtistId(position), itemFragment);

    }

    private String getArtistId(int position){

        return artistIdList.get(position);
    }

    private void setArtistIdList(String vName) {
        //set the search results of just the names field for access by the click listener

       this.artistIdList.add(vName);
    }

    private List<String> getArtistList() {
        //get the artist names list
        return this.artistIdList;
    }

    public void showArtists(ArtistsPager artistsResults){

        int IMAGE_SIZE_INDEX = 1;
        String imageUrl;
        String name;
        // Artist element;
        Log.d(LOG_TAG, "showArtist:");
        Log.d(LOG_TAG, "Results:" + artistsResults.artists.items);

        artistCustomAdapter.clear();

        //extract just the 20 artists and pics
        List<Artist> listOfArtists = artistsResults.artists.items;
        if (!listOfArtists.isEmpty()) {

            for (Artist element : listOfArtists) {

                if (element.images == null) {
                    Log.d(LOG_TAG, "null detected.");
                    break;
                }
                if (element.name == null) {
                    name = "ARTIST NOT FOUND";
                } else {
                    name = element.name;
                    //pass the artists id to a list for use by click
                    setArtistIdList(element.id);
                }

                if (element.images.size() < IMAGE_SIZE_INDEX)

                {
                    //blank place holder image
                    imageUrl = "https://placeimg.com/100/100/people";
                    Log.d(LOG_TAG, "null image detected.");

                } else {
                    imageUrl = element.images.get(IMAGE_SIZE_INDEX).url;
                }

                //add to the adapterartists.isEmpty()
                adapterUpdate(imageUrl, name);
            }

        } else {
            //if (artists.isEmpty()) {
            Context context = getActivity();
            Toast.makeText(context, "Artist not found.", Toast.LENGTH_LONG).show();
        }
        //this doesnt work to hide the keyboard after listView is populated
        listView.requestFocus();

        Log.d(LOG_TAG, "Adapter updated.");

    }

    public void spotifySearch(String name) {
        SpotifyService spotify = api.getService();
        spotify.searchArtists(name, new Callback<ArtistsPager>() {
            @Override
            public void success(final ArtistsPager artistsPager, Response response) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(LOG_TAG, "spotify search.");
                        showArtists(artistsPager);
                    }
                });
            }

            @Override
            public void failure(final RetrofitError error) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //showErrorMessage(error.getMessage());

                        if (error == null) {
                            Log.d(LOG_TAG, "null detected.");
                        } else
                        if (error == null) {
                            Toast.makeText(getActivity(), "Network troubles...", Toast.LENGTH_LONG).show();
                        } else {

                        }




                    }
                });
            }
        });
    }


    public class FetchArtistTask extends AsyncTask<String, Void, ArtistsPager> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(LOG_TAG, "onPreExecute");
            spotify = api.getService();

        }


        @Override
        protected ArtistsPager doInBackground(String... params) {
            Log.d(LOG_TAG, "doInBackGround");

            String mArtistName = params[0];
            ArtistsPager results = spotify.searchArtists(mArtistName);

            //Get the data from the server and return it
            return results;


        }
        @Override
        protected void onPostExecute(ArtistsPager artistsResults) {
            super.onPostExecute(artistsResults);
            showArtists(artistsResults);

        }






    }


    private void adapterUpdate(String vImage, String vName) {
        ElementAdapter adapterElement = new ElementAdapter(vImage, vName);
        artists.add(adapterElement);

    }


}
