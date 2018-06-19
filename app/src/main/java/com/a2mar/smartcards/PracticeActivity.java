package com.a2mar.smartcards;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import android.view.View;

import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;


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

public class PracticeActivity extends AppCompatActivity {

    private static ArrayList<VocCard> vocCardList = null;
    private static String listName;
    private static int swapStep;

    private static View mCardFrontLayout;
    private static View mCardBackLayout;
    private AnimatorSet mSetLeftOut;
    private AnimatorSet mSetRightIn;
    private AnimatorSet mSetRightOut;
    private AnimatorSet mSetLeftIn;
    private boolean isBackRevealed = false;
    private boolean isBackVisible = true;
    private int angle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);

        // Get the Intent that started this activity and extract the List
        if(vocCardList ==null) {
            vocCardList = getIntent().getParcelableArrayListExtra("VocList");
            listName = getIntent().getStringExtra("listName");
            swapStep = (int) getIntent().getIntExtra("swapStep",0);

            if(swapStep <=0){
                swapStep = vocCardList.size();
            }

//            Log.println(Log.INFO, "swapStep", String.valueOf(swapStep));
        }
        mCardBackLayout = findViewById(R.id.card_back_frame);
        mCardFrontLayout = findViewById(R.id.card_front_frame);

        createRandIncline();
        loadAnimations();
        changeCameraDistance();
        makeBackCardInvisible();

        adaptCards2ScreenSize();
        applyStringsToCard();
        setProgressBar();

        //Log.println(Log.INFO, "current count", String.valueOf(vocCardList.get(0).getCount()));
    }

//    @Override
//    public void onResume(){
//        super.onResume();
//
//        createRandIncline();
//        loadAnimations();
//        changeCameraDistance();
//        makeBackCardInvisible();
//
//        adaptCards2ScreenSize();
//        applyStringsToCard();
//        setProgressBar();
//    }

    private void update(){
        createRandIncline();
        makeBackCardInvisible();
        adaptCards2ScreenSize();
        applyStringsToCard();
        setProgressBar();
    }

    private void setProgressBar() {
        int progress = (int) (100/vocCardList.size()*(vocCardList.get(0).getCount()+1));
        ProgressBar prBar = (ProgressBar) findViewById(R.id.progress_bar);

        prBar.setProgress(progress);
    }

    private void adaptCards2ScreenSize() {
        DisplayMetrics metrics;
        metrics = getApplicationContext().getResources().getDisplayMetrics();
        FrameLayout frontCard = (FrameLayout) findViewById(R.id.card_front_frame);
        FrameLayout backCard = (FrameLayout) findViewById(R.id.card_back_frame);

        float density = metrics.density;

        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        FrameLayout.LayoutParams layoutParamsF = (FrameLayout.LayoutParams) frontCard.getLayoutParams();

        layoutParamsF.width = (int) (0.7*width);
        layoutParamsF.height = (int) (0.49*width);

        frontCard.setLayoutParams(layoutParamsF);

        FrameLayout.LayoutParams layoutParamsB = (FrameLayout.LayoutParams) backCard.getLayoutParams();
        layoutParamsB.width = (int) (0.7*width);
        layoutParamsB.height = (int) (0.49*width);
        backCard.setLayoutParams(layoutParamsB);
    }

    private void makeBackCardInvisible() {
        mCardBackLayout.setVisibility(View.INVISIBLE);
        isBackVisible = false;
    }

    private void changeCameraDistance() {
        int distance = 5000; // 5000 looks nice
        float scale = getResources().getDisplayMetrics().density * distance;
        mCardFrontLayout.setCameraDistance(scale);
        mCardBackLayout.setCameraDistance(scale);
    }

    private void loadAnimations() {
        mSetLeftOut = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.ani_out_left);
        mSetRightIn = (AnimatorSet) AnimatorInflater.loadAnimator(this, R. animator.ani_in_right);
        mSetRightOut = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.ani_out_right);
        mSetLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.ani_in_left);
    }

    private void createRandIncline() {
        Random rand = new Random();
        angle = rand.nextInt(31) - rand.nextInt(31); // creates angle between -30° and +30°
        mCardBackLayout.setRotation(angle);
        mCardFrontLayout.setRotation(-angle);
    }

    public void wrongContinuePractice(View view) {

        vocCardList.get(vocCardList.get(0).getCount()).increaseErrorLevel();
        swapCards(swapStep);

        if(isBackRevealed){
            mSetRightOut.setTarget(mCardBackLayout);
            mSetLeftIn.setTarget(mCardFrontLayout);
            mSetRightOut.start();
            mSetLeftIn.start();
            isBackRevealed = false;
        }

        update();

//        Intent intent = new Intent(PracticeActivity.this, PracticeActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//        overridePendingTransition(0,0);
//        startActivity(intent);
    }

    private void swapCards(int swapStep) {
        int count = vocCardList.get(0).getCount();

        if((count+swapStep)>= vocCardList.size()){
            vocCardList.add(vocCardList.get(count));
            vocCardList.remove(count);
        }
        else {
            vocCardList.add(count + swapStep, vocCardList.get(count));
            vocCardList.remove(count);
        }
    }

    public void correctContinuePractice(View view) {
        VocCard card4count = vocCardList.get(0);
        vocCardList.get(card4count.getCount()).decreaseErrorLevel();

        if (card4count.getCount() >= vocCardList.size() - 1) {

            printResultsToXML();
            adjustContentInLOCXML();

            //reset VocCard counter
            card4count.resetCount();

            //set vocCardList to null, because it is static
            vocCardList = null;

            Intent intent = new Intent(PracticeActivity.this, EndPracticeActivity.class);
            startActivity(intent);

        } else {
            card4count.increaseCount();

            if(isBackRevealed){
                mSetRightOut.setTarget(mCardBackLayout);
                mSetLeftIn.setTarget(mCardFrontLayout);
                mSetRightOut.start();
                mSetLeftIn.start();
                isBackRevealed = false;
            }

            update();

//            Intent intent = new Intent(PracticeActivity.this, PracticeActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//            overridePendingTransition(50,0);
//            startActivity(intent);
        }
    }

    private void adjustContentInLOCXML() {
        File fListColl = new File(getFilesDir().getAbsolutePath()+"/list_of_collections.xml");
        String fListColl_path = getFilesDir().getAbsolutePath()+"/list_of_collections.xml";

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(fListColl);

            NodeList nListList = doc.getElementsByTagName("List");

            int counter = 0;
            for(int i = 0; i+counter<nListList.getLength(); i++){

                Element thisNode = (Element) nListList.item(i);
                String name = thisNode.getAttribute("name");

                if(name.equals(listName)) {

                    counter = nListList.getLength(); //Stop after this loop

                    NodeList nChildren = thisNode.getChildNodes();
                    int nChSize = nChildren.getLength();
                    Log.println(Log.INFO, "children size", String.valueOf(nChSize));

                    Element eTrainingR = (Element) nChildren.item(5);
//                    String sTrainingRName = eTrainingR.getTagName();
                    String sTrainingRText = eTrainingR.getTextContent();
                    int iNewTrainingR = Integer.parseInt(sTrainingRText)+1;

                    eTrainingR.setTextContent(String.valueOf(iNewTrainingR));
//                    Log.println(Log.INFO, "Training Tag", sTrainingRName);
//                    Log.println(Log.INFO, "Training Text", sTrainingRText);

                    Element eError = (Element) nChildren.item(7);

                    int iErrors = 0;

                    for(int j = 0; j<vocCardList.size(); j++  ){
                        if(vocCardList.get(j).getErrorLevel()>0){
                            iErrors++;
                        }
                    }

                    int iErrorPercent = (int) (100/vocCardList.size()*iErrors);

                    Log.println(Log.INFO, "Error %", String.valueOf(iErrorPercent));

                    /*LEFT THIS LOG-CODE HERE FOR FUTURE ERROR HANDLING*/

//                    String sErrorName = eError.getNodeName();
//                    String  sErrorText = nChildren.item(5).getTextContent();
//
//                    Log.println(Log.INFO, "ErrorTag", "Tag name is: " + sErrorName);
//                    Log.println(Log.INFO, "ErrorQu", "text is: " + sErrorText);

                    Element eLearned = (Element) nChildren.item(9);
                    eError.setTextContent(String.valueOf(iErrorPercent));
                    eLearned.setTextContent(String.valueOf(100-iErrorPercent));

//                    String sLearnedName = eLearned.getNodeName();
//                    String  sLearnedText = eLearned.getTextContent();
//
//                    Log.println(Log.INFO, "LearnedTag", "Tag name is: " + sLearnedName);
//                    Log.println(Log.INFO, "Learned", "text is: " + sLearnedText);
//

                    //Log.println(Log.INFO, "attr name", "name is: " + name);
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


        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }


    }

    private void printResultsToXML() {

        File currentVocList = new File(getFilesDir().getAbsolutePath()+"/"+listName+".xml");
        String currentVocList_path = getFilesDir().getAbsolutePath()+"/"+listName+".xml";

        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(currentVocList);

            NodeList allVocs = doc.getElementsByTagName("vocabulary");
            /*LEFT THIS CODE HERE FOR FUTURE ERROR HANDLING*/
//
//
//            NodeList subList = allVocs.item(0).getChildNodes();
//
//            int subSize = subList.getLength();
//
//            Log.println(Log.INFO, "sublist size", String.valueOf(subSize));
//
//            Node E1 = subList.item(0);
//            String sE1 = E1.getTextContent();
//
//            Node E2 =  subList.item(1);
//            String sE2 = E2.getNodeName();
//
//            Node E3 =  subList.item(2);
//            String sE3 = E3.getTextContent();
//
//            Node E4 =  subList.item(3);
//            String sE4 = E4.getNodeName();
//            String sE4_1 = E4.getTextContent();
//
//
//            Node E5 =  subList.item(4);
//            String sE5 = E5.getTextContent();
//
//            Node E6 =  subList.item(5);
//            String sE6 = E6.getNodeName();
//
//            Node E7 =  subList.item(6);
//            String sE7 = E7.getTextContent();
//
//            Node E8 = subList.item(7);
//            String sE8 = E8.getNodeName();
//
//            Node E9 =  subList.item(8);
//            String sE9 = E9.getTextContent();
//
//            Log.println(Log.INFO, "e1", sE1);
//            Log.println(Log.INFO, "e2", sE2);
//            Log.println(Log.INFO, "e3", sE3);
//            Log.println(Log.INFO, "e4", sE4);
//            Log.println(Log.INFO, "e4.1", sE4_1);
//            Log.println(Log.INFO, "e5", sE5);
//            Log.println(Log.INFO, "e6", sE6);   //index 5 = priority
//            Log.println(Log.INFO, "e7", sE7);
//            Log.println(Log.INFO, "e8", sE8);
//            Log.println(Log.INFO, "e9", sE9);


            for(int i = 0; i<vocCardList.size(); i++){
                int counter = 0;
                for(int j = 0; j+counter<allVocs.getLength(); j++){

                    NodeList childList = allVocs.item(j).getChildNodes();

                    if(vocCardList.get(i).getVocForeign().equals(childList.item(3).getTextContent())){
                        //int oldScore = Integer.parseInt(childList.item(5).getTextContent());
                        childList.item(5).setTextContent(String.valueOf(vocCardList.get(i).getErrorLevel()));
                        counter = allVocs.getLength();
                    }
                }
            }


            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(currentVocList_path));

            transformer.transform(source, result);


        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }

    }

    private void applyStringsToCard() {
        VocCard zeroCard = vocCardList.get(0);

        VocCard card = vocCardList.get(zeroCard.getCount());
        String foreignVoc = card.getVocForeign();
        String nativeVoc = card.getVocNative();

        //Make a TextView display the transfered String
        TextView textViewFront = findViewById(R.id.word_foreign);
        textViewFront.setText(foreignVoc);
        TextView textViewBack = findViewById(R.id.word_native);
        textViewBack.setText(nativeVoc);

        //Adapt text size to display

        int optimumNat = getBestSize(nativeVoc);
        int optimumFor = getBestSize(foreignVoc);

        DisplayMetrics metrics;
        metrics = getApplicationContext().getResources().getDisplayMetrics();
        float fTextsizeN = optimumNat/metrics.density;
        float fTextsizeF = optimumFor/metrics.density;


        textViewBack.setTextSize(fTextsizeN+1);
        textViewFront.setTextSize(fTextsizeF+1);
    }

    private int getBestSize(String voc) {
        if(voc.length()<=7){
            return 120;
        }
        if(voc.length()>7 && voc.length()<=10){
            if(!voc.contains(" ")){
                return 80;
            }
            else{
                if(voc.indexOf(" ")>=2 || voc.lastIndexOf(" ")<=5){
                    return 120;
                }
            }
        }
        if(voc.length() > 10 && voc.length()<= 14){
            if(!voc.contains(" ")) {
                return 60;
            }
            else{
                int counter = 0;
                String tSubject = voc;
                int index = 0;
                while(tSubject.contains(" ")){
                        index = tSubject.indexOf(" ");
                        tSubject = tSubject.substring(index+1);
                        counter++;
                }
                if(counter==1){
                    if(voc.lastIndexOf(" ")<=6 || voc.indexOf(" ")>=2 ){
                        return 80;
                    }
                }
                if(counter >=2){
                    return 100;
                }
            }
        }
        if(voc.length() > 14 && voc.length()<=19){
            if(!voc.contains(" ")) {
                return 40;
            }
            else{
                int counter = 1;
                String tSubject = voc;
                int index = 0;
                while(tSubject.contains(" ")){
                    index = tSubject.indexOf(" ");
                    tSubject = tSubject.substring(index+1);
                    counter++;
                }
                if(counter == 1){
                    if(voc.indexOf(" ")>=2 || voc.lastIndexOf(" ")<=10){
                        return 60;
                    }
                }
                if(counter == 2){
                    if(Math.abs(voc.indexOf(" ")-voc.lastIndexOf(" "))<=8){
                        return 80;
                    }
                }
                if(counter >= 3){

                    if(Math.abs(voc.indexOf(" ")-voc.lastIndexOf(" "))<=8){
                        return 100;
                    }
                    else{
                        return 80;
                    }
                }
            }
        }
        if(voc.length()>19 && voc.length()<=24){
            if(!voc.contains(" ")) {
                return 30;
            }
            else{   // evtl. replace with String.split
                int counter = 1;
                String tSubject = voc;
                int index = 0;
                while(tSubject.contains(" ")){
                    index = tSubject.indexOf(" ");
                    tSubject = tSubject.substring(index+1);
                    counter++;
                }
                if(counter == 1){
                    if(voc.indexOf(" ")>=2 || voc.lastIndexOf(" ")<=15){
                        return 40;
                    }
                }
                if(counter == 2){
                    if(Math.abs(voc.indexOf(" ")-voc.lastIndexOf(" "))<=12){
                        return 60;
                    }
                }
                if(counter >= 3){
                    if(Math.abs(voc.indexOf(" ")-voc.lastIndexOf(" "))<=12){
                        return 80;
                    }
                    else{
                        return 60;
                    }
                }
            }
        }
        if(voc.length() >24){
            if(!voc.contains(" ")) {
                return 20;
            }
            else{
                int counter = 1;
                String tSubject = voc;
                int index = 0;
                while(tSubject.contains(" ")){
                    index = tSubject.indexOf(" ");
                    tSubject = tSubject.substring(index+1);
                    counter++;
                }
                if(counter == 1){
                    if(voc.indexOf(" ")>=2 || voc.lastIndexOf(" ")<=20){
                        return 30;
                    }
                }
                if(counter == 2){
                    if(Math.abs(voc.indexOf(" ")-voc.lastIndexOf(" "))<=17){
                        return 40;
                    }
                }
                if(counter >= 3){
                    if(Math.abs(voc.indexOf(" ")-voc.lastIndexOf(" "))<=17){
                        return 60;
                    }
                    else{
                        return 40;
                    }
                }
            }
        }
        return 0;
    }

    public void flipCard(View view) {
        if(!isBackVisible) {
            mCardBackLayout.setVisibility(View.VISIBLE);
        }
        if (!isBackRevealed) {
            mSetLeftOut.setTarget(mCardFrontLayout);
            mSetRightIn.setTarget(mCardBackLayout);
            mSetLeftOut.start();
            mSetRightIn.start();
            isBackRevealed = true;
        }else {
            mSetRightOut.setTarget(mCardBackLayout);
            mSetLeftIn.setTarget(mCardFrontLayout);
            mSetRightOut.start();
            mSetLeftIn.start();
            isBackRevealed = false;
        }
    }
}
