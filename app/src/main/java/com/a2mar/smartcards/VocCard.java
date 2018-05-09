package com.a2mar.smartcards;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

@SuppressLint("ParcelCreator")
public class VocCard implements Parcelable{

    private String voc_foreign;
    private String voc_native;
    private boolean learned;
    private int error_level;
    private static int counter;     //in order to fullfill its job as a static variable, counter has to be reset to 0 after every training

    public String getVocForeign(){
        return voc_foreign;
    }

    public void setVocForeign(String voc_f){
        voc_foreign = voc_f;
    }

    public String getVocNative(){
        return voc_native;
    }

    public void setVocNative(String voc_n){
        voc_native = voc_n;
    }

    public boolean checkLearned(){
        return learned;
    }

    public int getErrorLevel(){
        return error_level;
    }

    public void makeLearned(){
        learned = true;
    }

    public void increaseErrorLevel(){
        error_level++;
    }

    public int getCount(){
        return counter;
    }

    public void increaseCount(){
        counter++;
    }

    public static final Creator<VocCard> CREATOR = new Creator<VocCard>() {
        @Override
        public VocCard createFromParcel(Parcel in) {
            return new VocCard(in);
        }

        @Override
        public VocCard[] newArray(int size) {
            return new VocCard[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(voc_foreign);
        dest.writeString(voc_native);
        dest.writeInt(learned ? 1 : 0);
        dest.writeInt(error_level);
    }
    /**
     * This constructor is invoked by the method
     * createFromParcel(Parcel source) of the object CREATOR.
     *
     * The order and number of writing and reading data to and from
     * Parcel should be same
     **/
    private VocCard(Parcel in) {
        voc_foreign = in.readString();
        voc_native = in.readString();
        learned = in.readInt() == 1;
        error_level = in.readInt();
    }

    /**
     * A constructor that initializes the VocCard object.
     **/
    VocCard(String voc_foreign, String voc_native) {
        this.voc_foreign = voc_foreign;
        this.voc_native = voc_native;
    }

}
