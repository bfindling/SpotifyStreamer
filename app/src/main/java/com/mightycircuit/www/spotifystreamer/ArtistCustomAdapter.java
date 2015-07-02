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

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by brian on 6/30/15.
 */
public class ArtistCustomAdapter extends ArrayAdapter<ArtistAdapter> {
    public static final String LOG_TAG = "SpotifyStreamer";
    private Context context;
    //private List<Artist> artists;
    private List<ArtistAdapter> artistNames;

    public ArtistCustomAdapter(Activity context, List<ArtistAdapter> artistNames) {
        super(context, 0 , artistNames);
        this.context= context;
        this.artistNames=artistNames;
    }
    @Override
    public View getView(int position,View convertView, ViewGroup parent){

        ArtistAdapter artistPosition = getItem(position);
        Log.d(LOG_TAG, "Position: "+ position + " artist name: " + artistPosition.artist);

        // Adapters recycle views to AdapterViews.
        // If this is a new View object we're getting, then inflate the layout.
        // If not, this view already has the layout inflated from a previous call to getView,
        // and we modify the View widgets as usual.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_icon_artist, parent, false);

        }

        ImageView iconView = (ImageView) convertView.findViewById(R.id.artist_icon_view);
        //iconView.setImageResource(artistPosition.image);
       // Picasso.with(context).load(artist.images.get(0).url).into(artistIcon);


        Picasso.with(context).load(artistPosition.image).resize(300, 300).into(iconView);


        TextView versionNameView = (TextView) convertView.findViewById(R.id.artist_name_view);
        versionNameView.setText(artistPosition.artist);

        return convertView;







       //This is eventually going to become the final code minus errors---------
        //ArtistAdapter artistAdapter = getItem(position);
//        Artist artist = artists.get(position);
//        Image image = new Image();
//        String url = null;
//        List<Image> images = artist.images;
//        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_artist, parent, false);
//        ImageView artistIcon =(ImageView) rootView.findViewById(R.id.artist_icon);
//       // Picasso.with(context).load(artist.images.get(0).url).into(artistIcon);
//        if (images != null && !images.isEmpty()) {
//            url = images.get(0).url;
//        }
//        Picasso.with(context).load(url).into(artistIcon);
//        TextView artistName = (TextView) rootView.findViewById(R.id.listview_artists);
//        artistName.setText(artist.name);

//        return rootView;
    }

}
