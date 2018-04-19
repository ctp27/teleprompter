package com.ctp.theteleprompter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ctp.theteleprompter.data.SharedPreferenceUtils;
import com.ctp.theteleprompter.utils.TeleUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUpActivity extends AppCompatActivity
                implements View.OnClickListener{

    @BindView(R.id.email)
    EditText emailInput;

    @BindView(R.id.password)
    EditText passwordInput;

    @BindView(R.id.confirm_password)
    EditText confirmPasswordInput;

    @BindView(R.id.full_name)
    EditText fullNameInput;

    @BindView(R.id.sign_up_button)
    Button signUpButton;

    @BindView(R.id.sign_up_container)
    LinearLayout layoutContainer;



    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        signUpButton.setOnClickListener(this);

    }



    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id){
            case R.id.sign_up_button:
                initiateSignUp();
                break;
        }
    }


    private void initiateSignUp(){


        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();

        if(!TeleUtils.isConnectedToNetwork(this)){
            Snackbar.make(layoutContainer,
                    "Check your internet connection"
                    ,Snackbar.LENGTH_LONG).show();

            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });


    }


    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 6;
    }


    private void updateUI(FirebaseUser currentUser){

        if(currentUser == null){
//            TODO: some error occured
            return;

        }
        else {
//            TODO: store id in shared preferences
            SharedPreferenceUtils.setPrefUserId(this,currentUser.getUid());
            SharedPreferenceUtils.setPrefEmail(this,currentUser.getEmail());
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }



    }
}
