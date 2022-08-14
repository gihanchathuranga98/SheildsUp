package com.hdp.careup;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {} factory method to
 * create an instance of this fragment.
 */
public class LiveTracking extends Fragment {

    private FirebaseFirestore firestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_live_tracking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firestore = FirebaseFirestore.getInstance();

//        get all of the children
        firestore.collection("users")
                .document(getActivity()
                        .getSharedPreferences("user_data", Context.MODE_PRIVATE)
                        .getString("UUID", "")).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        ArrayList<String> uuid = new ArrayList<String>();
                        Map<String, String> children = task.getResult().toObject(User.class).getChildren();


                        if(children != null){
                            Set<String> childKey = children.keySet();
                            for(String key : childKey){
                                uuid.add(key);
                            }

                            RecyclerView recyclerView = view.findViewById(R.id.template_live_tracking_recycle_view);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            LiveTrackingAdapter adapter = new LiveTrackingAdapter(uuid);
                            recyclerView.setAdapter(adapter);
                        }else{
                            Toast.makeText(getContext(), "No children were added", Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }
}