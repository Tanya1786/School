package com.example.school;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    EditText txtUser, txtPass;
    Button btnLogin, btnCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtUser = findViewById(R.id.txtUser);
        txtPass = findViewById(R.id.txtPass);
        //btnCreateAccount = findViewById(R.id.btnCreateAccount);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = txtUser.getText().toString();
                String pass = txtPass.getText().toString();

                //SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                String savedUser = "Teacher";//sharedPreferences.getString("username", null);
                String savedPass = "Passward";//sharedPreferences.getString("password", null);

                if (user.isEmpty() || pass.isEmpty()){
                    Toast.makeText(MainActivity.this,"Please enter all your credentials...", Toast.LENGTH_SHORT).show();
                }
                else if (user.equals(savedUser) && pass.equals(savedPass)){
                    Intent intent = new Intent(MainActivity.this, HomePage.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(MainActivity.this,"Incorrect, please try again...", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                createAccount();
//            }
//        });
    }
    private void createAccount() {
        String username = txtUser.getText().toString();
        String password = txtPass.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save the username and password using SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.apply(); // Use apply() to save changes asynchronously

        Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();
    }
}