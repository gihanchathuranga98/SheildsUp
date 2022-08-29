package com.hdp.careup;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {} factory method to
 * create an instance of this fragment.
 */
public class SettingsChild extends Fragment {

    FirebaseFirestore firestore;
    Context context;
    FragmentManager parentFragmentManager;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings_child, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firestore = FirebaseFirestore.getInstance();
        SwitchMaterial switchMaterial = view.findViewById(R.id.screen_lock_switch_child);
        parentFragmentManager = getParentFragmentManager();


        if (getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE).getString("PWD", null) == null) {
            switchMaterial.setChecked(false);
        } else {
            switchMaterial.setChecked(true);
        }

        switchMaterial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    Intent intent = new Intent(getContext(), SetPasswordActivity.class);
                    intent.putExtra("role", "child");
                    startActivity(intent);

                    getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE).edit().putString("PWD", "1").apply();
                } else {
                    getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE).edit().remove("PWD").apply();
                }
            }
        });

        view.findViewById(R.id.settings_parent_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(getContext());
                alertDialogBuilder.setTitle("Are You Sure..?");
                alertDialogBuilder.setMessage("If you change the roll,\nParent Application cannot track you..!");
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setRoleToParent();
                        getParentFragmentManager().beginTransaction()
                                .replace(R.id.profile_container, new Settings(), "LOAD_CHILD")
                                .commit();
                    }
                });
                alertDialogBuilder.setNegativeButton("No", null);
                alertDialogBuilder.show();
            }
        });

        view.findViewById(R.id.settings_child_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle("Are You Sure..?")
                        .setMessage("Do you need to logout?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                System.out.println("came to logout");
                                getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE).edit().clear().apply();
                                Intent intent = new Intent(getActivity(), SplashActivity.class);
                                startActivity(intent);
                            }
                        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
            }
        });

    }

    private void setRoleToParent() {
        firestore.collection("users")
                .document(getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE).getString("UUID", null))
                .update("role", "PARENT").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        context.getSharedPreferences("user_data", Context.MODE_PRIVATE).edit().putString("ROLE", "PARENT").apply();
                        parentFragmentManager.beginTransaction()
                                .replace(R.id.profile_container, new Settings(), "LOAD_PARENT")
                                .commit();
                    }
                });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
}