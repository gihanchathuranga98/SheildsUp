package com.hdp.careup;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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

        if (getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE).getString("PWD", null) == null) {
            switchMaterial.setChecked(false);
        } else {
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
                if (isChecked) {
//                    TODO -> show the set screen pin | store the pin number in shared preference
                    getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE).edit().putString("PWD", "1").apply();
                } else {
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

        view.findViewById(R.id.linked_child_accounts).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getChildrenFromDb();
            }
        });


    }


    private void getChildrenFromDb() {

        firestore.collection("users")
                .document(getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE).getString("UUID", null))
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        ArrayList<String[]> childrenDetails = new ArrayList<>();
                        if (task.isSuccessful()) {

                            Map<String, String> children = task.getResult().toObject(User.class).getChildren();

                            Collection<String> values = children.values();
                            Set<String> keys = children.keySet();

                            for (String uid : keys) {
                                System.out.println("Checking Map -----> " + uid);
                                String value = children.get(uid);
                                String[] details = {uid, value};
                                childrenDetails.add(details);

                            }


                            getParentFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.profile_container, new ListOfChildren(childrenDetails))
                                    .commit();


                            System.out.println("here is the details of children --------> " + childrenDetails.get(0)[1]);


                        }
//                        System.out.println("this is the details of the children --------> " + childrenDetails.get(0)[2]);
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