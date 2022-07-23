package com.hdp.careup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.profile_container, new Profile(), "profile loaded");
        transaction.commit();


        ((BottomNavigationView)findViewById(R.id.bottom_navigation)).setOnItemSelectedListener(
                item -> {
                    Toast.makeText(getApplicationContext(), "Item "+item.getItemId()+" Has Selected.",
                            Toast.LENGTH_LONG).show();
                    System.out.println("Item ID : "+item.getItemId());

                    if(item.getItemId() == 2131362316){
                        getSupportFragmentManager().beginTransaction()
                        .replace(R.id.profile_container, new Profile(), "profile loaded")
                        .commit();
                    }else if(item.getItemId() == 2131362332){
                        System.out.println("item mounted");
                        getSupportFragmentManager().beginTransaction().replace(R.id.profile_container, new Tracking(), "Tracking loaded")
                                .commit();
                    }
                    return true;
                });

        BottomNavigationView navView2 = findViewById(R.id.bottom_navigation);
        navView2.setOnItemReselectedListener(item -> {
//            if(item.getItemId() == 2131362316){
//                System.out.println("item removed");
//                Fragment oldFragment = getSupportFragmentManager().findFragmentById(R.id.profile);
//                getSupportFragmentManager().beginTransaction().remove(oldFragment).commit();
//            }else if(item.getItemId() == 2131362332){
//                Fragment oldFragment = getSupportFragmentManager().findFragmentById(R.id.tracker);
//                getSupportFragmentManager().beginTransaction().remove(oldFragment).commit();
//            }
        });

    }
}