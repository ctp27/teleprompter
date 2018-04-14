package com.ctp.theteleprompter.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ctp.theteleprompter.R;
import com.ctp.theteleprompter.ui.ColorView;
import com.ctp.theteleprompter.utils.SharedPreferenceUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DocEditActivityFragment extends Fragment
            implements TextColorDialogFragment.TextColorDialogCallbacks,
                        SeekBar.OnSeekBarChangeListener{


    private static final String BUNDLE_TEXT_COLOR = "bundle_text-color";
    private static final String BUNDLE_BACKGROUND_COLOR = "bundle-background-color" ;
    private static final String BUNDLE_SPEED_NUMBER = "bundle_text-color";
    private static final String BUNDLE_FONT_SIZE_TEXT = "bundle-background-color" ;

    public DocEditActivityFragment() {
    }


    @BindView(R.id.text_color_picker_view)
    ColorView textColorView;

    @BindView(R.id.background_color_picker_view)
    ColorView backgroundColorView;

    @BindView(R.id.seekBar_speed)
    SeekBar scrollSpeedBar;

    @BindView(R.id.seekBar_font_size)
    SeekBar fontSizeBar;

    @BindView(R.id.seekBar_speed_display)
    TextView speedBarTextView;

    @BindView(R.id.font_size_display)
    TextView fontSizeTextView;

    @BindView(R.id.play_button)
    ImageButton playButton;



    private int textColor;
    private int backgroundColor;
    private int speedNumber=1;
    private int fontSize=1;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        textColor = SharedPreferenceUtils.getDefaultTextColor(getContext());
        backgroundColor = SharedPreferenceUtils.getDefaultBackgroundColor(getContext());

        if(savedInstanceState!=null){
            textColor = savedInstanceState.getInt(BUNDLE_TEXT_COLOR);
            backgroundColor = savedInstanceState.getInt(BUNDLE_BACKGROUND_COLOR);
        }


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.doc_edit_fragment,container,false);
        ButterKnife.bind(this,v);


        textColorView.setBackgroundColor(textColor);
        backgroundColorView.setBackgroundColor(backgroundColor);

        textColorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayDialog(textColor,TextColorDialogFragment.TEXT_COLOR_PICKER);

            }
        });


        backgroundColorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               displayDialog(backgroundColor,TextColorDialogFragment.BACKGROUND_COLOR_PICK);
            }
        });

        scrollSpeedBar.setProgress(speedNumber);
        fontSizeBar.setProgress(fontSize);
        speedBarTextView.setText(Integer.toString(speedNumber));
        fontSizeTextView.setText(getFontSizeDisplayStringFromProgress(fontSize));

        scrollSpeedBar.setOnSeekBarChangeListener(this);
        fontSizeBar.setOnSeekBarChangeListener(this);

        return v;
    }

    @Override
    public void onPositiveButtonClick(DialogInterface dialogInterface, int colorId, int pickerType) {
        if(pickerType==TextColorDialogFragment.TEXT_COLOR_PICKER) {
            textColor = colorId;
            textColorView.setBackgroundColor(textColor);

        }
        else {
            backgroundColor = colorId;
            backgroundColorView.setBackgroundColor(backgroundColor);
        }
    }

    @Override
    public void onNegativeButtonClick(DialogInterface dialogInterface) {

    }

    @Override
    public void onNeutralButtonClick(DialogInterface dialogInterface,int colorId, int pickerType){

        onPositiveButtonClick(dialogInterface,colorId,pickerType);

        if(pickerType==TextColorDialogFragment.TEXT_COLOR_PICKER){
            SharedPreferenceUtils.setDefaultTextColor(getContext(),colorId);
        }else {
            SharedPreferenceUtils.setDefaultBackgroundColor(getContext(),colorId);
        }

    }

    private void displayDialog(int color, int pickerType){

        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color >> 0) & 0xFF;

        Bundle bundle = new Bundle();
        bundle.putInt(TextColorDialogFragment.ARGUMENTS_KEY,pickerType);
        bundle.putInt(TextColorDialogFragment.R_VALUE,r);
        bundle.putInt(TextColorDialogFragment.G_VALUE,g);
        bundle.putInt(TextColorDialogFragment.B_VALUE,b);


        TextColorDialogFragment dialogFragment = new TextColorDialogFragment();
        dialogFragment.setArguments(bundle);
        dialogFragment.show(getFragmentManager(),"ColorPicker");

    }



    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(BUNDLE_TEXT_COLOR,textColor);
        outState.putInt(BUNDLE_BACKGROUND_COLOR,backgroundColor);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        updateSeekbarValues();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void updateSeekbarValues(){
        speedNumber = scrollSpeedBar.getProgress();
        speedBarTextView.setText(Integer.toString(speedNumber+1));
        fontSize = fontSizeBar.getProgress();
        fontSizeTextView.setText(getFontSizeDisplayStringFromProgress(fontSize));
    }


    private String getFontSizeDisplayStringFromProgress(int fontSize) {

        switch (fontSize){
            case 0:
                return "S";
            case 1:
                return "N";
            case 2:
                return "M";
            case 3:
                return "L";
                default:
                    return "S";
        }
    }
}
