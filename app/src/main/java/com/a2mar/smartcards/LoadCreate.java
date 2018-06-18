package com.a2mar.smartcards;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import org.xmlpull.v1.XmlPullParserFactory;

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
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class LoadCreate extends AppCompatActivity {

    private LinearLayout innerParentLayout;
    private int swapStep;

    private VocCollection mListCollection;
    private ArrayList<VocCard> mVocCardList;
    private String[][] voc_f = {{"bread1","apple1","water1"},{"bread2","apple2","water2"},{"bread3","apple3","water3"},{"bread4","apple4","water4"}};
    private String[][] voc_n = {{"Brot1","Apfel1","Wasser1"},{"Brot2","Apfel2","Wasser2"},{"Brot3","Apfel3","Wasser3"},{"Brot4","Apfel4","Wasser4"}};
    private File mPathTry;
    /* private String[] voc_f = {"bread","apple","water"};
    private String[] voc_n = {"Brot","Apfel","Wasser"};*/

    private ArrayList<ArrayList<String>> mListColl;

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

        //parseList();
        
        mListColl = pullList();

        displayList();

        ListenSwap();


    }

    private void ListenSwap() {
        Button swapBtn = (Button) findViewById(R.id.set_swap);
        swapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(LoadCreate.this);


                View innerView = getLayoutInflater().inflate(R.layout.swaptep_dialog,null);
                final EditText etNumber = (EditText) innerView.findViewById(R.id.et_number);
                Button submitBtn = (Button) innerView.findViewById(R.id.btn_submit);
                mBuilder.setView(innerView);
                final AlertDialog dialog = mBuilder.create();
                submitBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!etNumber.getText().toString().isEmpty()){
                            if(Integer.parseInt(etNumber.getText().toString())>0) {
                                swapStep = Integer.parseInt(etNumber.getText().toString());
                            }
                            else{
                                swapStep = 0;
                            }
                        }
                        else{
                            swapStep = 0;
                        }
                        dialog.cancel();
                    }
                });


                dialog.show();
            }
        });

    }

    private void displayList() {
        //check size of mListColl
        Log.println(Log.INFO, "list size", String.valueOf(mListColl.size()));

        for(int i = 0; i<mListColl.size(); i++){

            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View newView = inflater.inflate(R.layout.element, null);
            TextView infoView = newView.findViewById(R.id.Infos);
            infoView.setText("Type: "+mListColl.get(i).get(1)+"\n"
                    +"Words: "+mListColl.get(i).get(2)+"\n"
                    +"Absolved training rounds: "+mListColl.get(i).get(3)+"\n"
                    +"Error Quota: "+mListColl.get(i).get(4)+"%"+"\n"
                    +"Learned: "+mListColl.get(i).get(5)+"%"            );
            TextView titleView = newView.findViewById(R.id.list_title);
            titleView.setText(mListColl.get(i).get(0));
            // Add the new element view construct
            innerParentLayout.addView(newView, innerParentLayout.getChildCount());


        }

    }

    private ArrayList<ArrayList<String>> pullList() {

        File mListCollection = new File(getFilesDir().getAbsolutePath()+"/list_of_collections.xml");
        mListColl = new ArrayList<ArrayList<String>>();
        try {
            InputStream is = new FileInputStream(mListCollection);

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            XmlPullParser parser = factory.newPullParser();

            parser.setInput(is, null);

            //while loop as long as the END_TAG for Collection appears

            int eventType = parser.getEventType();
            //ArrayList<ArrayList<String>> mListColl = new ArrayList<ArrayList<String>>();

            //ArrayList<String> will be created before or at every occurence of the "List" Start_Tag
            ArrayList<String> mIndList = new ArrayList<String>();

            while(eventType != XmlPullParser.END_DOCUMENT){

                //ArrayList<String> mIndList = new ArrayList<String>();
                String tagName = "";

                switch(eventType){

                    case XmlPullParser.START_TAG:
                        tagName = parser.getName();

                        if(tagName.equalsIgnoreCase("List")){
                            mIndList.add(parser.getAttributeValue(null,"name"));
                        }
                        if(tagName.equalsIgnoreCase("Type")){
                            mIndList.add(parser.nextText());
                        }
                        if(tagName.equalsIgnoreCase("WordCount")){
                            mIndList.add(parser.nextText());
                        }
                        if(tagName.equalsIgnoreCase("TrainingRounds")){
                            mIndList.add(parser.nextText());
                        }
                        if(tagName.equalsIgnoreCase("ErrorQuota")){
                            mIndList.add(parser.nextText());
                        }
                        if(tagName.equalsIgnoreCase("PercentLearned")){
                            mIndList.add(parser.nextText());
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        tagName = parser.getName();

                        if(tagName.equalsIgnoreCase("List")){
                            mListColl.add(mIndList);

                            //define mIndList as new instance
                            mIndList = new ArrayList<String>();
                        }
                }
                eventType = parser.next();
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Log-Tests
        for(int i = 0; i<=5; i++) {
            Log.println(Log.INFO, "ArrayList entry "+i, mListColl.get(0).get(i));
        }
        return mListColl;
    }

    private void testLog() {
        File testDir = new File(getFilesDir()+"/Lebensmittel.xml");

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

//            Log.println(Log.ASSERT,"=","=========================");
//
//            for (int temp = 0; temp < nList.getLength(); temp++)
//            {
//                Node node = nList.item(temp);
//                Log.println(Log.ASSERT,"space","");    //Just a separator
//                if (node.getNodeType() == Node.ELEMENT_NODE)
//                {
//                    //Print each employee's detail
//                    Element eElement = (Element) node;
//                    Log.println(Log.ASSERT,"type","Type : "    + eElement.getAttribute("Type"));
//                    Log.println(Log.ASSERT,"word count","Word Count : "  + eElement.getElementsByTagName("WordCount").item(0).getTextContent());
//                    Log.println(Log.ASSERT,"TRounds","Training Rounds : "   + eElement.getElementsByTagName("TrainingRounds").item(0).getTextContent());
//                    Log.println(Log.ASSERT,"E Quota","Error Quota : "    + eElement.getElementsByTagName("ErrorQuota").item(0).getTextContent());
//                    Log.println(Log.ASSERT,"Percent Learned","Percent Learned : "    + eElement.getElementsByTagName("PercentLearned").item(0).getTextContent());
//                }
//            }



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
        ViewGroup parentView = (ViewGroup) view.getParent();
        TextView tv_ListTitle = (TextView) parentView.getChildAt(1);
        String listName = (String) tv_ListTitle.getText();

        createVocList(listName);

        Intent intent = new Intent(LoadCreate.this, PracticeActivity.class);

        intent.putParcelableArrayListExtra("VocList", mVocCardList);
        intent.putExtra("listName",listName);
        intent.putExtra("swapStep", (int) swapStep);

        startActivity(intent);
    }

    private void createVocList(String listName) {

        File fList = new File(getFilesDir().getAbsolutePath()+"/"+listName+".xml");

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(fList);

            mVocCardList = new ArrayList<VocCard>();

            //Node listOColl = doc.getFirstChild();

            NodeList allVocs = doc.getElementsByTagName("vocabulary");

            for(int i = 0; i<allVocs.getLength(); i++){

                Node nNative = allVocs.item(i).getFirstChild().getNextSibling();
                Node nForeign = nNative.getNextSibling().getNextSibling();
                Node nPriority = nForeign.getNextSibling().getNextSibling();

                String sVocNat = nNative.getTextContent();
                String sVocFor = nForeign.getTextContent();
                int sPriority = Integer.parseInt(nPriority.getTextContent());

                Log.println(Log.INFO, "load native", sVocNat);
                Log.println(Log.INFO, "load foreign", sVocFor);

                VocCard newVoc = new VocCard(sVocNat,sVocFor);

                newVoc.setErrorLevel(sPriority);

                mVocCardList.add(newVoc);

            }


        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public void startCreation(View view) {
        Intent intent = new Intent(LoadCreate.this, CreateActivity.class);
        startActivity(intent);
    }

    public void onDeleteList(View view) {
        final ViewGroup parentView = (ViewGroup) view.getParent();
        TextView tv_ListTitle = (TextView) parentView.getChildAt(1);
        final String listName = (String) tv_ListTitle.getText();

        AlertDialog.Builder builder = new AlertDialog.Builder(LoadCreate.this);


        builder.setMessage("Do you want to remove this List?").setTitle("List \""+listName+"\" Selected");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteList(listName);

                innerParentLayout.removeView(parentView);

                //dialog.cancel(); doesn't need to be called here (because positive button?)
            }
        })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //dialog.cancel(); doesn't need to be called here (because negative button?)
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();


    }

    private void deleteList(String listName) {
        File fListColl = new File(getFilesDir().getAbsolutePath()+"/list_of_collections.xml");
        String fListColl_path = getFilesDir().getAbsolutePath()+"/list_of_collections.xml";

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(fListColl);

            Node listOColl = doc.getFirstChild();


            NodeList allLists = doc.getElementsByTagName("List");

            for(int i = 0; i<allLists.getLength(); i++){
                Element targetNode = (Element) allLists.item(i);

                if(targetNode.getAttribute("name").equals(listName)){

                    //this snipped removes the "carriage return" (Zeilenleerschlag)
                    Node prev = targetNode.getPreviousSibling();
                    if (prev != null &&
                            prev.getNodeType() == Node.TEXT_NODE &&
                            prev.getNodeValue().trim().length() == 0) {
                        listOColl.removeChild(prev);
                    }

                    listOColl.removeChild(targetNode);
                }
            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(fListColl_path));

            transformer.transform(source, result);

        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }
}
