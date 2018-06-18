package com.a2mar.smartcards;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class EndPracticeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_practice);

        Button btnBack2List = findViewById(R.id.btn_back_to_lists);
        btnBack2List.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EndPracticeActivity.this, LoadCreate.class);
                startActivity(intent);
            }
        });


    }
}
