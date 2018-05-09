package com.a2mar.smartcards;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class PracticeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);

        // Get the Intent that started this activity and extract the string

        final ArrayList<VocCard> vocCardList = getIntent().getParcelableArrayListExtra("voc1");

        //Get the Data from the VocCards
        final VocCard card4count = vocCardList.get(0);
        int count = card4count.getCount();
        if(count >= vocCardList.size()){
            // TODO:
             //Create new intent for EndPracticeActivity

            //makeshift statement
            count--;

        }else{
           card4count.increaseCount();
        }
        final VocCard card = vocCardList.get(count);
        String test = card.getVocForeign();

        //Make a TextView display the transfered String
        TextView textView = findViewById(R.id.textView);
        textView.setText(test);

        //Create another intent that recalls same activity recursively
        Button nextBtn = (Button) findViewById(R.id.button2);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void onClick(View v) {
                Intent intent = new Intent(PracticeActivity.this, PracticeActivity.class);
                intent.putParcelableArrayListExtra("voc1",(ArrayList)vocCardList);
                getApplicationContext().startActivity(intent);
            }
        }); /**/

    }
}