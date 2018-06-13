package com.a2mar.smartcards;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

        writeXML();

        showComplete();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                startTheNewActivity();
            }
        }, 3000);   //3 seconds



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

            Scanner scn = new Scanner(new FileInputStream(userText), "UTF-8");

            String separate = ";";
            String fileTitle = scn.nextLine();
            int indSepTitle = fileTitle.indexOf(separate);
            fileTitle = fileTitle.substring(0,indSepTitle);

            String fileOutputName = fileTitle+".xml";
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
            attr.setValue(fileTitle);
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
            }

            //write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(mOutFile);

            transformer.transform(source, result);


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
