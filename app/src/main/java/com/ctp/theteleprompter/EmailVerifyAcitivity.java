package com.ctp.theteleprompter;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.ctp.theteleprompter.data.SharedPreferenceUtils;
import com.ctp.theteleprompter.fragments.RequestInternetDialogFragment;
import com.ctp.theteleprompter.services.DocService;
import com.ctp.theteleprompter.utils.TeleUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

    @BindView(R.id.next_btn)
    Button nextButton;




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


    @OnClick(R.id.resend_mail_btn)
    protected void onResendEmailButtonClicked(){

        if(!TeleUtils.isConnectedToNetwork(this)){
            RequestInternetDialogFragment fragment = new RequestInternetDialogFragment();
            fragment.show(getSupportFragmentManager(),"request_internet");
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Snackbar.make(findViewById(R.id.email_verify_container),
                                getString(R.string.reset_password_dialog_email_sent),
                                Snackbar.LENGTH_LONG).show();
                    }
                });
    }


    @OnClick(R.id.next_btn)
    protected void onNextButtonClicked(){

        if(!TeleUtils.isConnectedToNetwork(this)){
            RequestInternetDialogFragment fragment = new RequestInternetDialogFragment();
            fragment.show(getSupportFragmentManager(),"request_internet");
            return;
        }

        nextButton.setEnabled(false);


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


    private void initializeUser(FirebaseUser user){

        if(user.isEmailVerified()){
            SharedPreferenceUtils.setPrefUsername(this,user.getDisplayName());
            SharedPreferenceUtils.setPrefUserId(this,user.getUid());
            SharedPreferenceUtils.setPrefEmail(this,user.getEmail());
            DocService.performStartupDocSync(this,user.getUid());
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }else {
            Snackbar.make(findViewById(R.id.email_verify_container),
                    getString(R.string.email_unverified),
                    Snackbar.LENGTH_LONG).show();
            nextButton.setEnabled(true);
        }
    }


    @Override
    public void onUserAcceptsInternetRequest(DialogInterface dialogInterface) {
        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
    }

    @Override
    public void onUserDeniesInternetRequest(DialogInterface dialogInterface) {

    }
}
