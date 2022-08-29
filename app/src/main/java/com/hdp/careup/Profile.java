package com.hdp.careup;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.hdp.careup.services.ChildLocationService;

import java.util.HashMap;
import java.util.Map;

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
    private User userDetails2;
    Context context;
    public static String userID;

    TextView profileName, profileEmail, profileMobile, profileDisplayName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e("TAG", "onViewCreated:came to onViewCreated()");
        if(!isLocationPermissionGranted()){
//            map.setMyLocationEnabled(true);

            Log.e("TAG", "onViewCreated:came to isLocationPermissionGranted()");

        }else{
            Log.e("TAG", "onViewCreated:came to requestPermissions()");
            requestPermissions(
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                            Manifest.permission.ACCESS_MEDIA_LOCATION,
                            Manifest.permission.FOREGROUND_SERVICE,
                            Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                    },
                    1
            );
        }


        storage = FirebaseStorage.getInstance();
        firestore = FirebaseFirestore.getInstance();
        preferences = getContext().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        dialog = new ProgressDialog(getContext());
        user = FirebaseAuth.getInstance().getCurrentUser();


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
//                Toast.makeText(getContext(), "Came to picture btn onClick()", //Toast.LENGTH_LONG);

            }
        });

        view.findViewById(R.id.profile_edit_details_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                DialogFragment dialogFragment = new EditProfileDialogContent("Gihan", "Chathuranga", "Attanayake");
//                dialogFragment.show(getParentFragmentManager(), "edit_dialog");
                Dialog editDialog = new Dialog(getActivity());
                LayoutInflater inflater = requireActivity().getLayoutInflater();
                editDialog.setContentView(inflater.inflate(R.layout.fragment_edit_profile_dialog_content, null));
                editDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

//                TODO-> add existing details to popup window
                TextView displayName = view.findViewById(R.id.profile_displayName);

                ((EditText)editDialog.findViewById(R.id.edit_profile_display_name_text)).setText(displayName.getText());
                ((EditText)editDialog.findViewById(R.id.edit_profile_fName_text)).setText(getName(0));
                ((EditText)editDialog.findViewById(R.id.edit_profile_lName_text)).setText(getName(1));
                editDialog.show();

                editDialog.findViewById(R.id.dialog_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editDialog.dismiss();
                    }
                });

                editDialog.findViewById(R.id.dialog_save).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String displayName = ((EditText) editDialog.findViewById(R.id.edit_profile_display_name_text)).getText().toString();
                        String fName = ((EditText) editDialog.findViewById(R.id.edit_profile_fName_text)).getText().toString();
                        String lName = ((EditText) editDialog.findViewById(R.id.edit_profile_lName_text)).getText().toString();
                        saveProfileData(displayName, fName, lName, editDialog);

                    }
                });

            }
        });


        StorageReference reference = storage.getReference().child("profile_pics/" + preferences.getString("UUID", null));
//        TODO -> reference for the profile picture
        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                CircleImageView circleProfile = view.findViewById(R.id.profile_picture);
                if(uri.equals("") || uri == null){

                }else{
                    Glide.with(context).load(uri).into(circleProfile);
                }
                //Toast.makeText(getContext(), "Profile Pic Updated", //Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Toast.makeText(getContext(), "Profile Picture Loading is Failed", //Toast.LENGTH_LONG).show();
            }
        });

        userID = context.getSharedPreferences("user_data", Context.MODE_PRIVATE).getString("UUID", "");

        firestore.collection("users").document(userID)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                System.out.println("event Updated in profile fragment...!");
                userDetails2 = value.toObject(User.class);

                profileName = view.findViewById(R.id.profile_name);
                profileEmail = view.findViewById(R.id.profile_email);
                profileMobile = view.findViewById(R.id.profile_mobileNo);
                profileDisplayName = view.findViewById(R.id.profile_displayName);
                TextView pid = view.findViewById(R.id.profile_text_pid);

                if(!userDetails2.getfName().isEmpty() && !userDetails2.getlName().isEmpty()){
                    profileName.setText(userDetails2.getfName() + " " + userDetails2.getlName());
                }else{
                    profileName.setText("N/A");
                }

                if(!userDetails2.getEmail().isEmpty()){
                    profileEmail.setText(userDetails2.getEmail());
                }else{
                    profileEmail.setText("N/A");
                }

                if(!userDetails2.getMobile().isEmpty()){
                    profileMobile.setText(userDetails2.getMobile());
                }else{
                    profileMobile.setText("N/A");
                }

                if(!userDetails2.getDisplayName().isEmpty()){
                    profileDisplayName.setText(userDetails2.getDisplayName());
                }else{
                    profileDisplayName.setText("N/A");
                }

                if(userDetails2.getPairID() != 0){
                    pid.setText("PID : " + userDetails2.getPairID());
                }else{
                    profileDisplayName.setText("0");
                }
            }
        });

        ((TextView)view.findViewById(R.id.profile_text_pid)).setText("PID : " + getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE).getInt("PID", 0));

        firestore.collection("users").document(preferences.getString("UUID", null)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String role = task.getResult().getString("role");
                System.out.println("this is the role ---------> " + role);
                if(role.equals("CHILD")){
                    Intent intent = new Intent( context, ChildLocationService.class);
//                    intent.putExtra("uid", context.getSharedPreferences("user_data", Context.MODE_PRIVATE).getString("UUID", ""));
                    if(!foregroundServiceRunning()){
                        context.startForegroundService(intent);
                    }
                }
            }
        });
    }

    private void saveProfileData(String displayName, String fName, String lName, Dialog dialog) {
        Map map = new HashMap();
        map.put("fName", fName);
        map.put("lName", lName);
        map.put("displayName", displayName);
        firestore.collection("users").document(getContext().getSharedPreferences("user_data", Context.MODE_PRIVATE).getString("UUID", "")).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                dialog.dismiss();
            }
        });
    }

    private String getName(int i) {
        TextView name = getView().findViewById(R.id.profile_name);
        String[] names = name.getText().toString().trim().split(" ");
        if(i == 0){
            if(names.length == 2){
                return names[0];
            }else{
                return name.getText().toString();
            }
        }else{
            if(names.length == 2){
                return names[1];
            }else{
                return null;
            }
        }
    }


    private boolean foregroundServiceRunning(){
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)){
            if(ChildLocationService.class.getName().equals(service.service.getClassName())){
                return true;
            }
        }
        return false;
    }


    private void addProfilePictureToFirestore(FirebaseStorage storage, Uri imgPath) {
        storage.getReference("profile_pics").child(getActivity()
                        .getSharedPreferences("user_data", Context.MODE_PRIVATE)
                        .getString("UUID", null))
                .putFile(imgPath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        dialog.dismiss();
                        updateProfilePicture();
                        //Toast.makeText(getContext(), "Success", //Toast.LENGTH_LONG).show();
//                        Intent intent = new Intent(getContext(), ProfileActivity.class);
//                        startActivity(intent);
//                        getActivity().finish();
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

    private void updateProfilePicture() {
        StorageReference reference = storage.getReference().child("profile_pics/" + preferences.getString("UUID", null));
//        TODO -> reference for the profile picture
        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                CircleImageView circleProfile = getActivity().findViewById(R.id.profile_picture);
                if(uri.equals("") || uri == null){

                }else{
                    Glide.with(context).load(uri).into(circleProfile);
                }
                //Toast.makeText(getContext(), "Profile Pic Updated", //Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Toast.makeText(getContext(), "Profile Picture Loading is Failed", //Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public void updateProfileDetails(){
        firestore.collection("users").document(context.getSharedPreferences("user_data", Context.MODE_PRIVATE).getString("UUID", "")).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                System.out.println("event Updated in profile fragment... in updateProfileDetails..!");
                userDetails2 = value.toObject(User.class);

                if(!userDetails2.getfName().isEmpty() && !userDetails2.getlName().isEmpty()){
                    profileName.setText(userDetails2.getfName() + " " + userDetails2.getlName());
                }else{
                    profileName.setText("N/A");
                }

                if(!userDetails2.getEmail().isEmpty()){
                    profileEmail.setText(userDetails2.getEmail());
                }else{
                    profileEmail.setText("N/A");
                }

                if(!userDetails2.getMobile().isEmpty()){
                    profileMobile.setText(userDetails2.getMobile());
                }else{
                    profileMobile.setText("N/A");
                }

                if(!userDetails2.getDisplayName().isEmpty()){
                    profileDisplayName.setText(userDetails2.getDisplayName());
                }else{
                    profileDisplayName.setText("N/A");
                }
            }
        });

        ((TextView)getActivity().findViewById(R.id.profile_text_pid)).setText("PID : " + getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE).getInt("PID", 0));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isLocationPermissionGranted() {
        if(getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean permissionGranted = false;

        if(requestCode == 1){
            for(int i = 0; i < permissions.length; i++){
                if(permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION) && grantResults[i] == PackageManager.PERMISSION_GRANTED){
                    permissionGranted = true;
                }else if (permissions[i].equals(Manifest.permission.ACCESS_COARSE_LOCATION) && grantResults[i] == PackageManager.PERMISSION_GRANTED){
                    permissionGranted = true;
                }else if (permissions[i].equals(Manifest.permission.ACCESS_BACKGROUND_LOCATION) && grantResults[i] == PackageManager.PERMISSION_GRANTED){
                    permissionGranted = true;
                }else if (permissions[i].equals(Manifest.permission.ACCESS_MEDIA_LOCATION) && grantResults[i] == PackageManager.PERMISSION_GRANTED){
                    permissionGranted = true;
                }else if (permissions[i].equals(Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS) && grantResults[i] == PackageManager.PERMISSION_GRANTED){
                    permissionGranted = true;
                }
            }

            if(permissionGranted){
//                map.setMyLocationEnabled(true);
                Log.e("TAG", "onRequestPermissionsResult: permission granted");
            }
        }

    }

}