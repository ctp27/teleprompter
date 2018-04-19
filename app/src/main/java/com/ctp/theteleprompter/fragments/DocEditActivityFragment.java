package com.ctp.theteleprompter.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ctp.theteleprompter.DocEditActivity;
import com.ctp.theteleprompter.R;
import com.ctp.theteleprompter.SlideShowActivity;
import com.ctp.theteleprompter.data.SharedPreferenceUtils;
import com.ctp.theteleprompter.model.Doc;
import com.ctp.theteleprompter.model.TeleSpec;
import com.ctp.theteleprompter.services.DocService;
import com.ctp.theteleprompter.ui.ColorView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DocEditActivityFragment extends Fragment
            implements TextColorDialogFragment.TextColorDialogCallbacks,
                        SeekBar.OnSeekBarChangeListener
                        {


    private static final String BUNDLE_TEXT_COLOR = "bundle_text-color";
    private static final String BUNDLE_BACKGROUND_COLOR = "bundle-background-color" ;
    private static final String BUNDLE_SPEED_NUMBER = "bundle_text-color";
    private static final String BUNDLE_FONT_SIZE_TEXT = "bundle-background-color" ;
    private static final String BUNDLE_DATA_OBJECT = "bundle_data_object";

    private static final String TAG = DocEditActivityFragment.class.getSimpleName();

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

    @BindView(R.id.doc_detail_title)
    EditText titleText;

    @BindView(R.id.doc_detail_text)
    EditText textBody;

    private boolean orientationChanged = false;
    private int textColor;
    private int backgroundColor;
    private int speedNumber;
    private int fontSize;
    private boolean isTextMirrored;
    private boolean returnFromSlideshow=false;
    private Doc thisDoc=null;
    String userId;
    private boolean isPerisited;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = SharedPreferenceUtils.getPrefUserId(getContext());
        speedNumber = SharedPreferenceUtils.getDefaultScrollSpeed(getContext());
        fontSize = SharedPreferenceUtils.getDefaultFontSize(getContext());
        textColor = SharedPreferenceUtils.getDefaultTextColor(getContext());
        backgroundColor = SharedPreferenceUtils.getDefaultBackgroundColor(getContext());

        isPerisited = false;


        Bundle b = getArguments();
        if(b.containsKey(DocEditActivity.EXTRA_PARCEL_KEY)){
            thisDoc = b.getParcelable(DocEditActivity.EXTRA_PARCEL_KEY);
        }

        if(savedInstanceState!=null){
            textColor = savedInstanceState.getInt(BUNDLE_TEXT_COLOR);
            backgroundColor = savedInstanceState.getInt(BUNDLE_BACKGROUND_COLOR);
//            thisDoc = savedInstanceState.getParcelable(BUNDLE_DATA_OBJECT);
            orientationChanged = true;

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



        titleText.setText(thisDoc.getTitle());
        textBody.setText(thisDoc.getText());
        textBody.requestFocus();

        if(savedInstanceState==null){
            textBody.setSelection(thisDoc.getText().length());
        }


        setScrollSpeed();
        setFontSize();


        return v;
    }


    @OnClick(R.id.play_button)
    public void onPlayButtonClick(View v){

        TeleSpec teleSpec = new TeleSpec();
        teleSpec.setBackgroundColor(backgroundColor);
        teleSpec.setFontColor(textColor);
        teleSpec.setTitle(titleText.getText().toString());
        teleSpec.setContent(textBody.getText().toString());
        teleSpec.setFontSize(fontSize);
        teleSpec.setScrollSpeed(speedNumber);
//        if(thisDoc==null) {
//            persistDoc();
//            returnFromSlideshow = true;
//            thisDoc.setNew(false);
//        }
        Intent intent = new Intent(getContext(), SlideShowActivity.class);
        intent.putExtra(SlideShowActivity.INTENT_PARCELABLE_EXTRA_KEY,teleSpec);
        startActivity(intent);


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

    private void setScrollSpeed(){
        scrollSpeedBar.setProgress(speedNumber);
        speedBarTextView.setText(Integer.toString(speedNumber+1));
    }

    private void setFontSize(){
        fontSizeBar.setProgress(fontSize);
        fontSizeTextView.setText(getFontSizeDisplayStringFromProgress(fontSize));
    }

    private void updateSeekbarValues(){

        speedNumber = scrollSpeedBar.getProgress();
        setScrollSpeed();
        fontSize = fontSizeBar.getProgress();
        setFontSize();
    }


    private String getFontSizeDisplayStringFromProgress(int fontSize) {

        switch (fontSize){
            case 0:
                return "S";
            case 1:
                return "M";
            case 2:
                return "L";
            case 3:
                return "XL";
                default:
                    return "S";
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        if(getActivity().isFinishing()) {
            persistDoc();
            isPerisited = true;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(getActivity().isFinishing() && !isPerisited){
            persistDoc();
        }

        Log.d(TAG,"Destroyed Fragment View");

    }

      @Override
      public void onDestroy() {
           super.onDestroy();
           Log.d(TAG,"onDestroy Fragment");

      }

   @Override
   public void onDetach() {
     super.onDetach();
        Log.d(TAG,"Detach called");
    }




                            /**
     * Private method which handles Doc persistence when the user leaves the
     * DocEditActivityFragment. Handles all cases and conditions.
     *
     */
    private void persistDoc(){

        /*  Get the text from the Edit view */
        String title = titleText.getText().toString();
        String body = textBody.getText().toString();



      if(thisDoc.isNew()){

       /*   The doc is new  */

            if(!title.trim().isEmpty() && !body.trim().isEmpty()){

                /*  If title and body are both not empty, populate the object and insert it*/
                thisDoc.setUserId(SharedPreferenceUtils.getPrefUserId(getContext()));
                thisDoc.setTitle(title);
                thisDoc.setText(body);
                Log.d(TAG,"Inserting new Doc");
                DocService.insertDoc(getContext(),thisDoc);

            }

      }
      else {

            /*  if title and body are empty, delete this doc    */
          if(title.trim().isEmpty() && body.trim().isEmpty()){

              DocService.deleteDoc(getContext(),thisDoc);
              Log.d(TAG,"Deleting old doc, coz its empty");

          }else{
                /*  if title and body are not the same, update the doc */
              if(title.equals(thisDoc.getTitle()) && body.equals(thisDoc.getText())) {
                  return;
              }

              thisDoc.setText(body);
              thisDoc.setTitle(title);
              Log.d(TAG,"Updating doc with id "+thisDoc.getId());
              DocService.updateDoc(getContext(),thisDoc);
          }


      }


        /*  Populate thisDoc object with the title and body */



    }


}
