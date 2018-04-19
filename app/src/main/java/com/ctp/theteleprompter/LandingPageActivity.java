package com.ctp.theteleprompter;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LandingPageActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

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
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        }


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
               Thread.sleep(5000);
           } catch (InterruptedException e) {
               e.printStackTrace();
           }

           return null;
       }
   }
}
