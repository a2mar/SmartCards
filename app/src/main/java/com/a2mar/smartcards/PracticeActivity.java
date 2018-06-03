package com.a2mar.smartcards;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.a2mar.smartcards.animations.FlipCard;

import java.util.ArrayList;
import java.util.Random;
import java.util.function.ToDoubleBiFunction;

public class PracticeActivity extends AppCompatActivity {

    private static ArrayList<VocCard> vocCardList = null;
    private int count;


    private View mCardFrontLayout;
    private View mCardBackLayout;
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
        }
        mCardBackLayout = findViewById(R.id.card_back_frame);
        mCardFrontLayout = findViewById(R.id.card_front_frame);

        createRandIncline();
        loadAnimations();
        changeCameraDistance();
        makeBackCardInvisible();

        //correctContinuePractice(); gehört zu einem neuen onClick
        //wrongContinuePractice();  dito
        applyStringsToCard();

        testVocCard();

        //Create another intent that recalls same activity recursively
        Button nextBtn = (Button) findViewById(R.id.button1);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void onClick(View v) {
                Intent intent = new Intent(PracticeActivity.this, PracticeActivity.class);
                //intent.putParcelableArrayListExtra("voc1",(ArrayList)vocCardList);
                getApplicationContext().startActivity(intent);
            }
        });

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
        //TODO: Switch VocCards within the ArrayList

        Intent intent = new Intent(PracticeActivity.this, PracticeActivity.class);
        getApplicationContext().startActivity(intent);
    }

    public void correctContinuePractice(View view) {
        VocCard card4count = vocCardList.get(0);
        count = card4count.getCount();
        if(count >= vocCardList.size()){
            // TODO:
            //Create new intent for EndPracticeActivity

            //makeshift statement
            count--;

        }else{
            card4count.increaseCount();
        }
        Intent intent = new Intent(PracticeActivity.this, PracticeActivity.class);
        getApplicationContext().startActivity(intent);
    }

    private void applyStringsToCard() {
        VocCard card = vocCardList.get(count);
        String foreignVoc = card.getVocForeign();
        String nativeVoc = card.getVocNative();

        //Make a TextView display the transfered String
        TextView textViewFront = findViewById(R.id.word_foreign);
        textViewFront.setText(foreignVoc);
        TextView textViewBack = findViewById(R.id.word_native);
        textViewBack.setText(nativeVoc);
    }

    private void testVocCard() {

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
