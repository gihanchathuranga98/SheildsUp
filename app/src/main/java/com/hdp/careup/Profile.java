package com.hdp.careup;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {} subclass.
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
    public static User userDetails;
    Context context;

//    @NonNull
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        // Get the layout inflater
//        LayoutInflater inflater = requireActivity().getLayoutInflater();
//
//        // Inflate and set the layout for the dialog
//        // Pass null as the parent view because its going in the dialog layout
//        builder.setView(inflater.inflate(R.layout.fragment_edit_profile_dialog_content, null))
//                // Add action buttons
//                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int id) {
//                        // sign in the user ...
//                    }
//                })
//                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//
//                    }
//                });
//        return builder.create();
//    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
//                            //Toast.makeText(getContext(), "came to onActivityResult", //Toast.LENGTH_SHORT).show();
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
//                //Toast.makeText(getContext(), "Came to picture btn onClick()", //Toast.LENGTH_LONG);

            }
        });

        view.findViewById(R.id.profile_edit_details_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = new EditProfileDialogContent();
                dialogFragment.show(getParentFragmentManager(), "edit_dialog");

            }
        });


        StorageReference reference = storage.getReference().child("profile_pics/" + preferences.getString("UUID", null));
        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                CircleImageView circleProfile = view.findViewById(R.id.profile_picture);
                Glide.with(context).load(uri).into(circleProfile);
                //Toast.makeText(getContext(), "Profile Pic Updated", //Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Toast.makeText(getContext(), "Profile Picture Loading is Failed", //Toast.LENGTH_LONG).show();
            }
        });

        firestore.collection("users").document(preferences.getString("UUID", null))
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                userDetails = value.toObject(User.class);

                if(!userDetails.getfName().isEmpty() && !userDetails.getlName().isEmpty()){
                    profileName.setText(userDetails.getfName() + " " + userDetails.getlName());
                }else{
                    profileName.setText("N/A");
                }

                if(!userDetails.getEmail().isEmpty()){
                    profileEmail.setText(userDetails.getEmail());
                }else{
                    profileEmail.setText("N/A");
                }

                if(!userDetails.getMobile().isEmpty()){
                    profileMobile.setText(userDetails.getMobile());
                }else{
                    profileMobile.setText("N/A");
                }

                if(!userDetails.getDisplayName().isEmpty()){
                    profileDisplayName.setText(userDetails.getDisplayName());
                }else{
                    profileDisplayName.setText("N/A");
                }
            }
        });

        ((TextView)view.findViewById(R.id.profile_text_pid)).setText("PID : " + getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE).getInt("PID", 0));

    }

    private void addProfilePictureToFirestore(FirebaseStorage storage, Uri imgPath) {
        storage.getReference("profile_pics").child(getActivity()
                        .getSharedPreferences("user_data", Context.MODE_PRIVATE)
                        .getString("UUID", null))
                .putFile(imgPath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        dialog.dismiss();
                        //Toast.makeText(getContext(), "Success", //Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getContext(), ProfileActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        //Toast.makeText(getContext(), "Uploading Faild, Try Again..!", //Toast.LENGTH_LONG).show();
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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
}