package com.mightycircuit.www.spotifystreamer;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by brian on 6/30/15.
 */
public class ArtistCustomAdapter extends ArrayAdapter<ArtistAdapter> {

    public ArtistCustomAdapter(Activity context, List<ArtistAdapter> artistNames) {
        super(context, 0 , artistNames);
    }
    @Override
    public View getView(int position,View convertView, ViewGroup parent){
        ArtistAdapter artistAdapter = getItem(position);
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_artist, parent, false);

        SquaredImageView artistIcon =(ImageView) rootView.findViewById(R.id.artist_icon);
        artistIcon.setImageResource(artistAdapter.image);

        TextView artistName = (TextView) rootView.findViewById(R.id.listView_Artists);
        artistName.setText(artistAdapter.artist);

        return rootView;
    }

}
