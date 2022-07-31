package com.hdp.careup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {} factory method to
 * create an instance of this fragment.
 */
public class Profile extends Fragment {

    FirebaseStorage storage;
    FirebaseFirestore firestore;
    SharedPreferences preferences;
    Uri imgPath;
    ProgressDialog dialog;
    FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        preferences.edit().putString("stat", "1").apply();

        storage = FirebaseStorage.getInstance();
        firestore = FirebaseFirestore.getInstance();
        preferences = getContext().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        dialog = new ProgressDialog(getContext());
        user = FirebaseAuth.getInstance().getCurrentUser();

        TextView profileName = (TextView) getActivity().findViewById(R.id.profile_name);
        TextView profileEmail = (TextView) getActivity().findViewById(R.id.profile_email);
        TextView profileMobile = (TextView) getActivity().findViewById(R.id.profile_mobileNo);
        TextView profileDisplayName = (TextView) getActivity().findViewById(R.id.profile_displayName);

        ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        Log.i("IMG", "onActivityResult: came to launcher");
                        if(result.getResultCode() == Activity.RESULT_OK && result.getData() != null){
                            Uri data = result.getData().getData();
                            imgPath = data;
                            addProfilePictureToFirestore(storage, imgPath);
//                            Toast.makeText(getContext(), "came to onActivityResult", Toast.LENGTH_SHORT).show();
                        }else{
                            Log.i("IMG", "onActivityResult: getData is null");
                        }
                    }
                });

        getActivity().findViewById(R.id.profile_edit_picture_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                launcher.launch(intent);
//                Toast.makeText(getContext(), "Came to picture btn onClick()", Toast.LENGTH_LONG);

            }
        });


        StorageReference reference = storage.getReference().child("profile_pics/" + preferences.getString("UUID", null));
        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(Profile.this).load(uri).into((CircleImageView)view.findViewById(R.id.profile_picture));
                Toast.makeText(getContext(), "Profile Pic Updated", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        firestore.collection("users").document(preferences.getString("UUID", null))
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                User user = value.toObject(User.class);

                if(!user.getfName().isEmpty() && !user.getlName().isEmpty()){
                    profileName.setText(user.getfName() + " " + user.getlName());
                }else{
                    profileName.setText("N/A");
                }

                if(!user.getEmail().isEmpty()){
                    profileEmail.setText(user.getEmail());
                }else{
                    profileEmail.setText("N/A");
                }

                if(!user.getMobile().isEmpty()){
                    profileMobile.setText(user.getMobile());
                }else{
                    profileMobile.setText("N/A");
                }

                if(!user.getDisplayName().isEmpty()){
                    profileDisplayName.setText(user.getDisplayName());
                }else{
                    profileDisplayName.setText("N/A");
                }
            }
        });

    }

    private void addProfilePictureToFirestore(FirebaseStorage storage, Uri imgPath) {
        storage.getReference("profile_pics").child(getActivity()
                        .getSharedPreferences("user_data", Context.MODE_PRIVATE)
                        .getString("UUID", null))
                .putFile(imgPath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        dialog.dismiss();
                        Toast.makeText(getContext(), "Success", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getContext(), ProfileActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Uploading Faild", Toast.LENGTH_LONG).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        dialog.setMessage("Uploading Profile Picutre...");
                        dialog.setCancelable(false);
                        dialog.show();
                    }
                });
    }
}