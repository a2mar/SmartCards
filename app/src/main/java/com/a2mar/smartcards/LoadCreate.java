package com.a2mar.smartcards;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.a2mar.smartcards.vocdata.VocCollection;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class LoadCreate extends AppCompatActivity {

    private LinearLayout innerParentLayout;

    private VocCollection mListCollection;
    private ArrayList<VocCard> mVocCardList;
    private String[][] voc_f = {{"bread1","apple1","water1"},{"bread2","apple2","water2"},{"bread3","apple3","water3"},{"bread4","apple4","water4"}};
    private String[][] voc_n = {{"Brot1","Apfel1","Wasser1"},{"Brot2","Apfel2","Wasser2"},{"Brot3","Apfel3","Wasser3"},{"Brot4","Apfel4","Wasser4"}};
    private File mPathTry;
    /* private String[] voc_f = {"bread","apple","water"};
    private String[] voc_n = {"Brot","Apfel","Wasser"};*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_create);

        innerParentLayout = (LinearLayout) findViewById(R.id.inner_parent_layout);

        mPathTry = getFilesDir();
        if(!new File(mPathTry+"/list_of_collections.xml").exists()) {
            //copy res file from raw to local app directory
            copyResFile(mPathTry);
        }

        //testLog();

        //produceList();

        parseList();
        
        displayList();

    }

    private void displayList() {
    }

    private void testLog() {
        File testDir = new File(getFilesDir()+"/%EF%BB%BFLebensmittel.xml");

        if(testDir.exists()){
            Log.println(Log.ASSERT,"yes test","File exists");
        }
        else{
            Log.println(Log.ASSERT,"no Test","File does NOT exists");
        }

        Log.println(Log.ASSERT,"=","=========================");
    }

    private void parseList() {
        try {
            //Get Document Builder
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            //Build Document
            //Document document = builder.parse(mPathTry+"/list_of_collections.xml");
            Document document = builder.parse(new File(mPathTry.getAbsolutePath()+"/list_of_collections.xml"));

            //Normalize the XML Structure; It's just too important !!
            document.getDocumentElement().normalize();

            //Here comes the root node
            Element root = document.getDocumentElement();
            Log.println(Log.ASSERT,"nodename", root.getNodeName());

            //Get all lists
            NodeList nList = document.getElementsByTagName("List");
            Log.println(Log.ASSERT,"=","=========================");

            for (int temp = 0; temp < nList.getLength(); temp++)
            {
                Node node = nList.item(temp);
                Log.println(Log.ASSERT,"space","");    //Just a separator
                if (node.getNodeType() == Node.ELEMENT_NODE)
                {
                    //Print each employee's detail
                    Element eElement = (Element) node;
                    Log.println(Log.ASSERT,"type","Type : "    + eElement.getAttribute("Type"));
                    Log.println(Log.ASSERT,"word count","Word Count : "  + eElement.getElementsByTagName("WordCount").item(0).getTextContent());
                    Log.println(Log.ASSERT,"TRounds","Training Rounds : "   + eElement.getElementsByTagName("TrainingRounds").item(0).getTextContent());
                    Log.println(Log.ASSERT,"E Quota","Error Quota : "    + eElement.getElementsByTagName("ErrorQuota").item(0).getTextContent());
                    Log.println(Log.ASSERT,"Percent Learned","Percent Learned : "    + eElement.getElementsByTagName("PercentLearned").item(0).getTextContent());
                }
            }



        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void copyResFile(File mPathTry) {
        Toast mToast = new Toast(this);
        mToast.makeText(this, "list of collection not yet here", Toast.LENGTH_LONG).show();

        InputStream templateFile = getResources().openRawResource(R.raw.list_of_collections);

        File copyOfLiOfCo = new File(mPathTry, "list_of_collections.xml");

        OutputStream outStream = null;
        try {
            outStream = new FileOutputStream(copyOfLiOfCo);
            byte[] buffer = new byte[8 * 1024];
            int bytesRead;
            while ((bytesRead = templateFile.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            templateFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void produceList() {
        //(1)find list of Collections .xml file
        File listColl = new File(mPathTry+"/list_of_collections.xml");

        //(2)get the names/ pathes of all the current lists and their properties and save them as ArrayList<String> in an ArrayList<List<String>>
        List<List<String>> mCollFileList = new ArrayList<List<String>>();

        try {

            FileInputStream fis = new FileInputStream(listColl);
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(fis, null);
            parser.nextTag();

            extractXML(parser, mCollFileList);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        //(3)Inflate Layout innerParentLayout with multiple Versions of element with the ArrayList<String> from (2)

        //(4) make implementation of mVocCardList (and mListCollection if necessary) with a OnClick method of each element Layout, by retrieving the List-Title

    }

    private void extractXML( XmlPullParser parser, List<List<String>> mCollFileList) {
        try {

            //ArrayList<ArrayList<String>> bigList = new ArrayList<ArrayList<String>>();

            parser.require(XmlPullParser.START_TAG, null, "List");

            while(parser.next() != XmlPullParser.END_TAG){

//                ArrayList<String> innerList = new ArrayList<>();
//
//                parser.require(XmlPullParser.START_TAG, null, "Typ");
//                String sTyp = "";
//                while(parser.next() != XmlPullParser.END_TAG){
//                    sTyp = sTyp + parser.getText();
//                    parser.nextTag();
//                }
//                innerList.add(sTyp);
//
//                parser.require(XmlPullParser.START_TAG, null, "WordCount");
//                String sWordCount = "";
//                while(parser.next() != XmlPullParser.END_TAG){
//                    sWordCount = sWordCount + parser.getText();
//                    parser.nextTag();
//                }
//                innerList.add(sWordCount);
//
//                parser.require(XmlPullParser.START_TAG, null, "TrainingRounds");
//                String sTrainingRounds = "";
//                while(parser.next() != XmlPullParser.END_TAG){
//                    sTrainingRounds = sTrainingRounds + parser.getText();
//                    parser.nextTag();
//                }
//                innerList.add(sTrainingRounds);
//
//                parser.require(XmlPullParser.START_TAG, null, "ErrorQuota");
//                String sErrorQuota = "";
//                while(parser.next() != XmlPullParser.END_TAG){
//                    sErrorQuota = sErrorQuota + parser.nextTag();
//                    parser.nextTag();
//                }
//                innerList.add(sErrorQuota);
//
//                parser.require(XmlPullParser.START_TAG, null, "PercentLearned");
//                String sPercentLearned = "";
//                while(parser.next() != XmlPullParser.END_TAG){
//                    sPercentLearned = sPercentLearned + parser.getText();
//                    parser.nextTag();
//                }
//                innerList.add(sTyp);
//
//                mCollFileList.add(innerList);
            }


        } catch (XmlPullParserException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }






    //Adding element to Layout
    public void addElement(View view) {
        addToList();

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View newView = inflater.inflate(R.layout.element, null);
        TextView infoView = newView.findViewById(R.id.Infos);
        infoView.setText("this is the List No. "+Integer.toString(mListCollection.getIndex()));
        TextView titleView = newView.findViewById(R.id.list_title);
        titleView.setText("List "+Integer.toString(mListCollection.getIndex()));
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

    public void startCreation(View view) {
        Intent intent = new Intent(LoadCreate.this, CreateActivity.class);
        startActivity(intent);
    }
}
