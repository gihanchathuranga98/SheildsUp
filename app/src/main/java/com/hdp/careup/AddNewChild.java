package com.hdp.careup;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {} factory method to
 * create an instance of this fragment.
 */
public class AddNewChild extends Fragment {

    FirebaseFirestore firestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_new_child, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText pairID = view.findViewById(R.id.text_pid);
        firestore = FirebaseFirestore.getInstance();

        view.findViewById(R.id.btn_verify_pid).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Integer.parseInt(pairID.getText().toString()) != getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE).getInt("PID", 0)){

                    firestore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                List<DocumentSnapshot> documents = task.getResult().getDocuments();
                                for(DocumentSnapshot document : documents){

                                    if((long)document.get("pairID") == Integer.parseInt(pairID.getText().toString())){
//                                        addChildToParent(document.getReference().toString().replace("users/", ""));
                                    }else{
//                                        Children is not available
                                    }

                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

                }else{
//                    duplicate entry
                }

            }
        });

    }

    private boolean isParent() {
        return false;
    }

    private void addChildToParent(String uid) {

    }
}