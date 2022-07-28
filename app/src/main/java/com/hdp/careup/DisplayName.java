package com.hdp.careup;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {} factory method to
 * create an instance of this fragment.
 */
public class DisplayName extends Fragment {

    FirebaseFirestore db;
    SharedPreferences preferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_display_name, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        preferences = getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        TextInputLayout layout = (TextInputLayout) getActivity().findViewById(R.id.displayName);
        TextInputEditText displayName = (TextInputEditText) getActivity().findViewById(R.id.displayName_text);
        Button submitBtn = (Button) getActivity().findViewById(R.id.displayName_submit_btn);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFieldValid(displayName, layout)){
                    addUserToFirestore();
                }
            }
        });

    }

    private boolean isFieldValid(TextInputEditText displayName, TextInputLayout layout) {
        if(!displayName.getText().toString().trim().isEmpty()){
            GetOtp.userDetails.setDisplayName(displayName.getText().toString().trim());
            return true;
        }else{
            layout.setHelperTextEnabled(true);
            layout.setHelperText("Required*");
            layout.setHelperTextColor(ColorStateList.valueOf(getResources().getColor(R.color.error)));
        }
        return false;
    }

    private void addUserToFirestore() {

        GetOtp.userDetails.setUserStat(1);
        db.collection("users").document(preferences.getString("UUID", null)).set(GetOtp.userDetails)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.i("OTP", "onSuccess: User details have added to the firestore");
                        Intent intent = new Intent(getContext(), ProfileActivity.class);
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("OTP", "onFailure: User details haven't added to the firestore");
                        Toast.makeText(getContext(), "Operation Faild", Toast.LENGTH_LONG);
                    }
                });
    }

}

