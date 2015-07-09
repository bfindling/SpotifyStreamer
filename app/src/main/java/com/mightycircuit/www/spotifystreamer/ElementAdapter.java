package com.mightycircuit.www.spotifystreamer;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * http://www.perfectapk.com/android-parcelable.html
 */
public class ElementAdapter implements Parcelable {
    // parcel keys
    private static final String KEY_ARTISTS = "artist";
    private static final String KEY_IMAGES = "images";




    String image;
    String artist;
    public ElementAdapter () {

    }

    public ElementAdapter(String vImage, String vArtist){
        this.image=vImage;
        this.artist=vArtist;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
// create a bundle for the key value pairs
        Bundle bundle = new Bundle();
        bundle.putString(KEY_ARTISTS, artist);
        bundle.putString(KEY_IMAGES, image);
        // write the key value pairs to the parcel
        dest.writeBundle(bundle);
    }


    /**
     * Creator required for class implementing the parcelable interface.
     */
    public static final Parcelable.Creator<ElementAdapter> CREATOR = new Creator<ElementAdapter>() {
        @Override
        public ElementAdapter createFromParcel(Parcel source) {
            // read the bundle containing key value pairs from the parcel
            Bundle bundle = source.readBundle();

            // instantiate a person using values from the bundle
            return new ElementAdapter(bundle.getString(KEY_ARTISTS),
                    bundle.getString(KEY_IMAGES));
        }


        @Override
        public ElementAdapter[] newArray(int size) {
            return new ElementAdapter[size];
        }
    };
}
