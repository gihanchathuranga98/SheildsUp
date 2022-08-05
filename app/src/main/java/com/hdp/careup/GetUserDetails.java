package com.hdp.careup;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {} factory method to
 * create an instance of this fragment.
 */
public class GetUserDetails extends Fragment {

    FirebaseFirestore db;
    SharedPreferences preferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_get_user_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        preferences = getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        db = FirebaseFirestore.getInstance();

        TextInputEditText fName = (TextInputEditText) view.findViewById(R.id.edit_profile_fName_text);
        TextInputEditText lName = (TextInputEditText) view.findViewById(R.id.edit_profile_lName_text);
        TextInputEditText email = (TextInputEditText) view.findViewById(R.id.edit_profile_email_text);

        getActivity().findViewById(R.id.user_data_next_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateForm(fName, lName, email)){
//                    addUserToFirestore();
                    getParentFragmentManager().beginTransaction().replace(R.id.login_fragment_container,
                            new DisplayName(), "GET_DISPLAY_NAME").commit();
                }
            }
        });

        getActivity().findViewById(R.id.edit_profile_fName_text).setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                TextInputLayout layoutFname = (TextInputLayout)getActivity().findViewById(R.id.edit_profile_fName);
                TextInputLayout layoutLname = (TextInputLayout)getActivity().findViewById(R.id.edit_profile_fName);
                TextInputLayout layoutEmail = (TextInputLayout)getActivity().findViewById(R.id.edit_profile_fName);

                if(!fName.getText().toString().trim().isEmpty()){
                    layoutFname.setHelperTextEnabled(false);
                }

                if(!lName.getText().toString().trim().isEmpty()){
                    layoutLname.setHelperTextEnabled(false);
                }

                if(!email.getText().toString().trim().isEmpty()){
                    layoutEmail.setHelperTextEnabled(false);
                }

                return false;
            }
        });

    }

    private boolean validateForm(TextInputEditText fName, TextInputEditText lName, TextInputEditText email) {

        boolean validation = true;

        if(!fName.getText().toString().trim().isEmpty()){
            MainActivity.userDetails.setfName(fName.getText().toString());
        }else{
            TextInputLayout layout = (TextInputLayout)getActivity().findViewById(R.id.edit_profile_fName);
            layout.setHelperTextEnabled(true);
            layout.setHelperText("Required*");
            layout.setHelperTextColor(ColorStateList.valueOf(getResources().getColor(R.color.error)));
            validation = false;
        }

        if(!lName.getText().toString().trim().isEmpty()){
            MainActivity.userDetails.setlName(lName.getText().toString());
        }else{
            TextInputLayout layout = (TextInputLayout)getActivity().findViewById(R.id.edit_profile_lName);
            layout.setHelperTextEnabled(true);
            layout.setHelperText("Required*");
            layout.setHelperTextColor(ColorStateList.valueOf(getResources().getColor(R.color.error)));
            validation = false;
        }

        if(!email.getText().toString().trim().isEmpty() && !email.getText().toString().trim().matches("^(.+)@(.+)$")){
            TextInputLayout layout = (TextInputLayout)getActivity().findViewById(R.id.edit_profile_email);
            layout.setHelperTextEnabled(true);
            layout.setHelperText("Not a valid email");
            layout.setHelperTextColor(ColorStateList.valueOf(getResources().getColor(R.color.error)));
            validation = false;
        }else{
            MainActivity.userDetails.setEmail(email.getText().toString());
        }

//        GetOtp.userDetails.setlName(lName.getText().toString());
//        GetOtp.userDetails.setEmail(email.getText().toString());
//        GetOtp.userDetails.setUserStat(1);

        return validation;
    }
}