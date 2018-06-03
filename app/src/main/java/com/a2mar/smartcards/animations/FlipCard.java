package com.a2mar.smartcards.animations;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;

import com.a2mar.smartcards.R;

import java.util.Random;

public class FlipCard extends Activity {

    private AnimatorSet mSetLeftOut;
    private AnimatorSet mSetRightIn;
    private AnimatorSet mSetRightOut;
    private AnimatorSet mSetLeftIn;
    private boolean isBackRevealed = false;
    private boolean isBackVisible = true;
    private View mCardFrontLayout;
    private View mCardBackLayout;
    private int angle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public FlipCard(View front, View back){
        //findViews();
        mCardFrontLayout = front;
        mCardBackLayout = back;
        createRandIncline();
        loadAnimations();
        changeCameraDistance();
        //makeBackCardInvisible();
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
        mCardFrontLayout.setRotation(angle);
    }

    private void findViews() {
        mCardFrontLayout = findViewById(R.id.card_front_frame);
        mCardBackLayout = findViewById(R.id.card_back_frame);
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
