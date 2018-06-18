package com.a2mar.smartcards;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

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

public class ParseActivity extends AppCompatActivity {

    private String passedPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parse);
        Intent intent = getIntent();
        passedPath = intent.getStringExtra("path");

        if(!new File(getFilesDir().getAbsolutePath()+"/ExampleList.xml").exists()){
            writeExampleXML();
        }

        writeXML();

        //addListToCollection(cutTitle);


        showComplete();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                startTheNewActivity();
            }
        }, 3000);   //3 seconds



    }

    private void addListToCollection(String listName, String sType, int wordCount) {
        File fListColl = new File(getFilesDir().getAbsolutePath()+"/list_of_collections.xml");
        String fListColl_path = getFilesDir().getAbsolutePath()+"/list_of_collections.xml";

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(fListColl);

            //get root element, node "ListOfCollections"
            Node listOColl = doc.getFirstChild();

            //Test append new Node
            Element newList = doc.createElement("List");
            listOColl.appendChild(newList);

            Attr attr = doc.createAttribute("name");
            attr.setValue(listName);
            newList.setAttributeNode(attr);

            //add the Tag Elements
            Element eType = doc.createElement("Type");
            eType.appendChild(doc.createTextNode(sType));
            newList.appendChild(eType);

            Element eWordCount = doc.createElement("WordCount");
            eWordCount.appendChild(doc.createTextNode(String.valueOf(wordCount)));
            newList.appendChild(eWordCount);

            Element eTraining = doc.createElement("TrainingRounds");
            eTraining.appendChild(doc.createTextNode("0"));
            newList.appendChild(eTraining);

            Element eError = doc.createElement("ErrorQuota");
            eError.appendChild(doc.createTextNode("0"));
            newList.appendChild(eError);

            Element eLearned = doc.createElement("PercentLearned");
            eLearned.appendChild(doc.createTextNode("0"));
            newList.appendChild(eLearned);


            //write the content into xml file
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

    private void writeExampleXML() {
        File exampleFile = new File(getFilesDir().getAbsolutePath()+"/ExampleList.xml");
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            //root element
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("Collection");
            doc.appendChild(rootElement);

            //List element
            Element listElement = doc.createElement("List");
            rootElement.appendChild(listElement);

            //set Attributes to listElement
            Attr attr = doc.createAttribute("name");
            attr.setValue("ExampleList");
            listElement.setAttributeNode(attr);

            //für jede Zeile
            for(int i = 1; i<=10; i++) {

                //vocabulary element
                Element vocElement = doc.createElement("vocabulary");
                listElement.appendChild(vocElement);

                //native Word Element
                Element natWord = doc.createElement("native");
                natWord.appendChild(doc.createTextNode("Wort"+i));
                vocElement.appendChild(natWord);

                //foreign Word Element
                Element forWord = doc.createElement("foreign");
                forWord.appendChild(doc.createTextNode("mot"+i));
                vocElement.appendChild(forWord);

                //Priority Element
                Element priorityElement = doc.createElement("priority");
                priorityElement.appendChild(doc.createTextNode("0"));
                vocElement.appendChild(priorityElement);

                //learn status Element
                Element learnStatus = doc.createElement("learningStat");
                learnStatus.appendChild(doc.createTextNode("0"));
                vocElement.appendChild(learnStatus);

                //write the content into xml file
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(exampleFile);

                transformer.transform(source, result);
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }

    }

    private void startTheNewActivity() {
        Intent newIntent = new Intent(ParseActivity.this, LoadCreate.class);
        startActivity(newIntent);
    }

    private void showComplete() {

        String message = "The new list was successfully added to your collection.";
        TextView tv = findViewById(R.id.tv_accomplished);
        tv.setText(message);
    }

    private void writeXML() {
        try {
            File userText = new File(passedPath);

            int wordCount = 0;

            Scanner scn = new Scanner(new FileInputStream(userText), "UTF-8");

            String separate = ";";

            String sFile = scn.nextLine();

            int indSepTitle = sFile.indexOf(separate);
            String fileTitle = sFile.substring(0,indSepTitle);

            int indTypeTile = sFile.lastIndexOf(separate);
            String sType = sFile.substring(indSepTitle+1, indTypeTile);

            char[] titleChars = fileTitle.toCharArray();
            int chLength = titleChars.length;

            String cutTitle = "";
            for(int i=1; i<chLength;i++){
                cutTitle = cutTitle+titleChars[i];
            }



//            Log.println(Log.ASSERT, "ft","codepoint at 0: "+fileTitle.codePointAt(0));
//            Log.println(Log.ASSERT, "ft","codepoint at 1: "+fileTitle.codePointAt(1));
//            Log.println(Log.ASSERT, "ft","codepoint at 2: "+fileTitle.codePointAt(2));
//
//            Log.println(Log.ASSERT, "ct","codepoint at 0: "+cutTitle.codePointAt(0));
//            Log.println(Log.ASSERT, "ct","codepoint at 1: "+cutTitle.codePointAt(1));
//            Log.println(Log.ASSERT, "ct","codepoint at 2: "+cutTitle.codePointAt(2));

            String fileOutputName = cutTitle+".xml";
            File mOutFile = new File(getFilesDir(), fileOutputName);

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            //root element
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("Collection");
            doc.appendChild(rootElement);

            //List element
            Element listElement = doc.createElement("List");
            rootElement.appendChild(listElement);

            //set Attributes to listElement
            Attr attr = doc.createAttribute("name");
            attr.setValue(cutTitle);
            listElement.setAttributeNode(attr);

            //inside while loop
            while(scn.hasNext()) {
                String line = scn.nextLine();
                String[] parts = line.split(separate);

                //vocabulary element
                Element vocElement = doc.createElement("vocabulary");
                listElement.appendChild(vocElement);

                //native Word Element
                Element natWord = doc.createElement("native");
                natWord.appendChild(doc.createTextNode(parts[0]));
                vocElement.appendChild(natWord);

                //foreign Word Element
                Element forWord = doc.createElement("foreign");
                forWord.appendChild(doc.createTextNode(parts[1]));
                vocElement.appendChild(forWord);

                //Priority Element
                Element priorityElement = doc.createElement("priority");
                priorityElement.appendChild(doc.createTextNode(parts[2]));
                vocElement.appendChild(priorityElement);

                //learn status Element
                Element learnStatus = doc.createElement("learningStat");
                learnStatus.appendChild(doc.createTextNode("0"));
                vocElement.appendChild(learnStatus);

                wordCount++;
            }

            //write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(mOutFile);

            transformer.transform(source, result);

            addListToCollection(cutTitle, sType, wordCount);


        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }

    }
}
