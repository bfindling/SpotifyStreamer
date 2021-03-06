package com.mightycircuit.www.spotifystreamer;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import com.mightycircuit.www.spotifystreamer.MusicService.MusicBinder;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TrackPlayerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TrackPlayerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrackPlayerFragment extends Fragment implements OnPreparedListener, MediaPlayer.OnCompletionListener, View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

//    private MusicService musicSrv;
//    private Intent playIntent;
//    private boolean musicBound=false;
    public static final String LOG_TAG = "SpotifyStreamer";
    private MediaPlayer mPlayer;
    public String trackUrl;

    public DataPassListener mCallback;
    private OnFragmentInteractionListener mListener;
    private String AlbumImageUrl;

    Button playButton;
    Button stopButton;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TrackPlayerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TrackPlayerFragment newInstance(String param1, String param2) {
        TrackPlayerFragment fragment = new TrackPlayerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public TrackPlayerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        trackUrl = "https://open.spotify.com/track/0eGsygTp906u18L0Oimnem";
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);


        }
        mPlayer = new MediaPlayer();
        //get data from main activity
        Bundle args = getArguments();
        if (args != null) {
            trackUrl = args.getString(DataPassListener.DATA_RECEIVE);
            AlbumImageUrl = args.getString(DataPassListener.IMAGE_RECEIVE);
        }
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

       // Toast.makeText(getActivity(), "url:" + trackUrl, Toast.LENGTH_SHORT).show();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_track_player, container, false);

        try {
            mPlayer.setDataSource(trackUrl);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(LOG_TAG, "Error !");
            Toast.makeText(getActivity(), "cannot open track:" + trackUrl, Toast.LENGTH_SHORT).show();
        }
        mPlayer.prepareAsync();
        mPlayer.setOnPreparedListener(this);

        ImageView albumView = (ImageView) rootView.findViewById(R.id.album_image);
        playButton =(Button) rootView.findViewById(R.id.play_button);
        playButton.setTag("PLAY");
        playButton.setOnClickListener(this);

        stopButton =(Button) rootView.findViewById(R.id.stop_button);
        stopButton.setTag("STOP");
        stopButton.setOnClickListener(this);


       // return inflater.inflate(R.layout.fragment_track_player, container, false);

        Picasso.with(getActivity()).load(AlbumImageUrl).resize(640, 640).into(albumView);


        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (DataPassListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement DataPassListener");
        }



    }



    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mPlayer.start();
        Log.d(LOG_TAG,"On Prepared called and media player started");
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Toast.makeText(getActivity(), "This ends your free track preview.", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onClick(View v) {
        String tag=(String) v.getTag();
        Log.d(LOG_TAG, "button tag=" + tag);
        if (v.getTag() == "PLAY") {
            mPlayer.start();

        } else {

            mPlayer.pause();
        }
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
        public void onFragmentInteraction(Uri uri);
    }






}
