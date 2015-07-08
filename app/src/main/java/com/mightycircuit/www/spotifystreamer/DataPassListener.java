package com.mightycircuit.www.spotifystreamer;

import android.app.Fragment;

/**
 * Created by brian on 7/7/15.
 */

//mainActivity implements this to receive the artist name and pass it to the
// second fragment (top 10 trask list)
public interface DataPassListener {
    final static String DATA_RECEIVE = "data_receive";

    public void passData(String selectedArtist, Fragment fragment);

}
