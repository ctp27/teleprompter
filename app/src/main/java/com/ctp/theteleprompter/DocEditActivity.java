package com.ctp.theteleprompter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.ctp.theteleprompter.fragments.DocEditActivityFragment;
import com.ctp.theteleprompter.model.Doc;

public class DocEditActivity extends AppCompatActivity {

    public static final String DOC_EDIT_FRAGMENT_TAG = "doc-edit-tag";
    public static final String EXTRA_PARCEL_KEY = "extra-parcel-key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_edit);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Intent recievedIntent = getIntent();
        Doc doc = null;
        if(recievedIntent.hasExtra(EXTRA_PARCEL_KEY)){
            doc = recievedIntent.getParcelableExtra(EXTRA_PARCEL_KEY);
        }
        if(savedInstanceState==null){

            DocEditActivityFragment fragment = new DocEditActivityFragment();

            Bundle bundle = new Bundle();
            bundle.putParcelable(EXTRA_PARCEL_KEY,doc);

            fragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.doc_edit_fragment_container,fragment,DOC_EDIT_FRAGMENT_TAG)
                    .commit();

        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
//        DocEditActivityFragment fragment = (DocEditActivityFragment)
//                getSupportFragmentManager().findFragmentByTag(DOC_EDIT_FRAGMENT_TAG);
//
//        Doc doc = fragment.getDataFromFragment();
//        if(doc!=null) {
//            Log.d(DOC_EDIT_FRAGMENT_TAG,"Updated");
//            if (doc.isNew()) {
//                DocService.insertDoc(this, doc);
//            }
//            else {
//                DocService.updateDoc(this,doc);
//            }
//
//        }
//
        super.onBackPressed();
    }


    @Override
    protected void onPause() {
        super.onPause();

    }
}
