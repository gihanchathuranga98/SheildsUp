package com.hdp.careup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;


public class ProfileActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    private FirebaseFirestore firestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSharedPreferences("user_data", Context.MODE_PRIVATE).edit().putString("stat", "1").apply();

        setContentView(R.layout.activity_profile);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.profile_container, new Profile(), "profile loaded");
        transaction.commit();

        BottomNavigationView navView2 = findViewById(R.id.bottom_navigation);
        navView2.setOnItemSelectedListener(this);
        navView2.setSelectedItemId(R.id.profile);

        firestore = FirebaseFirestore.getInstance();

        firestore.collection("users")
                .document(getApplicationContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
                        .getString("UUID", null)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        User userPID = task.getResult().toObject(User.class);
                        if(userPID.getPairID() == 0){
                            int pid = generateRandomId();
                            setPairID(pid, userPID);
                        }else{
                            System.out.println("user PID is available");
                            getApplicationContext().getSharedPreferences("user_data", Context.MODE_PRIVATE).edit().putInt("PID", userPID.getPairID()).apply();
                            System.out.println(" PID shared preferences ---------> " + getApplicationContext().getSharedPreferences("user_data", Context.MODE_PRIVATE).getInt("PID", 0));
                        }

                        if(userPID.getRole() == null || userPID.getRole().equals("")){
                            System.out.println("came to check user role");
                            addRoleToUser(getApplicationContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
                                    .getString("UUID", null), userPID);
                        }

                        System.out.println("Here is users user Roll ----------> " + userPID.getRole());
                        getApplicationContext().getSharedPreferences("user_data", Context.MODE_PRIVATE).edit().putString("ROLE", userPID.getRole()).apply();
                    }
                });


    }

    private void addRoleToUser(String uid, User user) {
        user.setRole("PARENT");
        firestore.collection("users").document(uid).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                System.out.println("User role has assigned");
            }
        });
    }

    private void setPairID(int id, User userPid) {
        userPid.setPairID(id);
        firestore.collection("users").document(getApplicationContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
                .getString("UUID", null)).set(userPid).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                getApplicationContext().getSharedPreferences("user_data", Context.MODE_PRIVATE).edit().putInt("PID", id).apply();
                System.out.println("PID has setted to the user");
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.profile:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.profile_container, new Profile(), "profile_")
                        .commit();
                return true;
            case R.id.tracker:
                if(getApplicationContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
                        .getString("ROLE", null).equalsIgnoreCase("PARENT")){
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.profile_container, new Tracking(), "Tracking loaded")
                            .commit();
                }else{
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.profile_container, new NoTrackingForChild(), "Tracking loaded")
                            .commit();
                }

                return true;
            case R.id.settings:

                        if(getApplicationContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
                                .getString("ROLE", null).equalsIgnoreCase("PARENT")){
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.profile_container, new Settings(), "Tracking loaded")
                                    .commit();
                        }else{
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.profile_container, new SettingsChild(), "Tracking loaded")
                                    .commit();
                        }

                return true;
        }
        return false;
    }

    private int generateRandomId(){
        int id;
        do {
            id = (int)Math.floor(Math.random() * 1000000);
            System.out.println("came to generateRandomID()");
        }while (isPairIdUnique(id));
        System.out.println("return id -----> " + id);
        return id;
    }

    private boolean isPairIdUnique(int id) {
        getApplicationContext().getSharedPreferences("user_data", Context.MODE_PRIVATE).edit().putBoolean("unique", false).apply();
        firestore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<User> users = task.getResult().toObjects(User.class);
                for(User userNew : users){
                    System.out.println("user paidID -------> " + userNew.getPairID());
                    if(userNew.getPairID() == id){
                        getApplicationContext().getSharedPreferences("user_data", Context.MODE_PRIVATE).edit().putBoolean("unique", true).apply();
                    }
                }
            }
        });
        return getApplicationContext().getSharedPreferences("user_data", Context.MODE_PRIVATE).getBoolean("unique", true);
    }

}