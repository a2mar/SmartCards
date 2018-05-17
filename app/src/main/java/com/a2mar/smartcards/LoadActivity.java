package com.a2mar.smartcards;

import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.a2mar.smartcards.vocdata.VocCollection;

import java.util.ArrayList;

public class LoadActivity extends AppCompatActivity {

    private static final String STATE_COLLECTION = "state_of_voc_collection_list";
    //private ArrayList<ArrayList<VocCard>> mListCollection;
    private VocCollection mListCollection;
    private ArrayList<VocCard> mVocCardList;
    private String[] voc_f = {"bread","apple","water"};
    private String[] voc_n = {"Brot","Apfel","Wasser"};
    private LinearLayout ll;
    private ScrollView scrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_load);
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            mListCollection = (VocCollection) savedInstanceState.getParcelableArrayList(STATE_COLLECTION);

        }
        //ScrollView scrl = new ScrollView(this);
        scrl = new ScrollView(this);
        //final LinearLayout ll = new LinearLayout(this);
        ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        scrl.addView(ll);

        Button createBtn = new Button(this);
        createBtn.setText("Create");
        ll.addView(createBtn);

        //These loops are for recreating the data from a persitent storage
        //add for each element from mListCollection a 2 Views to the LinerarLayout ll
        if(mListCollection == null){
            int count = 0;
        }else{
            int count = mListCollection.size();
            for(int i = count; i<count; i++) {
                TextView tv = new TextView(getApplicationContext());
                tv.setText("saved Voc List " + (i + 1));
                ll.addView(tv);
                Button btn = new Button(getApplicationContext());
                btn.setText("Start saved Voc List " + (i + 1));
            }
        }

        // Create Intent and assign the parcelable List for sending to second activity on btn click
        //Button createBtn = (Button) findViewById(R.id.button4);
        createBtn.setOnClickListener(new /*View.*/OnClickListener() {
            @Override
            public void onClick(View view) {
                //

                //Create VocCards and add to List
                mVocCardList = new ArrayList<>(3);
                for (int i = 0; i < 3; i++) {
                    mVocCardList.add(new VocCard(voc_f[i], voc_n[i]));
                }

                int count = 0;
                if(mListCollection != null){
                    count = mListCollection.size();
                    mListCollection.add(mVocCardList);
                }else{
                    //mListCollection = new ArrayList<ArrayList<VocCard>>();
                    mListCollection = new VocCollection();
                    mListCollection.add(mVocCardList);
                }

                TextView tv = new TextView(getApplicationContext());
                tv.setText("new Voc List "+(count+1));
                ll.addView(tv);
                Button btn = new Button(getApplicationContext());
                btn.setText("Start new Voc List "+(count+1));
                ll.addView(btn);

            }
        });
        this.setContentView(scrl);
    }
    public void reCreateViews(){
        // Get the List of List displayed again
        //ll.setOrientation(LinearLayout.VERTICAL);
        //scrl.addView(ll);
        if(mListCollection == null){
            int count = 0;
        }else{
            int count = mListCollection.size();
            for(int i = count; i<count; i++) {
                TextView tv = new TextView(getApplicationContext());
                tv.setText("saved Voc List " + (i + 1));
                ll.addView(tv);
                Button btn = new Button(getApplicationContext());
                btn.setText("Start Voc List " + (i + 1));
                ll.addView(btn);
            }
        }
        this.setContentView(scrl);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);
        //Restore the VocList List
        mListCollection = (VocCollection) savedInstanceState.getParcelableArrayList(STATE_COLLECTION);
    }

    //not working
    //These Loops are for the recreation of the data from instanceState
    @Override
    public void onResume() {

        super.onResume();  // Always call the superclass method first

        // Get the List of List displayed again
//        if(mListCollection == null){
//            int count = 0;
//        }else{
//            int count = mListCollection.size();
//            for(int i = count; i<count; i++) {
//                TextView tv = new TextView(getApplicationContext());
//                tv.setText("saved Voc List " + (i + 1));
//                ll.addView(tv);
//                Button btn = new Button(getApplicationContext());
//                btn.setText("Start Voc List " + (i + 1));
//            }
//        }
        //OR
        reCreateViews();

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putParcelableArrayList(STATE_COLLECTION, (ArrayList<? extends Parcelable>) mListCollection);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);

    }
}
