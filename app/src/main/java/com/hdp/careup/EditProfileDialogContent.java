package com.hdp.careup;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textfield.TextInputEditText;

/**
 * A simple {@link Fragment} subclass.
 * Use the {} factory method to
 * create an instance of this fragment.
 */
public class EditProfileDialogContent extends DialogFragment {

    TextInputEditText displayNmae;
    TextInputEditText fNmae;
    TextInputEditText lNmae;
    String fnmae, lname, displayName;
    User user;
    private View view;

    public EditProfileDialogContent(){

    }

    public EditProfileDialogContent(String fname, String lname, String displayName){
        this();
        this.fnmae = fname;
        this.lname = lname;
        this.displayName = displayName;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_edit_profile_dialog_content, container, false);
        return null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i("DIALOG", "onViewCreated: came to onViewCreated");
        this.displayNmae = view.findViewById(R.id.edit_profile_display_name_text);
        this.fNmae = view.findViewById(R.id.edit_profile_fName_text);
        this.lNmae = view.findViewById(R.id.edit_profile_lName_text);

        displayNmae.setText("displayName");
        fNmae.setText("fnmae");
        lNmae.setText("lname");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        onViewCreated(getView(), null);

        this.user = Profile.userDetails;

        System.out.println("Here is user's first name " + user.getfName());

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.fragment_edit_profile_dialog_content, null))
                // Add action buttons
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        return builder.create();
    }

}