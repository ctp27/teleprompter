package com.ctp.theteleprompter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ctp.theteleprompter.fragments.DocEditActivityFragment;

public class DocEditActivity extends AppCompatActivity {

    public static final String DOC_EDIT_FRAGMENT_TAG = "doc-edit-tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_edit);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if(savedInstanceState==null){

            DocEditActivityFragment fragment = new DocEditActivityFragment();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.doc_edit_fragment_container,fragment,DOC_EDIT_FRAGMENT_TAG)
                    .commit();

        }
    }
}
