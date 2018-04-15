package com.ctp.theteleprompter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

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
}
