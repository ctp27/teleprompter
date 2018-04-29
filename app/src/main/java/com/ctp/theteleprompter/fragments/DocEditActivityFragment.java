package com.ctp.theteleprompter.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.support.v4.app.SharedElementCallback;
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

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DocEditActivityFragment extends Fragment
            implements TextColorDialogFragment.TextColorDialogCallbacks,
                        SeekBar.OnSeekBarChangeListener
                        {


    private static final String BUNDLE_TEXT_COLOR = "bundle_text-color";
    private static final String BUNDLE_BACKGROUND_COLOR = "bundle-background-color" ;
    private static final String BUNDLE_TEXT_STORE = "bundle_text-store";
    private static final String BUNDLE_TITLE_STORE = "bundle-title-store" ;
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

    private String titleStore;
    private String textStore;



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
            textStore = thisDoc.getText();
            titleStore = thisDoc.getTitle();
        }

        if(savedInstanceState!=null){
            textColor = savedInstanceState.getInt(BUNDLE_TEXT_COLOR);
            backgroundColor = savedInstanceState.getInt(BUNDLE_BACKGROUND_COLOR);
//            thisDoc = savedInstanceState.getParcelable(BUNDLE_DATA_OBJECT);
            textStore = savedInstanceState.getString(BUNDLE_TEXT_STORE);
            titleStore = savedInstanceState.getString(BUNDLE_TITLE_STORE);
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


        scrollSpeedBar.setProgress(speedNumber);
        fontSizeBar.setProgress(fontSize);
        speedBarTextView.setText(Integer.toString(speedNumber));
        fontSizeTextView.setText(getFontSizeDisplayStringFromProgress(fontSize));

        scrollSpeedBar.setOnSeekBarChangeListener(this);
        fontSizeBar.setOnSeekBarChangeListener(this);

        titleText.setText(thisDoc.getTitle());
        textBody.setText(thisDoc.getText());



        if(savedInstanceState==null){

            if(thisDoc.isNew()){
                titleText.requestFocus();
            }
//            else {
//                textBody.requestFocus();
////                textBody.setSelection(thisDoc.getText().length());
//            }
        }


        setScrollSpeed();
        setFontSize();

       setEnterSharedElementCallback(new SharedElementCallback() {
           @Override
           public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
               super.onMapSharedElements(names, sharedElements);
           }
       });


        return v;
    }

    @OnClick(R.id.background_color_picker_view)
    protected void onBackgroundColorPickerClicked(){
        displayDialog(backgroundColor,TextColorDialogFragment.BACKGROUND_COLOR_PICK);
    }

    @OnClick(R.id.text_color_picker_view)
    protected void onTextColorPickerClicked(){
        displayDialog(textColor,TextColorDialogFragment.TEXT_COLOR_PICKER);
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
        outState.putString(BUNDLE_TITLE_STORE,titleText.getText().toString());
        outState.putString(BUNDLE_TEXT_STORE,textBody.getText().toString());
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
        if(thisDoc.isNew()){
            persistNewDoc();
        }
        else {
            persistOldDoc();
        }
    }


    /**
     * Private method which handles Doc persistence when the user leaves the
     * DocEditActivityFragment. Handles all cases and conditions.
     *
     */

    private void persistNewDoc(){

        String title = titleText.getText().toString();
        String body = textBody.getText().toString();


        /*if title equals the old title and text equals the old text
                return*/
        if(titleStore.equals(title) && textStore.equals(body)){
            Log.d(TAG,"Not going to persist, since its same as last persist");
            return;
        }


        if(title.trim().isEmpty() && body.trim().isEmpty() && orientationChanged){

            /*  If content has been changed and set to empty to empty after persisting it before
            *       delete the doc  */

                thisDoc.setId(SharedPreferenceUtils.getLastStoredId(getContext()));
                thisDoc.setCloudId(SharedPreferenceUtils.getLastStoredCloudId(getContext()));
                DocService.deleteDoc(getContext(),thisDoc);
                Log.d(TAG, "Content changed to empty after orientation. Delete the doc "+thisDoc.getId());
                return;
        }


        if(!title.trim().isEmpty() || !body.trim().isEmpty()){
            /*  If content is not the same, if content is changed after orientation
            * change, update the doc created before orientation change  */
            if(orientationChanged){
                thisDoc.setId(SharedPreferenceUtils.getLastStoredId(getContext()));
                thisDoc.setCloudId(SharedPreferenceUtils.getLastStoredCloudId(getContext()));
                DocService.updateDoc(getContext(),thisDoc);
                Log.d(TAG,"Content not changed to empty after orientation changed, update doc "+thisDoc.getId());
            }
            else {

                /*  If content is not the same, and no orientation change, insert the doc */
                thisDoc.setTitle(title);
                thisDoc.setText(body);
                thisDoc.setUserId(SharedPreferenceUtils.getPrefUserId(getContext()));
                Log.d(TAG,"Content not same and not empty and not persisted before, insert doc "+thisDoc.getId());
                DocService.insertDoc(getContext(),thisDoc);
            }
        }


        textStore = body;
        titleStore = title;


    }

    private void persistOldDoc(){

        String title = titleText.getText().toString();
        String body = textBody.getText().toString();

        if(titleStore.equals(title) && textStore.equals(body)){
            Log.d(TAG,"Not going to persist, since its same as last persist");
        }else {
            Log.d(TAG, "Updating this old doc with Id "+thisDoc.getId());
            thisDoc.setText(body);
            thisDoc.setTitle(title);

            DocService.updateDoc(getContext(),thisDoc);
        }
        textStore = body;
        titleStore = title;


    }

    public void deleteDoc(){

        if(thisDoc.isNew()){
            if(orientationChanged){
                thisDoc.setId(SharedPreferenceUtils.getLastStoredId(getContext()));
                thisDoc.setCloudId(SharedPreferenceUtils.getLastStoredCloudId(getContext()));
                DocService.deleteDoc(getContext(),thisDoc);
            }


        }else {
            DocService.deleteDoc(getContext(),thisDoc);
        }

        getActivity().finish();
    }


    public void shareDoc(){

        String mimeType = "text/plain";
        String title = thisDoc.getTitle();
        String content = thisDoc.getTitle()+"\n\n"+thisDoc.getText();
        ShareCompat.IntentBuilder
                .from(getActivity()).setType(mimeType)
                .setChooserTitle(title)
                .setText(content)
                .startChooser();
    }


}
