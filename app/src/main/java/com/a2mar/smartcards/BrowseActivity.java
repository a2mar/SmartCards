package com.a2mar.smartcards;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.os.Environment;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;



public class BrowseActivity extends AppCompatActivity {

    private String path;
    private TextView tv;
    private File file;
    private boolean hasParent;
    private String firstPath;
    private static int slashes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        Intent intent = getIntent();

        if(intent.hasExtra("newPath")) {
            String pathDown = intent.getStringExtra("newPath");
            TextView tv_3 = findViewById(R.id.path_list);
            tv_3.setText(pathDown);
            file = new File(pathDown);
            firstPath = file.getAbsolutePath();

            TextView tv1 = findViewById(R.id.title_directory);
            if(pathsLeft() == slashes-1) {
                tv1.setText("Local Storage");
            }
            else{
                int index = firstPath.lastIndexOf("/");
                String topTitle = firstPath.substring(index+1);
                tv1.setText(topTitle);
            }
        }
        else {

            file = Environment.getExternalStorageDirectory();
            firstPath = file.getAbsolutePath();

            TextView tv1 = findViewById(R.id.title_directory);
            tv1.setText("Local Storage");

            slashes = pathsLeft()+1;
        }

        showFiles();
    }

    private void showFiles() {
        File[] currentDir = file.listFiles();

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for(File mFile:currentDir){
            String fullPath = mFile.getAbsolutePath();
            if(mFile.isDirectory()|| fullPath.contains(".csv")|| fullPath.contains(".txt")|| fullPath.contains(".text")) {
                TextView newView = (TextView) inflater.inflate(R.layout.file_entry, null);

                String entry = fullPath.substring(firstPath.length() + 1);
                newView.setText(entry);
                LinearLayout innerParentLayout = findViewById(R.id.parent_f);
                innerParentLayout.addView(newView, innerParentLayout.getChildCount());
            }
        }
    }

    public void goParentFile(View view) {

        if(pathsLeft()>= slashes) {
            int index = firstPath.lastIndexOf("/");
            String parentPath = firstPath.substring(0, index);

            Intent intent = new Intent(BrowseActivity.this, BrowseActivity.class);

            intent.putExtra("newPath", parentPath);
            startActivity(intent);
        }
        else{
            Toast toast = new Toast(this);
            toast.makeText(this,"No higher directory available.", Toast.LENGTH_LONG).show();
        }
    }

    private int pathsLeft() {
        String tSubject = firstPath;
        String subString;
        int counter = 0;
        while(tSubject.contains("/")) {
            int index = tSubject.lastIndexOf("/");
            subString = tSubject.substring(0, index);
            tSubject = subString;
            counter++;
        }
        return counter;
    }

    public void enterDirectory(View view) {
        TextView tv_1 = (TextView) view;
        final String temp = firstPath+"/"+(String)tv_1.getText();

        File mFile = new File(temp);

        if(mFile.isDirectory()) {
            Intent intent = new Intent(BrowseActivity.this, BrowseActivity.class);

            intent.putExtra("newPath", temp);
            startActivity(intent);
        }
        else{

            AlertDialog.Builder builder = new AlertDialog.Builder(BrowseActivity.this);
            builder.setMessage("Do you want to parse this file?").setTitle("File Selected");
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(BrowseActivity.this, ParseActivity.class);
                    intent.putExtra("path", temp);
                    startActivity(intent);
                }
            })
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}
