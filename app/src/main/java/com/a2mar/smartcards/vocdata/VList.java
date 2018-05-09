package com.a2mar.smartcards.vocdata;

import java.util.ArrayList;

public class VList{

    private ArrayList<Vocabulary> vList;

    public void addToList(Vocabulary pair){
        vList.add(pair);
    }

    public Vocabulary getVocabulary(int index){
        return vList.remove(index);
    }

    public int getSize(){
        return vList.size();
    }
}
