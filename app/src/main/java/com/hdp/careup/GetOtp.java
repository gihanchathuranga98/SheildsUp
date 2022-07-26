package com.hdp.careup;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {} factory method to
 * create an instance of this fragment.
 */
public class GetOtp extends Fragment {

    private String mobileNo;
    private FirebaseAuth firebaseAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private SharedPreferences preferences;
    private FirebaseFirestore db;
    private User userDetails;

    public GetOtp() {

    }

    public GetOtp(@Nullable String mobileNo) {
        this();
        this.mobileNo = mobileNo;
        Log.i("OTP", "GetOtp: Mobile No. : " + mobileNo);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_get_otp, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        setting preferences to the users data
        preferences = getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);

//        firebaseAuth setting instance
        firebaseAuth = FirebaseAuth.getInstance();

//        firebase firestore instance setting
        db = FirebaseFirestore.getInstance();

//        User objects to keep the user details
        userDetails = new User();

        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Sending OTP...\n(This may take some time)");
        progressDialog.setCancelable(false);
        progressDialog.show();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                Toast.makeText(getContext(), "verification success", Toast.LENGTH_LONG);
                System.out.println("verification success");
                Log.i("OTP", "Came to Verification Completed");
                updateUI(user);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(getContext(), "verification faild", Toast.LENGTH_LONG);
                System.out.println("verification faild");
                Log.i("OTP", "Came to Verification Faild");
                progressDialog.dismiss();
            }

            @Override
            public void onCodeSent(@NonNull String verificationID, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(verificationID, token);
                progressDialog.dismiss();
                Log.i("OTP", "Came to onCodeSent");
                mVerificationId = verificationID;
            }
        };

//        when the verify OTP btn is clicked this method will be triggered
        view.findViewById(R.id.btn_verify_otp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!((EditText) view.findViewById(R.id.text_otp)).getText().equals("")) {
                    String otp = ((EditText) view.findViewById(R.id.text_otp)).getText().toString();
                    verifyOtp(otp);
                } else {
                    Log.i("OTP", "OTP is Empty");
                }

            }
        });

        sendMobileNo(this.mobileNo);
    }

    private void verifyOtp(String otp) {
        Log.i("OTP", "Verify OTP clicked");
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp);
//        credential.
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            Log.i("OTP", "onComplete: OTP Success");
                            updateUI(user);

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("OTP", "onComplete: OTP Faild");
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        Toast.makeText(getContext(), "Update-UI", Toast.LENGTH_LONG).show();
        String uuid = user.getUid();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("UUID", uuid);
        editor.putString("mobile", mobileNo);
        Log.i("OTP", "User UUID is : " + preferences.getString("UUID", "N/A"));

//        getting data from the firestore to check the available stat of the logged in user.
//        using this i will check the is user's display name is available?

        Task<QuerySnapshot> querySnapshotTask = db.collection("users").get();
        querySnapshotTask.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.i("OTP", "Task Success");
                    QuerySnapshot result = task.getResult();

                    boolean available = false;

                    for (DocumentSnapshot s : task.getResult().getDocuments()) {
                        Log.i("OTP", "DOC path: " + s.getId());

                        if (s.getId().equals(uuid)) {
                            Log.i("OTP", "Equal doc path has been found : " + uuid);
                            available = true;
                            break;
                        }
                    }

                    if (!available) {
//                        TODO -> user is not available

                        Log.i("OTP", "user is not available");

//                                add data to the firestore
                        db.collection("users").document(uuid).set(userDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getContext(), "Firebase Updated", Toast.LENGTH_LONG).show();
                                Log.i("OTP", "UUID onSuccess");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Firebase Update faild", Toast.LENGTH_LONG).show();
                                Log.i("OTP", "UUID onFaliure");
                            }
                        });

                    }else{
//                        TODO -> user is available - get the user details
                        Log.i("OTP", "User is available");
//                        getParentFragmentManager().beginTransaction().replace(R.id.login_fragment_container, new GetUserDetails(), "GET_USER_DETAILS").commit();
                    }
                } else {

                    Log.i("OTP", "Task Faild");
//                            TODO -> show that the task is faild

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }


    private void sendMobileNo(String mobileNo) {
        userDetails.setMobile(mobileNo);
        Log.i("OTP", "sendMobileNo: Mobile No has set");
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(mobileNo)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(getActivity())                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
}