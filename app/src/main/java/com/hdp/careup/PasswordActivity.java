package com.hdp.careup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class PasswordActivity extends AppCompatActivity {

    EditText pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        pwd = findViewById(R.id.text_pwd);
        findViewById(R.id.btn_pwd_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newPwd = Integer.parseInt(pwd.getText().toString());
                if(pwd.getText().toString().length() == 6){

                    int correctPwd = getSharedPreferences("user_data", Context.MODE_PRIVATE).getInt("pwd", 0);
                    if(correctPwd == Integer.parseInt(pwd.getText().toString())){
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(PasswordActivity.this, "Invalid Code", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(PasswordActivity.this, "Invalid Code", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}