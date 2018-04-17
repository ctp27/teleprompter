package com.ctp.theteleprompter;

import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ctp.theteleprompter.model.TeleSpec;
import com.ctp.theteleprompter.ui.SlideShowScrollView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SlideShowActivity extends AppCompatActivity
            implements SlideShowScrollView.ScrollViewListener{
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static final int SCROLL_START_DELAY_MILLIS = 6000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;

    public static final String INTENT_PARCELABLE_EXTRA_KEY = "parcel-data-key";

    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };


    private final View.OnTouchListener mDelayHideTouchListener2 = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
                animators.start();

            }
            return false;
        }
    };





    @BindView(R.id.slide_show_scroller)
    SlideShowScrollView scrollView;

    @BindView(R.id.slide_show_pause)
    Button pauseButton;

    @BindView(R.id.slide_show_play)
    Button playButton;

    @BindView(R.id.fullscreen_content)
    TextView contentView;

    @BindView(R.id.slide_show_bg)
    FrameLayout slideShowBackgroundView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.countdown_view)
    FrameLayout countDownView;

    @BindView(R.id.countdown_text)
    TextView countDownText;

    @BindView(R.id.ui_control_container)
    LinearLayout scrollContainer;

    private String content;
    private boolean isPlaying;

    private int animationDelayMillis;
    private int scrollOffset;

    private AnimatorSet animators;
    private Handler animationHandler;
    private AnimationRunnable animationRunnable;
    private boolean isFirstTime=false;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_show);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(savedInstanceState == null){
            isFirstTime = true;
        }

//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(3));
        TeleSpec teleSpec=null;

        Intent intent = getIntent();
        if(intent!=null && intent.hasExtra(INTENT_PARCELABLE_EXTRA_KEY)){

            teleSpec = intent.getParcelableExtra(INTENT_PARCELABLE_EXTRA_KEY);
        }

//        textView.setScaleX(-1);
//        textView.setScaleY(1);
//        textView.setTranslationX(1);


        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });


        scrollContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                toggle();
                return true;
            }
        });


        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.slide_show_play).setOnTouchListener(mDelayHideTouchListener);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startScrollAnimation();
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               stopScrollAnimation();
            }
        });

        scrollView.setScrollViewListener(this);


        ActionBar actionBar = getSupportActionBar();

        if(teleSpec!=null){
            content = teleSpec.getContent();
            contentView.setText(content);
            contentView.setTextColor(teleSpec.getFontColor());
            setSlideShowFontSize(teleSpec.getFontSize());
            slideShowBackgroundView.setBackgroundColor(teleSpec.getBackgroundColor());
            setAnimationSpeed(teleSpec.getScrollSpeed());
            getSupportActionBar().setTitle(teleSpec.getTitle());
        }

    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);

    }

    @Override
    protected void onResume() {
        super.onResume();
        startCountdown();
    }

    @Override
    protected void onPause() {
        super.onPause();
        animationHandler.removeCallbacks(animationRunnable);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }



    private void startCountdown(){
        showCountDownView();
        new Handler().postDelayed(new CountdownRunnable(),2000);
    }

    private void startScrollAnimation(){
//        scrollView.fullScroll(ScrollView.FOCUS_UP);
        int y = scrollView.getScrollY();
        scrollView.setScrollable(false);
        animationHandler = new Handler();
        animationRunnable = new AnimationRunnable(y);
        animationHandler.postDelayed(animationRunnable,SCROLL_START_DELAY_MILLIS);
        showPauseButton();



    }

    private void stopScrollAnimation(){
        animationHandler.removeCallbacks(animationRunnable);
        scrollView.setScrollable(true);
        showPlayButton();
    }

    private class AnimationRunnable implements Runnable {
        private int scrollTo;

        AnimationRunnable(int to){
            scrollTo = to;
        }

        @Override
        public void run() {
            scrollView.smoothScrollTo(0,scrollTo);
            animationHandler = new Handler();
            animationRunnable = new AnimationRunnable(scrollTo+scrollOffset);
            animationHandler.postDelayed(animationRunnable,animationDelayMillis);
        }
    }


    private class CountdownRunnable implements Runnable{


        @Override
        public void run() {
           int count = Integer.parseInt(countDownText.getText().toString());
           if(count>1){
               count--;
               countDownText.setText(Integer.toString(count));
               new Handler().postDelayed(new CountdownRunnable(),1000);
           }else {
               countDownText.setText(getString(R.string.countdown_start));
               hideCountDownView();
               startScrollAnimation();
           }


        }
    }

    @Override
    public void onScrollChanged(SlideShowScrollView scrollView, int x, int y, int oldx, int oldy) {
        View view = (View) scrollView.getChildAt(scrollView.getChildCount() - 1);
        int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));
        if(diff <=40) {
//            Log.d("blah","scroll bottom reached");
            if(animationHandler!=null) {
                stopScrollAnimation();
            }
        }
    }



    private void showCountDownView(){
        countDownView.setVisibility(View.VISIBLE);
    }

    private void hideCountDownView(){
        countDownView.setVisibility(View.GONE);

    }

    private void showPauseButton(){
        playButton.setVisibility(View.GONE);
        pauseButton.setVisibility(View.VISIBLE);
        isPlaying = true;
    }

    private void showPlayButton(){
        pauseButton.setVisibility(View.GONE);
        playButton.setVisibility(View.VISIBLE);
        isPlaying = false;
    }

    private void setAnimationSpeed(int scrollSpeed){

        switch (scrollSpeed){
            case 0:
                animationDelayMillis = 25;
                scrollOffset = 1;
                break;
            case 1:
                animationDelayMillis = 25;
                scrollOffset = 2;
                break;

            case 2:
                animationDelayMillis = 25;
                scrollOffset = 3;
                break;

            case 3:
                animationDelayMillis = 25;
                scrollOffset = 4;
                break;

            case 4:
                animationDelayMillis = 25;
                scrollOffset = 5;
                break;
        }

    }



    private void setSlideShowFontSize(int fontSize){

        int size = 16;
        switch (fontSize){

            case 0:
                size = 24;
                break;

            case 1:
                size = 32;
                break;

            case 2:
                size = 40;
                break;

            case 3:
                size = 48;
                break;

        }

        contentView.setTextSize(TypedValue.COMPLEX_UNIT_SP,size);

    }

}
