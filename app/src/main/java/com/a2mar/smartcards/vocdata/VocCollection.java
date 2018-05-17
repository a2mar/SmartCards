package com.a2mar.smartcards.vocdata;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import com.a2mar.smartcards.VocCard;

import java.util.ArrayList;

@SuppressLint("ParcelCreator")
public class VocCollection extends ArrayList implements Parcelable {

    private static int index;

    /*@Override
    public ArrayList<? extends Parcelable> remove(int index){
        index--;
        return super.remove(index);
    }*/

   /* @Override
    public boolean add(ArrayList<VocCard> vc){
        index++;
        return super.add(vc);
    }*/

    public void increaseIndex(){
        index++;
    }

    public void decreaseIndex(){
        index--;
    }

    protected VocCollection(Parcel in) {
        //mCollection = in.readArrayList(VocCard.class.getClassLoader());
    }

    public static final Creator<VocCollection> CREATOR = new Creator<VocCollection>() {
        @Override
        public VocCollection createFromParcel(Parcel in) {
            return new VocCollection(in);
        }

        @Override
        public VocCollection[] newArray(int size) {
            return new VocCollection[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    public int getIndex(){
        return index;
    }

    public VocCollection(){

    }
}
