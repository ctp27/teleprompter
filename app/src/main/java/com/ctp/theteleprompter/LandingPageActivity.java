package com.ctp.theteleprompter;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindBool;
import butterknife.ButterKnife;

public class LandingPageActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;


    @BindBool(R.bool.isTabletPort)
    boolean isTabletPort;


    @BindBool(R.bool.isTabletLand)
    boolean isTabletLand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();


    }


    @Override
    protected void onResume() {
        super.onResume();
       FirebaseUser user =  mAuth.getCurrentUser();
       initialize(user);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void initialize(FirebaseUser user){

        if(user == null){
            new SendToSignUpTask().execute();

        }else {
            if(user.isEmailVerified()){
                redirectToMainActivity();
            }
            else {
                redirectToEmailVerify(user);
            }
        }


   }

    private void redirectToEmailVerify(FirebaseUser user) {


        Intent intentB = new Intent(this,EmailVerifyAcitivity.class);
        intentB.putExtra(EmailVerifyAcitivity.INTENT_EXTRA_EMAIL,user.getEmail());
        startActivity(intentB);


    }

    private void redirectToMainActivity(){
       Intent intent = new Intent(this,MainActivity.class);
       startActivity(intent);
   }


   private class SendToSignUpTask extends AsyncTask<Void,Void,Void>{

       @Override
       protected void onPostExecute(Void aVoid) {
           Intent intent = new Intent(LandingPageActivity.this,LoginActivity.class);
           startActivity(intent);
       }

       @Override
       protected Void doInBackground(Void... voids) {

           try {
               Thread.sleep(1000);
           } catch (InterruptedException e) {
               e.printStackTrace();
           }

           return null;
       }
   }


}
