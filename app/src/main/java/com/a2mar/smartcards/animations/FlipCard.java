package com.a2mar.smartcards.animations;

import android.animation.AnimatorSet;
import android.view.View;

public class FlipCard {

    private AnimatorSet mSetLeftOut;
    private AnimatorSet mSetRightIn;
    private AnimatorSet mSetRightOut;
    private AnimatorSet mSetLeftIn;
    private boolean isBackRevealed = false;
    private boolean isBackVisible = true;
    private View mCardFrontLayout;
    private View mCardBackLayout;
    private int angle;


    public FlipCard(){
        findViews();
    }

    private void findViews() {

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
