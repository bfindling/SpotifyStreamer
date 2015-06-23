package com.mightycircuit.www.spotifystreamer;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main,container);

        String[] artistArray = {
                "1",
                "2",
                "3",
                "4",
                "5"
        };
        List<String> weekForecast = new ArrayList<String>(Arrays.asList(artistArray));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),R.layout.list_item_artist,R.id.list_item_artist_textview,weekForecast);


        ListView forecastElement = (ListView) rootView.findViewById(R.id.listView_Artists);

        forecastElement.setAdapter(adapter);



        return inflater.inflate(R.layout.fragment_main, container, false);
    }
}
