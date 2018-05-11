package com.ctp.theteleprompter;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.constraint.Group;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ctp.theteleprompter.data.SharedPreferenceUtils;
import com.ctp.theteleprompter.fragments.RequestInternetDialogFragment;
import com.ctp.theteleprompter.services.DocService;
import com.ctp.theteleprompter.utils.TeleUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EmailVerifyAcitivity extends AppCompatActivity
            implements RequestInternetDialogFragment.RequestInternetDialogFragmentCallbacks{

    public static final String INTENT_EXTRA_EMAIL = "extra-email-key";

    private FirebaseAuth mAuth;


    @BindView(R.id.textView)
    TextView emailMsg;

    @BindView(R.id.open_email_btn)
    Button nextButton;

    @BindView(R.id.display_not_verified)
    Group unverifiedView;

    @BindView(R.id.display_verified)
    Group verifiedView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verify);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        String email = "";

        if(intent.hasExtra(INTENT_EXTRA_EMAIL)){

            email = intent.getStringExtra(INTENT_EXTRA_EMAIL);

        }

        emailMsg.setText(getString(R.string.email_verification_msg,email));


    }


    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser user1 = mAuth.getCurrentUser();

        user1.reload().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                FirebaseUser user1 = mAuth.getCurrentUser();
                if(user1!=null) {
                    initializeUser(user1);
                }

            }
        });
    }

    @OnClick(R.id.resend_mail_btn)
    protected void onResendEmailButtonClicked(){

        if(!TeleUtils.isConnectedToNetwork(this)){
            RequestInternetDialogFragment fragment = new RequestInternetDialogFragment();
            fragment.show(getSupportFragmentManager(),"request_internet");
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();

        ActionCodeSettings actionCodeSettings = TeleUtils.getActionCodeSettingsForUser(user);

        user.sendEmailVerification(actionCodeSettings)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Snackbar.make(findViewById(R.id.email_verify_container),
                                getString(R.string.reset_password_dialog_email_sent),
                                Snackbar.LENGTH_LONG).show();
                    }
                });
    }


    @OnClick(R.id.open_email_btn)
    protected void onOpenEmailButtonClicked(){

//        if(!TeleUtils.isConnectedToNetwork(this)){
//            RequestInternetDialogFragment fragment = new RequestInternetDialogFragment();
//            fragment.show(getSupportFragmentManager(),"request_internet");
//            return;
//        }

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_APP_EMAIL);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

//        nextButton.setEnabled(false);
//
//
//        FirebaseUser user1 = mAuth.getCurrentUser();
//
//        user1.reload().addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//
//                FirebaseUser user1 = mAuth.getCurrentUser();
//                if(user1!=null) {
//                    initializeUser(user1);
//                }
//
//            }
//        });

    }


    private void initializeUser(FirebaseUser user){

        if(user.isEmailVerified()){

            showView(true);

            getSupportActionBar().setDisplayHomeAsUpEnabled(false);

            SharedPreferenceUtils.setPrefUsername(this,user.getDisplayName());
            SharedPreferenceUtils.setPrefUserId(this,user.getUid());
            SharedPreferenceUtils.setPrefEmail(this,user.getEmail());
            DocService.performStartupDocSync(this,user.getUid());

            navigateToMainActivity();


        }else {

            showView(false);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//            Snackbar.make(findViewById(R.id.email_verify_container),
//                    getString(R.string.email_unverified),
//                    Snackbar.LENGTH_LONG).show();
            nextButton.setEnabled(true);
        }
    }

    private void navigateToMainActivity() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(EmailVerifyAcitivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        },2000);

    }


    @Override
    public void onUserAcceptsInternetRequest(DialogInterface dialogInterface) {
        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
    }

    @Override
    public void onUserDeniesInternetRequest(DialogInterface dialogInterface) {

    }


    private void showView(boolean isEmailVerified){
        verifiedView.setVisibility(isEmailVerified ? View.VISIBLE : View.GONE);

        unverifiedView.setVisibility(isEmailVerified? View.GONE: View.VISIBLE);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,SignUpActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }
}
