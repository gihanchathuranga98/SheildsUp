package com.hdp.careup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SetPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);

        EditText pwd = findViewById(R.id.text_set_pwd);
        String role = getIntent().getExtras().getString("role");

        findViewById(R.id.set_pwd_back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                if(role.equals("parent")){
                    intent.putExtra("role", role);
                }else if(role.equals("child")){
                    intent.putExtra("role", role);
                }
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_set_pwd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pwd.getText().toString().length() == 6){
//                    Intent intent = new Intent(getApplicationContext(), PasswordActivity.class);
//                    startActivity(intent);
                    getSharedPreferences("user_data", Context.MODE_PRIVATE).edit().putInt("pwd", Integer.parseInt(pwd.getText().toString())).apply();
                    Toast.makeText(getApplicationContext(), "Password has set", Toast.LENGTH_LONG).show();
                    int testPwd = getSharedPreferences("user_data", Context.MODE_PRIVATE).getInt("pwd", 0);
                    Log.e("TAG", "onClick: testPwd : " + testPwd);
                    getSharedPreferences("user_data", Context.MODE_PRIVATE).edit().putString("PWD", "1").apply();
                    if(role.equals("parent")){
//                        getSupportFragmentManager().beginTransaction().replace(R.id.profile_container, new Settings()).commit();
                    }else{
//                        getSupportFragmentManager().beginTransaction().replace(R.id.profile_container, new SettingsChild()).commit();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Code should contain only 6 digits", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

}