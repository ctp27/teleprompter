package com.ctp.theteleprompter.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;

import com.ctp.theteleprompter.DocEditActivity;
import com.ctp.theteleprompter.R;
import com.ctp.theteleprompter.ui.ColorView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TextColorDialogFragment extends DialogFragment
                implements SeekBar.OnSeekBarChangeListener{

    private TextColorDialogCallbacks mCallback;

    public static final int BACKGROUND_COLOR_PICK=100;
    public static final int TEXT_COLOR_PICKER=200;

    public static final String R_VALUE="red";
    public static final String G_VALUE="green";
    public static final String B_VALUE="blue";


    public static final String ARGUMENTS_KEY = "arguments-key";

    @BindView(R.id.seek_red)
    SeekBar redBar;

    @BindView(R.id.seek_green)
    SeekBar greenBar;

    @BindView(R.id.seek_blue)
    SeekBar blueBar;

    @BindView(R.id.color)
    ColorView colorView;

    private int colorId;

    private int pickerType;

    private int red;
    private int green;
    private int blue;

    public interface TextColorDialogCallbacks{
        void onPositiveButtonClick(DialogInterface dialogInterface,int colorId, int pickerType);
        void onNegativeButtonClick(DialogInterface dialogInterface);
        void onNeutralButtonClick(DialogInterface dialogInterface,int colorId, int pickerType);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.activity_select_color,null);
        ButterKnife.bind(this,v);
        Bundle bundle = getArguments();
        if(bundle!=null && bundle.containsKey(ARGUMENTS_KEY)){
            pickerType = bundle.getInt(ARGUMENTS_KEY);
            red = bundle.getInt(R_VALUE);
            green = bundle.getInt(G_VALUE);
            blue = bundle.getInt(B_VALUE);
        }

        redBar.setOnSeekBarChangeListener(this);
        greenBar.setOnSeekBarChangeListener(this);
        blueBar.setOnSeekBarChangeListener(this);

        redBar.setProgress(red);
        greenBar.setProgress(green);
        blueBar.setProgress(blue);

        if(savedInstanceState==null){
            setColorToView();
        }


        builder.setView(v)
                .setPositiveButton(R.string.fire, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!

                        mCallback.onPositiveButtonClick(dialog,colorId,pickerType);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        mCallback.onNegativeButtonClick(dialog);
                    }
                })
                .setNeutralButton(R.string.default_btn, new DialogInterface.OnClickListener() {
                    @Override
                     public void onClick(DialogInterface dialogInterface, int i) {
                        mCallback.onNeutralButtonClick(dialogInterface,colorId,pickerType);
                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }


    public void setTextColorDialogFragmentListener(TextColorDialogCallbacks mCallback){
        this.mCallback = mCallback;
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        setColorToView();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void setColorToView() {
        int r = redBar.getProgress();
        int g = greenBar.getProgress();
        int b = blueBar.getProgress();

        colorId = Color.rgb(r, g, b);
        colorView.setColor(colorId);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface

        Activity a=null;

        if (context instanceof Activity){
            a=(Activity) context;
        }
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
           DocEditActivity docEditActivity =  (DocEditActivity) a;
           mCallback = (DocEditActivityFragment)docEditActivity.getSupportFragmentManager().findFragmentByTag(DocEditActivity.DOC_EDIT_FRAGMENT_TAG);
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(a.toString()
                    + " must implement NoticeDialogListener");
        }
    }

}
