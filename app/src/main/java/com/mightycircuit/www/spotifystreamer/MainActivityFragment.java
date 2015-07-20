package com.mightycircuit.www.spotifystreamer;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.Configuration;
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


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TracksPager;
import retrofit.RetrofitError;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements TextView.OnEditorActionListener, AdapterView.OnItemClickListener {
    public static final String LOG_TAG = "SpotifyStreamer";
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String PARCLE_SAVE_KEY = "PARCEL_SAVE_KEY";

    private static final String KEY_ARTISTS = "artist";
    private static final String KEY_IMAGES = "images";

    // TODO: Rename and change types of parameters
    public boolean changeFlag = false;

    public DataPassListener mCallback;

    private ArtistCustomAdapter artistCustomAdapter;
    private EditText editText;
    private FetchArtistTask fetchArtistTask;
    private ArrayList<ElementAdapter> artists;   //the raw-full list of artists + data
    private List<String> artistNamesList; //save a local list of the artists results
    private List<String> artistImagesList; //the final list

//    //parcelables--delete me?
//    private ArtistCustomAdapter mAdapter;    // the list adapter
//    private ArrayList<ElementAdapter> mArtists;    // the person list

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

        artistNamesList = new ArrayList<>();
        artists = new ArrayList<>();

        //original
        //artistCustomAdapter = new ArtistCustomAdapter(getActivity());


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
        Log.d(LOG_TAG, "MainFrag count=" + getFragmentManager().getBackStackEntryCount());
        //Log.d(LOG_TAG, "Frag support count=" + fm.getBackStackEntryCount());

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        if (getFragmentManager().getBackStackEntryCount() < 1) {
            changeFlag = false;
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
        outState.putParcelableArrayList(PARCLE_SAVE_KEY, artists);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        //  Log.d(LOG_TAG, "Search for: " + editText.getText().toString());

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);


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

        //pass the selected artist name to the mainActivity and
        //trigger the second fragment to launch

        String selectedArtist;
        // selectedArtist=getArtistList().get(position);
        ElementAdapter artistPosition = artistCustomAdapter.getItem(position);
        selectedArtist = artistPosition.artist;
        mCallback.passData(selectedArtist, itemFragment);


    }


    //    public void setDataPassListener(DataPassListener callBack){
//        this.mCallback=callBack;
//    }
    private void setArtistList(String vName) {
        //set the search results of just the names field for access by the click listener
        this.artistNamesList.add(vName);
    }

    private List<String> getArtistList() {
        //get the artist names list
        return this.artistNamesList;
    }

    public class FetchArtistTask extends AsyncTask<String, Void, ArtistsPager> {
        Exception error;

        List<ArtistsPager> mArtistPagerTemp = new ArrayList<ArtistsPager>();

//        public void FetchArtistTask(){
//
//        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(LOG_TAG, "onPreExecute");

            try {
                spotify = api.getService();

            } catch (Exception e) {
                toastOnUI(e.toString());
                error=e;
                Log.d(LOG_TAG, "caught error-preExec" +e);
            }



        }


        @Override
        protected ArtistsPager doInBackground(String... params) {
            Log.d(LOG_TAG, "doInBackGround");


            //remove hardcoded and pass to asycnch
            String mArtistName = params[0];


            try {
                ArtistsPager results = spotify.searchArtists(mArtistName);
                return results;
            } catch (Exception e) {
                Log.d(LOG_TAG, "caught error in backGround" +e);
                toastOnUI(e.toString());
                error=e;
                return null;
            }


            //Get the data from the server and return it
            // return results;


        }


        public void toastOnUI(final String msg) {
            // display a toast msg on the main UI
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    Context context = getActivity();
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                }
            });

        }

        @Override
        protected void onPostExecute(ArtistsPager artistsResults) {
            super.onPostExecute(artistsResults);
            int IMAGE_SIZE_INDEX = 1;
            String imageUrl;
            String name;
            // Artist element;



            //  mArtistAdapter.clear();

            //  if (artists != null) {

            artistCustomAdapter.clear();

            if (artistsResults !=null ) {


                //extract just the 20 artists and pics
                List<Artist> listOfArtists = artistsResults.artists.items;
                Log.d(LOG_TAG, "onPostExecute results:" + artistsResults.artists.items);
                if (!listOfArtists.isEmpty()) {

                    // Log.d(LOG_TAG, "Artist results size=" + listOfArtists.size());

                    for (Artist element : listOfArtists) {

                        if (element.images == null) {
                            Log.d(LOG_TAG, "null detected.");
                            break;
                        }
                        if (element.name == null) {
                            name = "ARTIST NOT FOUND";
                        } else {
                            name = element.name;
                            //pass the artists to a field to be shared with listener
                            setArtistList(name);

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


                        //    Log.d(LOG_TAG, "Artist added to list:" + name);
                    }

                } else {
                    //search came back empty
                    toastOnUI("Artist not found.");
                }
            } else {

                toastOnUI("Having trouble retrieving results.." + error);
            }

            //this doesnt work to hide the keyboard after listView is populated
            listView.requestFocus();

            Log.d(LOG_TAG, "Adapter updated.");

            //mForecastAdapter.notifyDataSetChanged();

        }


    }


    private void adapterUpdate(String vImage, String vName) {
        //mArtistAdapter.add(name);
        ElementAdapter adapterElement = new ElementAdapter(vImage, vName);
        artists.add(adapterElement);

    }


}
