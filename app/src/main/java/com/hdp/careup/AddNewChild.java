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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {} factory method to
 * create an instance of this fragment.
 */
public class AddNewChild extends Fragment {

    FirebaseFirestore firestore;
    public Map<String, String> childDetails = new HashMap<String, String>();
    public Set<String> keyset = new HashSet<String>();
    EditText pairID;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_new_child, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pairID = view.findViewById(R.id.text_pid);
        firestore = FirebaseFirestore.getInstance();

        view.findViewById(R.id.btn_add_new_pid).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long id = Long.parseLong(pairID.getText().toString());

//                checking is entered PID is same to the current user's PID
                if (Integer.parseInt(pairID.getText().toString()) != getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE).getInt("PID", 0)) {

                    firestore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            if (task.isSuccessful()) {
                                List<DocumentSnapshot> documents = task.getResult().getDocuments();

                                for (DocumentSnapshot document : documents) {

                                    if (document.getLong("pairID").compareTo(id) == 0) {
                                        DocumentReference reference = document.getReference();
                                        System.out.println("this is the reference -------> " + reference.getPath());

                                        if (isChild(document)) {
                                            addChildToParent(reference.getPath().replace("users/", ""), String.valueOf(id));

                                        } else {

                                            Toast.makeText(getContext(), "Not a parent", Toast.LENGTH_SHORT).show();
                                            ((EditText) getActivity().findViewById(R.id.text_pid)).setText("");

                                        }
                                    } else {

                                        ((EditText) getActivity().findViewById(R.id.text_pid)).setText("");

                                    }
                                }
                            }
                        }
                    });

                } else {
                    Toast.makeText(getContext(), "invalid pid new", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    class MyListener implements OnCompleteListener<DocumentSnapshot> {

        String uid, pid;

        public MyListener(String uid, String pid) {
            this.uid = uid;
            this.pid = pid;
        }

        @Override
        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

            if (task.isSuccessful()) {
                Map<String, String> children = task.getResult().toObject(User.class).getChildren();
//                childDetails = children;
//                System.out.println("here is the size of Map --------> " + children.size());
                if(children != null){
                    if (children.size() > 0) {
                        for (String key : children.keySet()) {
                            System.out.println("inside the for loop of children's keys -------> " + key);
                            childDetails.put(key, children.get(key));
                            keyset.add(key);
                        }
                    } else {
                        childDetails.put(uid, pairID.getText().toString());
                    }
                }else{
                    Toast.makeText(getContext(), "No Children Available..!", Toast.LENGTH_SHORT).show();
                }

                if (!keyset.contains(uid)) {
                    childDetails.put(uid, pid);
                    firestore.collection("users")
                            .document(getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE).getString("UUID", null))
                            .update("children", childDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getContext(), "Children Added Success..!", Toast.LENGTH_LONG).show();
                                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.profile_container, new Settings(), "Back to Settings").commit();
                                }
                            });
                }

            }
        }
    }

    private boolean isChild(DocumentSnapshot document) {
        if ((document.getString("role")).equals("CHILD")) {
            System.out.println("User role in isParent ------> " + document.getString("role"));
            return true;
        } else {
            System.out.println("Role condition failed............");
        }
        return false;
    }

    private void addChildToParent(String uid, String pid) {
        firestore.collection("users")
                .document(getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE).getString("UUID", ""))
                .get().addOnCompleteListener(new MyListener(uid, pid));
    }
}