package com.hdp.careup;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {} factory method to
 * create an instance of this fragment.
 */
public class Settings extends Fragment {

    FirebaseFirestore firestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SwitchMaterial switchMaterial = view.findViewById(R.id.screen_lock_switch);
        firestore = FirebaseFirestore.getInstance();

        if(getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE).getString("PWD", null) == null){
            switchMaterial.setChecked(false);
        }else{
            switchMaterial.setChecked(true);
        }

        view.findViewById(R.id.settings_child_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(getContext());
                        alertDialogBuilder.setTitle("Are You Sure..?");
                        alertDialogBuilder.setMessage("Your connected devices will be removed.\nYou cannot recover those connections\nunless you add them manually..!");
                        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setRoleToChild();
                            }
                        });
                        alertDialogBuilder.setNegativeButton("No", null);
                        alertDialogBuilder.show();
            }
        });

        switchMaterial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
//                    TODO -> show the set screen pin | store the pin number in shared preference
                    getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE).edit().putString("PWD", "1").apply();
                }else{
                    getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE).edit().remove("PWD").apply();
                }
            }
        });

        view.findViewById(R.id.text_add_new_child_pid).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.profile_container, new AddNewChild(), "ADD_NEW_CHILD")
                        .commit();
            }
        });
    }

    private void setRoleToChild() {
        firestore.collection("users")
                .document(getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE).getString("UUID", null))
                .update("role", "CHILD").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE).edit().putString("ROLE", "CHILD").apply();
                        getParentFragmentManager().beginTransaction()
                                .replace(R.id.profile_container, new SettingsChild(), "LOAD_PARENT")
                                .commit();
                    }
                });
    }
}