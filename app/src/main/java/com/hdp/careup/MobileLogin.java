package com.hdp.careup;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.Selection;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {} factory method to
 * create an instance of this fragment.
 */
public class MobileLogin extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mobile_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);




//        set data to country code spinner
        Spinner countryCode = (Spinner) getActivity().findViewById(R.id.withMobile_countryCode);

//        Array of country codes
        List list = new ArrayList();
        list.add("+94");
        list.add("+1");

//        setting data to an array adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, list);
        countryCode.setAdapter(adapter);
//        getting the mobile number typed in the text_mobileNo
        EditText mobile = view.findViewById(R.id.text_mobileNo);

//        Spinner spinner = ((Spinner) view.findViewById(R.id.withMobile_countryCode));

//        setOnclick listner to the send OTP button
        view.findViewById(R.id.withMobile_sendOTP).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                System.out.println("came to onclick -> "+mobile.getText()+" value -> "+mobile.getText().toString().equals(""));
                if(!mobile.getText().toString().trim().equals("")){
                    String mobileNo;
//                    removing first 0 from the mobile number
                    char[] numbs = mobile.getText().toString().trim().toCharArray();
                    if(numbs[0] == '0'){
                        char[] newMobile = Arrays.copyOfRange(numbs, 1, numbs.length);
                        mobileNo =  Arrays.toString(newMobile);
                        mobileNo = mobileNo.replace("[", "").replace("]", "")
                                        .replace(",", "").replace(" ", "");
                    }else{
                        mobileNo = mobile.getText().toString();
                    }

                    FragmentManager fragmentManager = getParentFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.login_fragment_container, new GetOtp(countryCode.getSelectedItem().toString()+mobileNo), "GET_OTP");
                    transaction.commit();
                }else{
                    Toast.makeText(getContext(), "Enter Phone Number", Toast.LENGTH_LONG).show();
                }

            }
        });

//        setOnCLickListner to back button
        view.findViewById(R.id.withMobile_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.login_fragment_container, new Login(), "BACK_TO_LOGIN");
                transaction.commit();
            }
        });

    }


}