package com.ctp.theteleprompter.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.ctp.theteleprompter.EmailVerifyAcitivity;
import com.ctp.theteleprompter.LoginActivity;
import com.ctp.theteleprompter.R;
import com.ctp.theteleprompter.SignUpActivity;

public class RequestInternetDialogFragment extends DialogFragment {


    public interface RequestInternetDialogFragmentCallbacks{

        void onUserAcceptsInternetRequest(DialogInterface dialogInterface);

        void onUserDeniesInternetRequest(DialogInterface dialogInterface);
    }


    private RequestInternetDialogFragmentCallbacks mCallback;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(getString(R.string.request_internet_dialog_text))
                .setTitle(getString(R.string.request_internet_dialog_title))
                .setPositiveButton(R.string.request_internet_dialog_positive, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                        mCallback.onUserAcceptsInternetRequest(dialog);
                    }
                })
                .setNegativeButton(R.string.request_internet_dialog_negative, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        mCallback.onUserDeniesInternetRequest(dialog);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);


        if (context instanceof LoginActivity){
           LoginActivity activity=(LoginActivity) context;
            try {
                mCallback = activity;
            }catch (ClassCastException c){
                throw new ClassCastException(c.toString()
                        + " must implement RequestInternetDialogCallbacks");
            }
        }else if(context instanceof SignUpActivity){
            SignUpActivity activity = (SignUpActivity) context;
            try {
                mCallback = activity;
            }catch (ClassCastException c){
                throw new ClassCastException(c.toString()
                        + " must implement RequestInternetDialogCallbacks");
            }
        }
        else if(context instanceof EmailVerifyAcitivity){
            EmailVerifyAcitivity acitivity = (EmailVerifyAcitivity) context;

            try {
                mCallback = acitivity;
            }
            catch (ClassCastException d){
                throw new ClassCastException(d.toString() + "must implement RequestInternetDialogCallback");
            }
        }


    }


}
