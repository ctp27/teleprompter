package com.ctp.theteleprompter.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ctp.theteleprompter.LoginActivity;
import com.ctp.theteleprompter.R;
import com.ctp.theteleprompter.utils.TeleUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ForgotPasswordDialogFragment extends DialogFragment {


    private ForgotPasswordDialogCallbacks mCallback;

    public interface ForgotPasswordDialogCallbacks{
        void onSendForgotPassEmailClicked(DialogInterface dialogInterface, String email);
    }


    @BindView(R.id.forgotpass_dialog_edit)
    EditText forgottenEmail;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.password_reset_dialog,null);
        ButterKnife.bind(this,v);

        builder.setView(v)
                // Add action buttons
                .setTitle(getString(R.string.forgot_pass_string))
                .setPositiveButton(R.string.send_email, null)
                .setNegativeButton(R.string.cancel, null);



        final AlertDialog dialog =  builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = ((AlertDialog) dialog).getButton(Dialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String email = forgottenEmail.getText().toString();

                        if(TeleUtils.isValidEmail(email)){
                            mCallback.onSendForgotPassEmailClicked(dialog,email);
                            dialog.dismiss();
                        }
                        else {
                            forgottenEmail.setError(getString(R.string.error_invalid_email));
                            forgottenEmail.requestFocus();
                        }
                    }
                });
            }
        });

        return dialog;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        LoginActivity activity=null;

        if (context instanceof LoginActivity){
            activity=(LoginActivity) context;
        }

        try {
            mCallback = activity;
        }catch (ClassCastException c){
            throw new ClassCastException(c.toString()
                    + " must implement ForgotPasswordDialogCallbacks");
        }

    }
}
