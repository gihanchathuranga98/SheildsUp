package com.hdp.careup;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

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

        TextInputEditText fName = (TextInputEditText) view.findViewById(R.id.user_data_fname_text);
        TextInputEditText lName = (TextInputEditText) view.findViewById(R.id.user_data_lname_text);
        TextInputEditText email = (TextInputEditText) view.findViewById(R.id.user_data_email_text);

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

        getActivity().findViewById(R.id.user_data_fname_text).setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                TextInputLayout layoutFname = (TextInputLayout)getActivity().findViewById(R.id.user_data_fname);
                TextInputLayout layoutLname = (TextInputLayout)getActivity().findViewById(R.id.user_data_fname);
                TextInputLayout layoutEmail = (TextInputLayout)getActivity().findViewById(R.id.user_data_fname);

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
            GetOtp.userDetails.setfName(fName.getText().toString());
        }else{
            TextInputLayout layout = (TextInputLayout)getActivity().findViewById(R.id.user_data_fname);
            layout.setHelperTextEnabled(true);
            layout.setHelperText("Required*");
            layout.setHelperTextColor(ColorStateList.valueOf(getResources().getColor(R.color.error)));
            validation = false;
        }

        if(!lName.getText().toString().trim().isEmpty()){
            GetOtp.userDetails.setlName(lName.getText().toString());
        }else{
            TextInputLayout layout = (TextInputLayout)getActivity().findViewById(R.id.user_data_lname);
            layout.setHelperTextEnabled(true);
            layout.setHelperText("Required*");
            layout.setHelperTextColor(ColorStateList.valueOf(getResources().getColor(R.color.error)));
            validation = false;
        }

        if(!email.getText().toString().trim().isEmpty() && !email.getText().toString().trim().matches("^(.+)@(.+)$")){
            TextInputLayout layout = (TextInputLayout)getActivity().findViewById(R.id.user_data_email);
            layout.setHelperTextEnabled(true);
            layout.setHelperText("Not a valid email");
            layout.setHelperTextColor(ColorStateList.valueOf(getResources().getColor(R.color.error)));
            validation = false;
        }else{
            GetOtp.userDetails.setEmail(fName.getText().toString());
        }

//        GetOtp.userDetails.setlName(lName.getText().toString());
//        GetOtp.userDetails.setEmail(email.getText().toString());
//        GetOtp.userDetails.setUserStat(1);

        return validation;
    }
}