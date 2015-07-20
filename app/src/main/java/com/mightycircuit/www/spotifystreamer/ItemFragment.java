package com.mightycircuit.www.spotifystreamer;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TracksPager;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class ItemFragment extends Fragment implements AbsListView.OnItemClickListener {
    public static final String LOG_TAG = "SpotifyStreamer";
    private static final String PARCLE_SAVE_KEY = "PARCEL_SAVE_KEY";


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private ArtistCustomAdapter trackCustomAdapter;
    private EditText editText;
    private FetchTracksTask fetchTracksTask;
    private ArrayList<ElementAdapter> tracks;    //the raw list of tracks
    private List<String> trackNamesList; //the final list or preview tracks

    private List<String> albumImageList;

    public DataPassListener mCallback;

    private ListView listView;

    private SpotifyApi api;
    private SpotifyService spotify;

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;

//    // TODO: Rename and change types of parameters
//    public static ItemFragment newInstance(String param1, String param2) {
//        ItemFragment fragment = new ItemFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // TODO: copy the same structure from MainActivity Frag

        trackNamesList=new ArrayList<>();
        tracks = new ArrayList<>();

        //saved for track playback
        albumImageList = new ArrayList<>();

        //setup spotify wrapper api
        api = new SpotifyApi();

        //check if first time
        if (savedInstanceState != null) {
            // read the  list from the saved state
            tracks = savedInstanceState.getParcelableArrayList(PARCLE_SAVE_KEY);
            Log.d(LOG_TAG, "onCreateView saved instance NOT null. tracks="+tracks);


        } else {
            //This is the first time inflating this fragment so fetch new track list
            Bundle args = getArguments();
            String selectedTrack=args.getString(DataPassListener.DATA_RECEIVE);

            fetchTracksTask = new FetchTracksTask();
            //fetchTracksTask.execute(artistsCheatGrab.get(position).name.toString());
            fetchTracksTask.execute(selectedTrack);

        }
        trackCustomAdapter = new ArtistCustomAdapter(getActivity(), tracks);

        //Log.d(LOG_TAG, "trackCustomAdapter="+trackCustomAdapter);
        //temporary for debuggin
//        ElementAdapter adapterElement = new ElementAdapter("a", "Artist 1");
//        tracks.add(adapterElement);
//        adapterElement = new ElementAdapter("b", "Artist 2");
//        tracks.add(adapterElement);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(PARCLE_SAVE_KEY, tracks);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tracks, container, false);

        Log.d(LOG_TAG, "Item Frag count=" + getFragmentManager().getBackStackEntryCount());
        // Set the adapter
       // mListView = (AbsListView) view.findViewById(android.R.id.list);
        //((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Get a reference to the ListView, and attach this adapter to it.
        listView = (ListView) rootView.findViewById(R.id.listview_tracks);
        listView.setAdapter(trackCustomAdapter);
        listView.setOnItemClickListener(this);



        // Set OnItemClickListener so we can be notified on item clicks
       // mListView.setOnItemClickListener(this);

        return rootView;
    }




    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);



        try {
            mCallback = (DataPassListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener-oye vey");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is a   artistCustomAdapter = new ArtistCustomAdapter(getActivity(), artists);
            //attached to one) that an item has been selected.
            mListener.onFragmentInteraction(position);
        }

        TrackPlayerFragment trackFragment = new TrackPlayerFragment();

        //pass the selected artist name to the mainActivity and
        //trigger the second fragment to launch
        mCallback.passDataImage(albumImageList.get(position), trackNamesList.get(position), trackFragment);

        //mCallback.passData("test", itemFragment);


        //Toast.makeText(getActivity(), "url:" + trackNamesList.get(position), Toast.LENGTH_SHORT).show();


    }



    private void setTracksList(String vName){
        //set the search results of just the names field for access by the click listener
        this.trackNamesList.add(vName);
    }
    private void setImageList(String vName){
        this.albumImageList.add(vName);

    }
    private List<String> getTracksList(){
        //get the artist names list
        return this.trackNamesList;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(int id);
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

    public class FetchTracksTask extends AsyncTask<String, Void, TracksPager> {
        Exception error;

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
        protected TracksPager doInBackground(String... params) {
            Log.d(LOG_TAG, "doInBackGround");


            //remove hardcoded and pass to asycnch
            String mArtistName = params[0];

           // Log.d(LOG_TAG, "passed artist name=" + mArtistName);



            try {
                TracksPager results = spotify.searchTracks(mArtistName);
                return results;
            } catch (Exception e) {
                Log.d(LOG_TAG, "caught error in backGround" +e);
                toastOnUI(e.toString());
                error=e;
                return null;
            }
            //Get the data from the server and return it



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

            trackCustomAdapter.clear();

            if (tracksResults !=null) {
                //!
                List<Track> topTracks = tracksResults.tracks.items;

                for (Track element : topTracks) {

                    // Log.d(LOG_TAG, "images size=" + element.preview_url);


                    if (element.preview_url == null) {
                        Log.d(LOG_TAG, "null detected.");
                        break;
                    }
                    if (element.name == null) {
                        trackName = "NOT FOUND";
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
                        setImageList(imageUrl);
                    }
                    //pass the track URL to a field to be shared with listener
                    setTracksList(element.preview_url);

                    //mArtistAdapter.add(name);
                    ElementAdapter adapterElement = new ElementAdapter(imageUrl, trackName);
                    tracks.add(adapterElement);


                    //  Log.d(LOG_TAG, "track added to list:" + trackName);
                }

                Log.d(LOG_TAG, "Adapter updated.");
            }
            else {
                //search came back empty due to error
                toastOnUI("Unable to retreive tracks..");
            }

        }


    }

}
