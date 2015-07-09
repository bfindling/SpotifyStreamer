package com.mightycircuit.www.spotifystreamer;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brian on 7/8/15.
 */
public class ParcelBag implements Parcelable {
    // parcel keys
    private static final String KEY_ARTISTS = "artist";
    private static final String KEY_IMAGES = "images";

    private ArrayList<String> artistNamesList;
    private ArrayList<String> artistImagesList;

    public ParcelBag(ArrayList<String> artistNamesList, ArrayList<String> artistImagesList) {
        this.artistNamesList = artistNamesList;
        this.artistImagesList = artistImagesList;

    }

    //empty constructor for array creation
    public ParcelBag(){

    }

    public String getArtistNameElement (int pos){
        return artistNamesList.get(pos);
    }
    public String getArtistImageElement (int pos){
        return artistImagesList.get(pos);
    }

    public ArrayList<String> getArtistList (){
        return artistNamesList;
    }
    public ArrayList<String> getImageList (){
        return artistImagesList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // create a bundle for the key value pairs
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(KEY_ARTISTS, artistNamesList);
        bundle.putStringArrayList(KEY_IMAGES, artistImagesList);
        // write the key value pairs to the parcel
        dest.writeBundle(bundle);

    }

    public void readFromParcel (Parcel source){

    }


    public static final Parcelable.Creator<ParcelBag> CREATOR = new Creator<ParcelBag>() {

        @Override
        public ParcelBag createFromParcel(Parcel source) {
            // read the bundle containing key value pairs from the parcel
            Bundle bundle = source.readBundle();

            // instantiate a person using values from the bundle
            return new ParcelBag(bundle.getStringArrayList(KEY_ARTISTS),
                    bundle.getStringArrayList(KEY_IMAGES));
        }

        @Override
        public ParcelBag[] newArray(int size) {
            return new ParcelBag[size];
        }

    };

}
