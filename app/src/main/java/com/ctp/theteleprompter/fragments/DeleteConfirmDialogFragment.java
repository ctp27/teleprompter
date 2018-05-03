package com.ctp.theteleprompter.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.ctp.theteleprompter.MainActivity;
import com.ctp.theteleprompter.R;

public class DeleteConfirmDialogFragment extends DialogFragment {

    public static final String EXTRA_DOC_NAME = "extra_doc_name";

    public interface DeleteConfirmDialogCallbacks{
        void onConfirmDeleteDocument(DialogInterface dialogInterface);
        void onCancelDeleteDocument(DialogInterface dialogInterface);
    }


    private DeleteConfirmDialogCallbacks mCallback;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        Bundle b = getArguments();
        String docName = "";

        if(b!=null) {
            docName = b.getString(EXTRA_DOC_NAME);
        }




        builder.setMessage(getString(R.string.delete_confm_msg,docName))
                .setTitle(getString(R.string.delete_confm_title))
                .setPositiveButton(R.string.request_internet_dialog_positive, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                        mCallback.onConfirmDeleteDocument(dialog);
                    }
                })
                .setNegativeButton(R.string.request_internet_dialog_negative, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        mCallback.onCancelDeleteDocument(dialog);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);


        if (context instanceof MainActivity){
            MainActivity activity=(MainActivity) context;
            try {
                mCallback = activity;
            }catch (ClassCastException c){
                throw new ClassCastException(c.toString()
                        + " must implement DeleteDialogCallbacks");
            }

        }
    }
}
