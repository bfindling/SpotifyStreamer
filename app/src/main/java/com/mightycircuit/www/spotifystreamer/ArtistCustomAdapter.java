package com.mightycircuit.www.spotifystreamer;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by brian on 6/30/15.
 */
public class ArtistCustomAdapter extends ArrayAdapter<ElementAdapter> {
    public static final String LOG_TAG = "SpotifyStreamer";
    private Context context;
    //private List<Artist> artists;
    private List<ElementAdapter> artistNames;

    public ArtistCustomAdapter(Activity context, List<ElementAdapter> artistNames) {
        super(context, 0 , artistNames);
        this.context= context;
        this.artistNames=artistNames;
    }
    @Override
    public View getView(int position,View convertView, ViewGroup parent){

        ElementAdapter artistPosition = getItem(position);
        Log.d(LOG_TAG, "Position: "+ position + " artist name: " + artistPosition.artist);

        // Adapters recycle views to AdapterViews.
        // If this is a new View object we're getting, then inflate the layout.
        // If not, this view already has the layout inflated from a previous call to getView,
        // and we modify the View widgets as usual.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_icon_artist, parent, false);

        }

        ImageView iconView = (ImageView) convertView.findViewById(R.id.artist_icon_view);

        Picasso.with(context).load(artistPosition.image).resize(300, 300).into(iconView);


        TextView versionNameView = (TextView) convertView.findViewById(R.id.artist_name_view);
        versionNameView.setText(artistPosition.artist);

        return convertView;


    }

}
