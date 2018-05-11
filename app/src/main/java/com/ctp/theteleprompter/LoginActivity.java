package com.ctp.theteleprompter;

import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ctp.theteleprompter.data.SharedPreferenceUtils;
import com.ctp.theteleprompter.fragments.ForgotPasswordDialogFragment;
import com.ctp.theteleprompter.fragments.RequestInternetDialogFragment;
import com.ctp.theteleprompter.services.DocService;
import com.ctp.theteleprompter.utils.TeleUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity
        implements LoaderCallbacks<Cursor>,
        RequestInternetDialogFragment.RequestInternetDialogFragmentCallbacks,
        ForgotPasswordDialogFragment.ForgotPasswordDialogCallbacks{


    public static final String TAG = LoginActivity.class.getSimpleName();
    /**
     * Id to identity READ_CONTACTS permission request.
     */

    private static final int LOGIN_TYPE_EMAIL_PASS = 900;
    private static final int LOGIN_TYPE_GOOGLE_SIGN_IN = 901;
    private static final int REQUEST_READ_CONTACTS = 0;
    private static final int RC_SIGN_IN = 101;
    private static final String FORGOT_PASSWORD_DIALOG_TAG = "Fogot-password_dialog";
    private static final String REQUEST_INTERNET_DIALOG_TAG = "request_internet_dialog";


    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */


    // UI references.
    @BindView(R.id.email)
   AutoCompleteTextView mEmailView;

    @BindView(R.id.password)
    EditText mPasswordView;

    @BindView(R.id.login_progress)
    View mProgressView;

    @BindView(R.id.login_form)
    View mLoginFormView;

    @BindView(R.id.login_container)
    LinearLayout loginContainer;

    @BindView(R.id.sign_up_button)
    Button signUpButton;

    @BindView(R.id.google_sign_in_button)
    SignInButton googleSignInButton;

    @BindView(R.id.email_sign_in_button)
    Button mEmailSignInButton;

    @BindView(R.id.forgot_pass_text)
    TextView forgotPasswordText;



    private FirebaseAuth mAuth;

    private  GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        ButterKnife.bind(this);

        /*  Set the google sign in button size  */
        googleSignInButton.setSize(SignInButton.SIZE_WIDE);

        /*  Populate the autocomplete   */
//        populateAutoComplete();

        /*  Get Firebase Auth instance  */
        mAuth = FirebaseAuth.getInstance();

        /*  Build the google sign in options*/
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestId()
                .requestIdToken(getString(R.string.webclient_server_id))
                .build();

        /*  Get the google client using google sign in options  */
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);


        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });


    }



    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }


    private boolean mayRequestContacts() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }



    /**
     * Handles the google signin click event
     */
    @OnClick(R.id.google_sign_in_button)
    protected void handleGoogleSignInButtonClick() {
        if(TeleUtils.isConnectedToNetwork(this)) {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }else {
            showRequestInternetDialog();
        }
    }

    /**
     * Handles Sign up button click event
     */
    @OnClick(R.id.sign_up_button)
    protected void handleSignUpButtonClick() {
        /*  Create and start the Sign Up activity   */
        Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Handles forgot password button Click event
     */
    @OnClick(R.id.forgot_pass_text)
    protected void handleForgotPasswordClick() {
        /*  Display the forgot password dialog  */

        if(!TeleUtils.isConnectedToNetwork(this)){
            showRequestInternetDialog();
            return;
        }

        ForgotPasswordDialogFragment forgotPasswordDialog = new ForgotPasswordDialogFragment();
        forgotPasswordDialog.show(getSupportFragmentManager(),FORGOT_PASSWORD_DIALOG_TAG);
    }



    /**
     * Handles the event when the the user enters the email in the forgot password dialog and hits
     * send email.
     * @param dialogInterface The dialog interface object of the forgot password dialog
     * @param email The valid email entered by the user
     */
    @Override
    public void onSendForgotPassEmailClicked(DialogInterface dialogInterface, String email) {



        /*  Send the password reset email by passing the email */
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        /* Task is complete */

                        if (task.isSuccessful()) {

                            /*  Email sending successful! Yay notify the user! */

                            updateUI(null,getString(R.string.reset_password_dialog_email_sent));
                        }
                        else {
                            /*  Email not sent due to some error. Get the exception    */
                            Exception e = task.getException();

                            if(e instanceof FirebaseAuthInvalidUserException){

                                /*  This email was never registered.
                                Notify the user to check the email  */
                                updateUI(null,getString(R.string.reset_password_dialog_emailnotfound));
                            }
                            else {
                                /*  Notify the user that an error occured   */
                                updateUI(null,getString(R.string.reset_password_dialog_email_error));
                            }
                        }
                    }
                });
    }

    /**
     * Callback received when a permissions request has been completed.
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    @OnClick(R.id.email_sign_in_button)
    protected void attemptLogin() {


        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        if(!TeleUtils.isConnectedToNetwork(this)){
            showRequestInternetDialog();
            return;
        }

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) && TeleUtils.isValidPassword(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!TeleUtils.isValidEmail(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);


            /*  Authenticate using google authentication. Pass email and password   */
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
//                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user,null);
                            } else {
                                // If sign in fails, display a message to the user.
//                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Exception exception = task.getException();

                                if(exception instanceof FirebaseAuthInvalidCredentialsException){
                                    updateUI(null,getString(R.string.login_incorrect_password));
                                }else if(exception instanceof FirebaseAuthInvalidUserException){
                                    updateUI(null,getString(R.string.login_account_not_exists));
                                }else {
                                    updateUI(null, getString(R.string.authentication_failed));
                                }
                            }

                            // ...
                        }
                    });
        }
    }

    /**
     * Called after an activity sends back a result
     * @param requestCode The request code while starting the activity
     * @param resultCode   The result code of the activity
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode){

            case RC_SIGN_IN:
                /*  Check the result for Google Sign in Activity request    */
                if(resultCode == RESULT_OK) {
                    /*  If result is o.k, start google sign in authentication*/
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    handleGoogleSignInResult(task);
                }
                break;

                default:
                    super.onActivityResult(requestCode, resultCode, data);
        }

    }


    /**
     * Handles the google sign in result. Extracts the credentials from the google sign in
     * and authenticates the user.
     * @param task
     */
    private void handleGoogleSignInResult(Task<GoogleSignInAccount> task) {

        /*  Show Progress bar   */
        showProgress(true);

        try {
            /*  Extract the Google Sign in Account  */
            GoogleSignInAccount account = task.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

            /*  Get the auth credential using the ID token  */
            AuthCredential credential = GoogleAuthProvider
                    .getCredential(account.getIdToken(), null);

            /*  Authenticate the user to the application with the credential obtained */
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithCredential:success");
                                FirebaseUser user = mAuth.getCurrentUser();
//                               TODO : Add sample docs to cloud
                                updateUI(user,null);

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithCredential:failure", task.getException());
//                                Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                                updateUI(null, "Google Sign Authentication failed. Please try again");
                            }

                            // ...
                        }
                    });


        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());

            updateUI(null,"Google Sign in failed. Please check your google account and try again");
        }
    }


    private void startSignIn(FirebaseUser user, int loginType){


    }

    /**
     * Updates the UI based on the login attempt results. Proceeds to login activity if user
     * is successfully logged in or shows an error if the user is not (ie. User is null)
     * @param user The FirebaseUser object which contains the details of the logged in user.
     *             Null if not authenticated.
     */
    private void updateUI(FirebaseUser user, String message){


        if(user==null){
            if(message!=null) {
                showProgress(false);
                Snackbar.make(loginContainer, message, Snackbar.LENGTH_LONG).show();
            }
        }
        else {
//             store his UId in preferences
//           Sync his docs

            String userId = user.getUid();
            String email = user.getEmail();
            String name = user.getDisplayName();

            SharedPreferenceUtils.setPrefEmail(this,email);
            SharedPreferenceUtils.setPrefUserId(this,userId);
            SharedPreferenceUtils.setPrefUsername(this,name);



            DocService.performStartupDocSync(this,userId);

            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            finish();

        }
    }

    private void showRequestInternetDialog(){
        RequestInternetDialogFragment dialogFragment = new RequestInternetDialogFragment();
        dialogFragment.show(getSupportFragmentManager(),REQUEST_INTERNET_DIALOG_TAG);
    }

    /**
     * Called if user accepts to turn on the internet from the
     * request internet dialog.
     * @param dialogInterface The dialog interface of the RequestInternetDialog
     */
    @Override
    public void onUserAcceptsInternetRequest(DialogInterface dialogInterface) {
        /*  Open Wifi settings  */
        startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), 0);
    }

    /**
     * Called if user denies to turn on the internet from the
     * request internet dialog.
     * @param dialogInterface The dialog interface of the RequestInternetDialog
     */
    @Override
    public void onUserDeniesInternetRequest(DialogInterface dialogInterface) {
        /*  Do nothing for now  */
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);

    }




    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }



}

