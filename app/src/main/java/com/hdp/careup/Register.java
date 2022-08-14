package com.hdp.careup;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {} subclass.
 * Use the {} factory method to
 * create an instance of this fragment.
 */
public class Register extends Fragment {

    FirebaseAuth mAuth;
    FirebaseUser user;
    TextInputEditText fName, lName, mobile, email, pwd, confirmPwd;
    TextInputLayout fNameLabel, lNameLabel, mobileLabel, emailLabel, pwdLabel, confirmPwdLabel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        fName = view.findViewById(R.id.register_firstName_text);
        lName = view.findViewById(R.id.register_lastName_text);
        email = view.findViewById(R.id.register_email_text);
        mobile = view.findViewById(R.id.register_mobile_text);
        pwd = view.findViewById(R.id.register_pwd_text);
        confirmPwd = view.findViewById(R.id.register_cnfrmPwd_text);

        fNameLabel = view.findViewById(R.id.register_firstName);
        lNameLabel = view.findViewById(R.id.register_lastName);
        emailLabel = view.findViewById(R.id.register_email);
        mobileLabel = view.findViewById(R.id.register_mobile);
        pwdLabel = view.findViewById(R.id.register_pwd);
        confirmPwdLabel = view.findViewById(R.id.register_cnfrmPwd);


        view.findViewById(R.id.register_backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.login_fragment_container, new Login(), "BACK_TO_LOGIN");
                transaction.commit();
            }
        });

        view.findViewById(R.id.register_submit_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isValide()) {
                    mAuth.createUserWithEmailAndPassword(email.getText().toString(), pwd.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {
                                        getActivity().getSupportFragmentManager()
                                                .beginTransaction().replace(R.id.login_fragment_container, new Login(), "Register ot login")
                                                .commit();
                                    } else {
                                        Toast.makeText(getContext(), "Please Try Again..!", Toast.LENGTH_LONG).show();
                                    }

                                }
                            });
                }
            }
        });

//        view.findViewById(R.id.register_firstName_text).setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//
//                System.out.println("Came to key event");
//
//                if(!fName.getText().toString().isEmpty()){
//                    fNameLabel.setErrorEnabled(false);
//                }
//
//                if(!lName.getText().toString().isEmpty()){
//                    lNameLabel.setErrorEnabled(false);
//                }
//
//                if(!email.getText().toString().isEmpty()){
//                    emailLabel.setErrorEnabled(false);
//                }
//
//                return false;
//            }
//        });

    }

    @SuppressLint("ResourceAsColor")
    private boolean isValide() {
        System.out.println("Came in to check valid...");

        boolean valid = false;

        if (!fName.getText().toString().equals("")) {
            fNameLabel.setErrorEnabled(false);
            valid = true;
        } else {
            fNameLabel.setError("First Name is Required*");
            fNameLabel.setErrorEnabled(true);
        }

        if (!lName.getText().toString().equals("")) {
            lNameLabel.setErrorEnabled(false);
        } else {
            lNameLabel.setError("Last Name is Required*");
            lNameLabel.setErrorEnabled(true);
        }

        if (!email.getText().toString().equals("")) {
            emailLabel.setErrorEnabled(false);
        } else {
            emailLabel.setError("Email is Required*");
            emailLabel.setErrorEnabled(true);
        }

        if (!pwd.getText().toString().equals("")) {
            pwdLabel.setErrorEnabled(false);
        } else {
            pwdLabel.setError("Password is Required*");
            pwdLabel.setErrorEnabled(true);
        }

        if (!confirmPwd.getText().toString().equals("")) {
            confirmPwdLabel.setErrorEnabled(false);
        } else {
            confirmPwdLabel.setError("Password is Required*");
            confirmPwdLabel.setErrorEnabled(true);
        }

        if (!fName.getText().toString().equals("")
                && !lName.getText().toString().equals("")
                && !email.getText().toString().equals("")
                && !pwd.getText().toString().equals("")
                && !confirmPwd.getText().toString().equals("")) {
            if (email.getText().toString().matches("[a-z0-9]+@[a-z]+\\.[a-z]{2,3}")) {
                if (pwd.getText().toString().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$")) {  // TODO -> validate the password properly
                    if (confirmPwd.getText().toString().equals(pwd.getText().toString())) {
                        valid = true;
                    } else {
                        confirmPwdLabel.setError("Passwords are not matching");
                        confirmPwdLabel.setErrorEnabled(true);
                    }
                } else {
                    pwdLabel.setError("Password is not strong enough");
                    pwdLabel.setErrorEnabled(true);
                    valid = false;
                }

            } else {
                emailLabel.setError("Email is not valid");
                emailLabel.setErrorEnabled(true);
                valid = false;
            }
        } else {
            valid = false;
        }


        return valid;
    }
}