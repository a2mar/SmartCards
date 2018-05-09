package com.a2mar.smartcards.vocdata;

public class Vocabulary {

    private static String foreign_v;
    private static String native_v;

    public String getForeignV(){
        return foreign_v;
    }

    public String getNativeV(){
        return native_v;
    }

    public Vocabulary(String foreign_v, String native_v){
        this.foreign_v = foreign_v;
        this.native_v = native_v;
    }
}
