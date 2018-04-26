package com.ctp.theteleprompter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.ctp.theteleprompter.fragments.DocEditActivityFragment;
import com.ctp.theteleprompter.model.Doc;

import butterknife.BindBool;
import butterknife.ButterKnife;

public class DocEditActivity extends AppCompatActivity {

    @BindBool(R.bool.isTabletPort)
    boolean isTabletPort;

    @BindBool(R.bool.isTabletLand)
    boolean isTabletLand;

    public static final String DOC_EDIT_FRAGMENT_TAG = "doc-edit-tag";
    public static final String EXTRA_PARCEL_KEY = "extra-parcel-key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_edit);


        ButterKnife.bind(this);



        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


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


    private void setActivityDimensions(int height, int width, int x, int y){
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.x = x;
        params.width = width;
        params.y = y;

        this.getWindow().setAttributes(params);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.doc_edit_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        DocEditActivityFragment fragment = (DocEditActivityFragment)
                getSupportFragmentManager().findFragmentByTag(DOC_EDIT_FRAGMENT_TAG);

        switch (id){
            case R.id.action_delete:
                fragment.deleteDoc();
                break;

            case R.id.action_share:
                fragment.shareDoc();
                break;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onPause() {
        super.onPause();

    }
}
