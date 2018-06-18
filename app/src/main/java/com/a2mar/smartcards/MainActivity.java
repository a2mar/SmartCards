package com.a2mar.smartcards;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create Intent and assign the parcelable List for sending to second activity on btn click
        Button startBtn = (Button) findViewById(R.id.button);
        startBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             @SuppressWarnings("unchecked")
             public void onClick(View view) {
                 Intent intent = new Intent(MainActivity.this, LoadCreate.class);

                 startActivity(intent);

             }
        });
    }
}
