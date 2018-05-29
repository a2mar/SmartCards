package com.a2mar.smartcards;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.a2mar.smartcards.vocdata.VocCollection;

import java.util.ArrayList;

public class LoadCreate extends AppCompatActivity {

    private LinearLayout innerParentLayout;

    private VocCollection mListCollection;
    private ArrayList<VocCard> mVocCardList;
    private String[][] voc_f = {{"bread1","apple1","water1"},{"bread2","apple2","water2"},{"bread3","apple3","water3"},{"bread4","apple4","water4"}};
    private String[][] voc_n = {{"Brot1","Apfel1","Wasser1"},{"Brot2","Apfel2","Wasser2"},{"Brot3","Apfel3","Wasser3"},{"Brot4","Apfel4","Wasser4"}};
    /* private String[] voc_f = {"bread","apple","water"};
    private String[] voc_n = {"Brot","Apfel","Wasser"};*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_create);

        innerParentLayout = (LinearLayout) findViewById(R.id.inner_parent_layout);
    }

    //Adding element to Layout
    public void addElement(View view) {
        addToList();

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View newView = inflater.inflate(R.layout.element, null);
        TextView infoView = newView.findViewById(R.id.Infos);
        infoView.setText("this is the List No. "+Integer.toString(mListCollection.getIndex()));
        // Add the new element view construct
        innerParentLayout.addView(newView, innerParentLayout.getChildCount());
    }

    //Adding a List of VocCards to the mListCollection
    public void addToList(){
        if(mListCollection == null){
            mListCollection = new VocCollection();
        }
        int indeKx = mListCollection.getIndex();
        if(indeKx ==-1){
            indeKx = 0;
        }
        if(indeKx >3){
            indeKx = 3;
        }
        //Create VocCards and add to a new VocCardList
        mVocCardList = new ArrayList<>(3);
        for (int i = 0; i < 3; i++) {
            mVocCardList.add(new VocCard(voc_f[indeKx][i], voc_n[indeKx][i]));
        }
        //Add new VocCardList to mListCollection
        mListCollection.add(mVocCardList);
        mListCollection.increaseIndex();
    }

    public void onDelete(View v){
        View parent = (View) v.getParent();
        deleteFromList(parent);
        //First remove the VocCardList from mListCollection, and then remove the view
        innerParentLayout.removeView((View)v.getParent());
            //TODO replace (View)v.getParent() with parent

    }

    /*In order to preserve the indexing in synchronicity with the names of the relating TextViews "Infos",
    * the method needs to determine the index of mListCOllectioin element which corresponds to the element-view-construct
    * */
    public void deleteFromList(View parent){
        int index = extractIndex(parent);

        mListCollection.add(index+1, null);     //this null element takes the place of the deleted one, to preserve the indices
        mListCollection.increaseIndex();
        mListCollection.remove(index);
        mListCollection.decreaseIndex();
    }
    /*This method resolves the relation between a View and its specified VocCardList which is inside the mListCollection
    * TODO find out if there is another way to make a reference to an instance within a View or a Layout via xml-Code
    */
    public int extractIndex(View parent){
        TextView infoView = parent.findViewById(R.id.Infos);
        CharSequence infoCharText = infoView.getText();
        String infoText = infoCharText.toString();
        String match = ". ";
        int position = infoText.indexOf(match);
        String indexStr = infoText.substring(position+2);
        int index = Integer.valueOf(indexStr) -1;       // -1, because static int index starts at 1
        return index;
    }

    public void startTraining(View view) {
        Intent intent = new Intent(LoadCreate.this, PracticeActivity.class);
        View parent = (View) view.getParent();
        int index = extractIndex(parent);
        intent.putParcelableArrayListExtra("VocList", (ArrayList<? extends Parcelable>) mListCollection.get(index));
        startActivity(intent);
    }
}
