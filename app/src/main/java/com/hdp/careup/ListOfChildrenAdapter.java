package com.hdp.careup;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {} factory method to
 * create an instance of this fragment.
 */
public class ListOfChildrenAdapter extends RecyclerView.Adapter<ListOfChildrenAdapter.MyViewHolder> {

    private ArrayList<String[]> childrenList;
    private FirebaseFirestore firestore;

    public ListOfChildrenAdapter(ArrayList<String[]> childrenList){
        this.childrenList = childrenList;
        System.out.println("ListOfChildrenAdapter ChildrenList Size -----> " + childrenList.size());
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        firestore = FirebaseFirestore.getInstance();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.child_item_template, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        getUserName(childrenList.get(position)[0], holder);
        holder.id.setText(childrenList.get(position)[1]);
    }

    private String getUserName(String uid, MyViewHolder holder) {

        firestore.collection("users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                User user = task.getResult().toObject(User.class);
                String displayName = user.getDisplayName();
                holder.name.setText(displayName);
            }
        });

        return null;
    }

    @Override
    public int getItemCount() {
        return childrenList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView name, id;
        FloatingActionButton delete;
        CircleImageView img;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.template_display_name);
            this.id = itemView.findViewById(R.id.template_pid);
            this.delete = itemView.findViewById(R.id.template_delete_btn);
            this.img = itemView.findViewById(R.id.template_profile);
        }

    }

}