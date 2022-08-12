package com.hdp.careup;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class LiveTrackingAdapter extends RecyclerView.Adapter<LiveTrackingAdapter.MyViewHolder> {

    ArrayList<String> uuid = new ArrayList<>();
    FirebaseFirestore firestore;

    public LiveTrackingAdapter(){}

    public LiveTrackingAdapter(ArrayList<String> uuid){
        this();
        this.uuid = uuid;
    }

    @NonNull
    @Override
    public LiveTrackingAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.child_layout_template_live_tracking, parent, false);
        LiveTrackingAdapter.MyViewHolder viewHolder = new LiveTrackingAdapter.MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull LiveTrackingAdapter.MyViewHolder holder, int position) {

        firestore = FirebaseFirestore.getInstance();
        System.out.println("this is the UUID of onBindViewHolder ---> " + uuid.get(position));

        firestore.collection("users")
                .document(uuid.get(position)).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String displayName = task.getResult().getString("displayName");
                long pid = task.getResult().getLong("pairID");
//                User child = task.getResult().toObject(User.class);
                holder.name.setText(displayName);
                holder.id.setText(pid+"");
            }
        });

    }

    @Override
    public int getItemCount() {
        return uuid.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, id;
        FloatingActionButton btn;
        CircleImageView profile;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.template_live_tracking_name);
            id = itemView.findViewById(R.id.template_live_tracking_pid);
            btn = itemView.findViewById(R.id.template_liva_tracking_btn);
            profile = itemView.findViewById(R.id.template_live_tracking_profile);
        }
    }
}
