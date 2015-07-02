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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements TextView.OnEditorActionListener, AdapterView.OnItemClickListener {
    public static final String LOG_TAG = "SpotifyStreamer";
    public ArrayAdapter<String> mArtistAdapter;

    //custom arrayadapter

    private ArtistCustomAdapter artistCustomAdapter;
    // private ArtistAdapter artistAdapter;
    private EditText editText;
    public FetchArtistTask fetchArtistTask;
    public SpotifyApi api;
    SpotifyService spotify;
    public ArtistsPager results;
    public List<String> mTemporary;

    public List<ArtistAdapter> artists;
//    public ArtistAdapter[] artists = {
//            new ArtistAdapter(R.drawable.null_person,"Billy Joel"),
//            new ArtistAdapter(R.drawable.donut,"The Simpsons"),
//    };


    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


        // fetchArtistTask = new FetchArtistTask();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //mTemporary = new ArrayList<String>(Arrays.asList(data));
        artists = new ArrayList<ArtistAdapter>();

        ArtistAdapter adapterElement = new ArtistAdapter("a", "Artist");
        artists.add(adapterElement);

        // Now that we have some dummy forecast data, create an ArrayAdapter.
        // The ArrayAdapter will take data from a source (like our dummy forecast) and
        // use it to populate the ListView it's attached to.
//        mArtistAdapter =
//                new ArrayAdapter<String>(
//                        getActivity(), // The current context (this activity)
//                        R.layout.list_item_artist, // The name of the layout ID.
//                        R.id.list_item_artist_textview, // The ID of the textview to populate.
//                        mTemporary);


        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //artistCustomAdapter = new ArtistCustomAdapter(getActivity(), Arrays.asList(artists));
        artistCustomAdapter = new ArtistCustomAdapter(getActivity(), artists);


        Log.d(LOG_TAG, "setAdapter");
        // Get a ref to the editText and set a listener to it
        editText = (EditText) rootView.findViewById(R.id.editText);
        editText.setOnEditorActionListener(this);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_artist);
        listView.setAdapter(artistCustomAdapter);
        listView.setOnItemClickListener(this);

        //setup spotify wrapper api
        api = new SpotifyApi();


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


        if(fetchArtistTask == null) {
            // --- create a new task --
            fetchArtistTask = new FetchArtistTask();
            fetchArtistTask.execute(editText.getText().toString());
        }
        else if(fetchArtistTask.getStatus() == AsyncTask.Status.FINISHED){
            // --- the task finished, so start another one --
            fetchArtistTask = new FetchArtistTask();
            fetchArtistTask.execute(editText.getText().toString());
        }


        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        ItemFragment mySecondFragment = new ItemFragment();
//
//        getFragmentManager().beginTransaction()
//                .replace(R.id.fragment, mySecondFragment)
//                .addToBackStack(null)
//                .commit();

    }


    public class FetchArtistTask extends AsyncTask<String, Void, ArtistsPager> {


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

            Log.d(LOG_TAG, "Artist results size=" + listOfArtists.size());

            for (Artist element : listOfArtists) {
            //for (int i=0; i< listOfArtists.size(); i++) {
                //Log.d(LOG_TAG, "------- size=" + listOfArtists.size());

                //element= listOfArtists.get(i);


                Log.d(LOG_TAG, "images size=" + element.images.size());



                if (element.images ==null) {
                    Log.d(LOG_TAG, "null detected.");
                    break;
                }
                if (element.name == null) {
                    name = "ARTIST NOT FOUND";
                } else {
                    name=element.name;
                }

                if ( element.images.size() < IMAGE_SIZE_INDEX)

                {
                    //blank place holder image
                    imageUrl = "https://placeimg.com/100/100/people";
                    Log.d(LOG_TAG, "null image detected.");

                } else {
                    imageUrl = element.images.get(IMAGE_SIZE_INDEX).url;
                }


                //mArtistAdapter.add(name);
                ArtistAdapter adapterElement = new ArtistAdapter(imageUrl, name);
                artists.add(adapterElement);


                Log.d(LOG_TAG, "Artist added to list:" + name);
            }


            Log.d(LOG_TAG, "Adapter updated.");

            //mForecastAdapter.notifyDataSetChanged();

        }


    }

}
