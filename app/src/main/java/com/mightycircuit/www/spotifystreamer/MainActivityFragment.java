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

    //make this static so i can access it from multiple frags
    //is this bad design?
    public DataPassListener mCallback;

    private ArtistCustomAdapter artistCustomAdapter;
    private EditText editText;
    private FetchArtistTask fetchArtistTask;
    private List<ElementAdapter> artists;   //the raw list of artists
    private List<String> artistNamesList; //the final list

    private SpotifyApi api;
    private SpotifyService spotify;

    private ListView listView;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "MainActivityFragment onCreate");

        setHasOptionsMenu(true);

        artists = new ArrayList<ElementAdapter>();
        artistNamesList=new ArrayList<String>();

        ElementAdapter adapterElement = new ElementAdapter("a", "Artist");
        artists.add(adapterElement);
        artistCustomAdapter = new ArtistCustomAdapter(getActivity(), artists);

        //setup spotify wrapper api
        api = new SpotifyApi();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(LOG_TAG, "MainActivityFragment onCreateView");
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

    //mainActivity implements this to receive the artist name and pass it to the
    // second fragment (top 10 trask list)
//    public interface DataPassListener {
//        public void passData(String selectedArtist);
//    }

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
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        Log.d(LOG_TAG, "Search for: " + editText.getText().toString());

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
        mCallback.passData(getArtistList().get(position), itemFragment);


       // moved this to main activity
//        getFragmentManager().beginTransaction()
//                .replace(R.id.fragment, itemFragment)
//                .addToBackStack(null)
//                .commit();




    }

    //crap maybe implement later!! why is it sooo complicated to simply pass data frag -frag!!!
    public void setDataPassListener(DataPassListener callBack){
        this.mCallback=callBack;
    }
    private void setArtistList (String vName){
        //set the search results of just the names field for access by the click listener
        this.artistNamesList.add(vName);
    }

    private List<String> getArtistList(){
        //get the artist names list
        return this.artistNamesList;
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

            //extract just the 20 artists and pics
            List<Artist> listOfArtists = artistsResults.artists.items;



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


                //mArtistAdapter.add(name);
                ElementAdapter adapterElement = new ElementAdapter(imageUrl, name);
                artists.add(adapterElement);


                Log.d(LOG_TAG, "Artist added to list:" + name);
            }
            //this doesnt work to hide the keyboard after listView is populated
            listView.requestFocus();

            Log.d(LOG_TAG, "Adapter updated.");

            //mForecastAdapter.notifyDataSetChanged();

        }



    }



}
