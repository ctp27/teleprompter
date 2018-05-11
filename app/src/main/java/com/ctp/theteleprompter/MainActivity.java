package com.ctp.theteleprompter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ctp.theteleprompter.adapters.DocGridAdapter;
import com.ctp.theteleprompter.data.SharedPreferenceUtils;
import com.ctp.theteleprompter.data.TeleContract;
import com.ctp.theteleprompter.fragments.DeleteConfirmDialogFragment;
import com.ctp.theteleprompter.fragments.RequestInternetDialogFragment;
import com.ctp.theteleprompter.model.Doc;
import com.ctp.theteleprompter.services.DocService;
import com.ctp.theteleprompter.services.TeleWidgetService;
import com.ctp.theteleprompter.utils.TeleUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity
            implements DocGridAdapter.DocGridAdapterCallbacks,
                LoaderManager.LoaderCallbacks<Cursor>,
        DeleteConfirmDialogFragment.DeleteConfirmDialogCallbacks,
        RequestInternetDialogFragment.RequestInternetDialogFragmentCallbacks{

    private static final int DOC_LOADER_ID = 10001;
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String INTENT_EXTRA_NO_PINNED_DOC="no-pinned-doc";
    private static final String EXTRA_DELETED_DOC_KEY = "extra-deleted_doc";
    private static final String EXTRA_DELETED_DOC_POS = "extra-deleted_doc_pos" ;
    private static final String REQUEST_INTERNET_DIALOG_TAG = "request_internet_dialog-tags" ;

    @BindView(R.id.main_doc_recycler_view)
    RecyclerView docGridView;


    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    @BindView(R.id.ad_view)
    AdView mAdView;
    
    @BindColor(R.color.colorDanger)
    int colorDanger;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;

    @BindView(R.id.empty_cursor_text)
    TextView emptyCursorText;

    private SyncDocsReciever mSyncDocsReciever;

    private boolean docsMoved;

    private DocGridAdapter adapter;


    private Doc deletedDoc;
    private int deletedDocPosition;
    private String DELETE_CONF_DIALOG_TAG ="delete_dialog_tag";

//    private IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_drawer);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        docsMoved = false;

        if(savedInstanceState!=null){
            if(savedInstanceState.containsKey(EXTRA_DELETED_DOC_KEY)) {
                deletedDoc = savedInstanceState.getParcelable(EXTRA_DELETED_DOC_KEY);
                deletedDocPosition = savedInstanceState.getInt(EXTRA_DELETED_DOC_POS);
            }
        }


        initializeWidgets();


        if(savedInstanceState==null && getIntent().hasExtra(INTENT_EXTRA_NO_PINNED_DOC)){
            if(getIntent().getBooleanExtra(INTENT_EXTRA_NO_PINNED_DOC,false)){
                /*  If coming from the widget and no docs pinned    */
                Snackbar.make(findViewById(R.id.constraintLayout),
                        getString(R.string.main_no_pinned_docs_msg),
                        Snackbar.LENGTH_LONG).show();
            }
        }




    }



    /**
     * Handles the clicks on the floating add action button. Opens the
     * Edit doc activity for a new doc.
     */
    @OnClick({R.id.fab})
    protected void onFabButtonClicked(){
        Intent intent = new Intent(MainActivity.this,DocEditActivity.class);
        Doc doc = new Doc();
        doc.setTitle("");
        doc.setText("");
        doc.setNew(true);
        doc.setTutorial(false);
        doc.setUserId(SharedPreferenceUtils.getPrefUserId(MainActivity.this));
        intent.putExtra(DocEditActivity.EXTRA_PARCEL_KEY,doc);
        startActivity(intent);
    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        if(id==DOC_LOADER_ID){
            return new CursorLoader(this, TeleContract.TeleEntry.TELE_CONTENT_URI,null,null,null,
                    TeleContract.TeleEntry.COLUMN_PRIORITY+" DESC");
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

        if(data==null || data.getCount()==0){
            showProgressBar(false);
            showEmptyCursorMessage(true);
            return;
        }
        showEmptyCursorMessage(false);
        showProgressBar(false);
        adapter.swapCursor(data);
        Log.d(TAG,"Cursor count is "+data.getCount());
    }

    private void showEmptyCursorMessage(boolean show) {

        emptyCursorText.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        docGridView.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
               drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // You can change this divider to adjust the size of the poster
        int widthDivider = 500;
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        if (nColumns < 2) return 2;
        return nColumns;
    }


    @Override
    public void onDocClicked(Doc doc) {
        Intent intent = new Intent(this, DocEditActivity.class);
        intent.putExtra(DocEditActivity.EXTRA_PARCEL_KEY, doc);
        startActivity(intent);
    }



    private void initializeWidgets(){

        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(numberOfColumns(),StaggeredGridLayoutManager.VERTICAL);

        docGridView.setLayoutManager(manager);
        adapter = new DocGridAdapter(this,null);




        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN
                | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int source = viewHolder.getAdapterPosition();
                int destination = target.getAdapterPosition();
                adapter.move(source,destination);
//

                return true;
            }

            @Override
            public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y) {

                docsMoved = true;


            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Here is where you'll implement swipe to delete

                int position = viewHolder.getAdapterPosition();

                Doc doc = adapter.getDocAtPosition(position);

//                DocService.deleteDoc(MainActivity.this,doc);

                deletedDoc = doc;
                deletedDocPosition = position;

                DeleteConfirmDialogFragment fragment = new DeleteConfirmDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putString(DeleteConfirmDialogFragment.EXTRA_DOC_NAME,doc.getTitle());
                fragment.setArguments(bundle);

                fragment.show(getSupportFragmentManager(),DELETE_CONF_DIALOG_TAG);

            }
        }).attachToRecyclerView(docGridView);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    case R.id.nav_settings:
                        Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
                        startActivity(intent);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_sync:
                        startDocSync();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_logout:
                        logoutUser();
                        break;
                }
                return false;
            }
        });


        View hView =  navigationView.getHeaderView(0);
        TextView nav_user = hView.findViewById(R.id.nav_account_name);
        TextView nav_email = hView.findViewById(R.id.nav_account_email);
        TextView nav_alph = hView.findViewById(R.id.nav_account_name_alphabet);

        String accountName = SharedPreferenceUtils.getPrefUsername(this);
        nav_user.setText(accountName);
        nav_email.setText(SharedPreferenceUtils.getPrefEmail(this));
        nav_alph.setText(accountName.substring(0,1));

        docGridView.setAdapter(adapter);
        MobileAds.initialize(getApplicationContext(),
                getString(R.string.admob_app_id));

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


    }


    /**
     * Called when user confirms deletion of a document. The method deletes the doc permanently
     * @param dialogInterface The dialog interface of the deleteConfirmDialogFragment
     */
    @Override
    public void onConfirmDeleteDocument(DialogInterface dialogInterface) {
        DocService.deleteDoc(this,deletedDoc);
        adapter.deletePosition(deletedDocPosition);
        Snackbar.make(drawerLayout,"Deleted "+ deletedDoc.getTitle(),Snackbar.LENGTH_LONG).show();
        deletedDoc = null;
        deletedDocPosition =-1;

    }

    /**
     * Called when the user cancels the delete. Reinserts the deleted doc back into the
     * recycler view
     * @param dialogInterface
     */
    @Override
    public void onCancelDeleteDocument(DialogInterface dialogInterface) {
//        adapter.reinsertDoc(deletedDoc,deletedDocPosition);
        adapter.notifyDataSetChanged();
    }



    private void startDocSync(){
        if(TeleUtils.isConnectedToNetwork(MainActivity.this)){
            DocService.syncDocs(MainActivity.this,
                    SharedPreferenceUtils.getPrefUserId(MainActivity.this));
        }else {
            RequestInternetDialogFragment fragment = new RequestInternetDialogFragment();
            fragment.show(getSupportFragmentManager(),REQUEST_INTERNET_DIALOG_TAG);
//            :startDialog for internet
        }

    }


    @Override
    public void onUserAcceptsInternetRequest(DialogInterface dialogInterface) {
        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
    }

    @Override
    public void onUserDeniesInternetRequest(DialogInterface dialogInterface) {

    }

    @Override
    protected void onPause() {
        super.onPause();
       unregisterReceiver(mSyncDocsReciever);
    }



    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(DOC_LOADER_ID,null,this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DocService.ACTION_SYNC_STARTED);
        intentFilter.addAction(DocService.ACTION_SYNC_END);
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mSyncDocsReciever = new SyncDocsReciever();
        registerReceiver(mSyncDocsReciever,intentFilter);
    }


    private class SyncDocsReciever extends BroadcastReceiver{

        Snackbar snackbar;

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            switch (action){
                case DocService.ACTION_SYNC_STARTED:
                    showProgressBar(true);
                    break;
                case DocService.ACTION_SYNC_END:
                    showProgressBar(false);
                    break;
                case ConnectivityManager.CONNECTIVITY_ACTION:
                    if(TeleUtils.isConnectedToNetwork(MainActivity.this)){
                        if(snackbar!=null){
                            snackbar.dismiss();
                        }

                    }else {
                        snackbar=Snackbar.make(drawerLayout,"Offline Mode",Snackbar.LENGTH_INDEFINITE);
                        snackbar.show();
                    }
            }

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(deletedDoc!=null){
            outState.putParcelable(EXTRA_DELETED_DOC_KEY,deletedDoc);
            outState.putInt(EXTRA_DELETED_DOC_POS,deletedDocPosition);
        }

    }


    @Override
    protected void onStop() {
        super.onStop();
        if(docsMoved){
            updateDocPositions();
        }
    }

    private void showProgressBar(boolean show){

        mProgressBar.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        docGridView.setVisibility(show ? View.INVISIBLE : View.VISIBLE);

    }

    /**
     * Logs the user out of Firebase Auth and Google Sign in Client.
     * Invalidates the stored user data and updates the widget
     */
    private void logoutUser(){

        /*  Get Firebase Auth  instance*/
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        /*  Sign out of Firebase Authentication*/
        mAuth.signOut();

        /*  Invalidate stored user data */
        SharedPreferenceUtils.invalidateUserDetails(MainActivity.this);

        /*  Delete locally stored docs  */
        DocService.deleteDoc(this,null);

        /*  Get Google Sign in options */
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        /*  Get the Google sign in client and sign out*/
       GoogleSignIn.getClient(this, gso).signOut();


        /* Update the widget since the user is signed out   */
        TeleWidgetService.updateTeleWidgets(this);

        /*  Switch to the Login screen  */
        Intent theIntent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(theIntent);

        /*  Remove  this activity from stack  */
        finish();
    }


    private void updateDocPositions(){
        List<Doc> orderedDocs = adapter.getDocList();

        for(int i=0;i<orderedDocs.size();i++){

            Doc d = orderedDocs.get(i);
//            Log.d(TAG,"Set Priority of "+d.getTitle()+" from "+d.getPriority() +" to "+(orderedDocs.size()-i));
            int newPriority = orderedDocs.size()-i;
            d.setPriority(newPriority);

            DocService.moveDocs(MainActivity.this,d);

        }
        docsMoved = false;
    }
}
