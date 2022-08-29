package com.hdp.careup;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.GetSignInIntentRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.zip.Inflater;

/**
 * A simple {@link Fragment} subclass.
 * Use the {} factory method to
 * create an instance of this fragment.
 */
public class Login extends Fragment {

    private FirebaseAuth firebaseAuth;
    FirebaseStorage storage;
    SharedPreferences preferences;
    SignInClient signInClient;
    Context context;
    FirebaseFirestore firestore;

    private final ActivityResultLauncher<IntentSenderRequest> signinLauncher = registerForActivityResult(
            new ActivityResultContracts.StartIntentSenderForResult(), new ActivityResultCallback<ActivityResult>(){

                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.i("my", "onActivityResult: Came to onActivityResult()");
                    System.out.println("----------------------------------------------------- came to onActivityResult()");
//                    Toast.makeText(getApplicationContext(), "here we go", Toast.LENGTH_LONG).show();
                    handleSigninResult(result.getData());
                }
            }
    );

    private void handleSigninResult(Intent intent) {
        Log.i("my", "handleSigninResult: handleSigninResult()");
        try {
            SignInCredential credential = signInClient.getSignInCredentialFromIntent(intent);
            String idToken = credential.getGoogleIdToken();
            firebaseAuthWithGoogle(idToken);

        }catch (ApiException e){
            System.err.println("error in handleSigninResult()\n"+e);
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        Log.i("my", "firebaseAuthWithGoogle: firebaseAuthWithGoogle()");
        AuthCredential authCredential = GoogleAuthProvider.getCredential(idToken, null);
        Task<AuthResult> authResultTask = firebaseAuth.signInWithCredential(authCredential);
        authResultTask.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = firebaseAuth.getCurrentUser();
//                    Toast.makeText(getContext(), user.getEmail(), Toast.LENGTH_LONG);
                    Log.e("TAG", "onComplete: came to success in login with google --");
                    updateUI(user);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Login Failed..@", Toast.LENGTH_LONG).show();
                Log.e("TAG", "onFailure: login with google failed ---> " + e);
            }
        });
    }

    private void updateUI(FirebaseUser user) {
        Log.i("USER", "updateUI: name : " + user.getDisplayName());
        Log.i("USER", "updateUI: email : " + user.getEmail());
        Log.i("USER", "updateUI: photo url : " + user.getPhotoUrl());
        Log.i("USER", "updateUI: phone : " + user.getPhoneNumber());

        preferences.edit().putString("UUID", user.getUid()).apply();
        Uri profileUri = user.getPhotoUrl();
        MainActivity.userDetails.setDisplayName(user.getDisplayName());
        MainActivity.userDetails.setEmail(user.getEmail());
        MainActivity.userDetails.setfName(user.getDisplayName());

        if(user.getPhoneNumber() != null){
            MainActivity.userDetails.setMobile(user.getPhoneNumber());
        }else{
            MainActivity.userDetails.setMobile("");
        }

        Task<QuerySnapshot> querySnapshotTask = firestore.collection("users").get();
        querySnapshotTask.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful()){
                    Log.e("TAG", "onComplete: came to check document in the firestore");
                    boolean available = false;

                    for(DocumentSnapshot snap : task.getResult().getDocuments()){

                        if(snap.getId().equals(user.getUid())){
                            available = true;
                            break;
                        }
                    }

                    if(!available){
                        Log.e("TAG", "Came to not available at Login.java ---> document is not available");
                        getParentFragmentManager()
                                .beginTransaction()
                                .replace(R.id.login_fragment_container, new GetUserDetails(), "LOADING_GET_USER_DETAILS")
                                .commit();
                    }else{
                        Log.e("TAG", "came to document available area in Login.java");
                        Intent intent = new Intent(context, ProfileActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        firestore = FirebaseFirestore.getInstance();
        preferences = getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);

//        Click on ** Create New Account **
        view.findViewById(R.id.newAccountLink).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("came to newAccountLink btn click");
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.login_fragment_container, new Register(), "REGISTER1");
                transaction.commit();
            }
        });


//        Click on ** login with mobile ***
        view.findViewById(R.id.login_withMobile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.login_fragment_container, new MobileLogin(), "MOBILE_LOGIN");
                transaction.commit();
            }
        });
//        testing push

//        click on ** forget password **
        view.findViewById(R.id.login_forgotPassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.login_fragment_container, new RecoverPassword(), "RECOVER_PASSWORD");
                transaction.commit();
            }
        });

        view.findViewById(R.id.login_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });

        view.findViewById(R.id.login_withGoogle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signin();
            }
        });

        EditText username = view.findViewById(R.id.text_username);
        EditText pwd = view.findViewById(R.id.text_password);

        view.findViewById(R.id.login_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!username.getText().toString().isEmpty() && !pwd.getText().toString().isEmpty()){
                    firebaseAuth.signInWithEmailAndPassword(username.getText().toString(), pwd.getText().toString())
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = firebaseAuth.getCurrentUser();
                                        updateUIEmail(user);
                                    } else {
                                        username.setText("");
                                        pwd.setText("");
                                        Toast.makeText(getContext(), "Invalid Credentials", Toast.LENGTH_LONG).show();
                                    }
                                }

                                private void updateUIEmail(FirebaseUser user) {
//                                    TODO -> updateUIEmail()

                                    preferences.edit().putString("UUID", user.getUid()).apply();
                                    Uri profileUri = user.getPhotoUrl();
                                    MainActivity.userDetails.setDisplayName(user.getDisplayName());
                                    MainActivity.userDetails.setEmail(user.getEmail());
                                    MainActivity.userDetails.setfName(user.getDisplayName());

                                    if(user.getPhoneNumber() != null){
                                        MainActivity.userDetails.setMobile(user.getPhoneNumber());
                                    }else{
                                        MainActivity.userDetails.setMobile("");
                                    }

                                    Task<QuerySnapshot> querySnapshotTask = firestore.collection("users").get();
                                    querySnapshotTask.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                            if(task.isSuccessful()){
                                                boolean available = false;

                                                for(DocumentSnapshot snap : task.getResult().getDocuments()){

                                                    if(snap.getId().equals(user.getUid())){
                                                        if(snap.getString("displayName") != null){
                                                            available = true;
                                                        }
                                                        break;
                                                    }
                                                }

                                                if(!available){
                                                    getActivity().getSupportFragmentManager()
                                                            .beginTransaction()
                                                            .replace(R.id.login_fragment_container, new DisplayName(), "")
                                                            .commit();
                                                }else{
                                                    Intent intent = new Intent(getActivity(), ProfileActivity.class);
                                                    startActivity(intent);
                                                }
                                            }
                                        }
                                    });

                                }
                            });
                }else{
                    Toast.makeText(getContext(), "Fill all of the fields", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        if(getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE).getString("stat", "0").equals("1")){
            Intent intent = new Intent(getContext(), ProfileActivity.class);
            startActivity(intent);
        }
    }

    private void signin(){
        GetSignInIntentRequest build = GetSignInIntentRequest.builder().setServerClientId(getString(R.string.web_client_id)).build();
        Task<PendingIntent> signInIntent = signInClient.getSignInIntent(build);

        signInIntent.addOnSuccessListener(new OnSuccessListener<PendingIntent>() {
            @Override
            public void onSuccess(PendingIntent pendingIntent) {
                loanchSignin(pendingIntent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Failed", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void loanchSignin(PendingIntent pendingIntent) {
        IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(pendingIntent).build();
        signinLauncher.launch(intentSenderRequest);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        signInClient = Identity.getSignInClient(getContext());
        this.context = context;
    }
}