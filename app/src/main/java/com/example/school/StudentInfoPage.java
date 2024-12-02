package com.example.school;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class StudentInfoPage extends AppCompatActivity {
    TextView txtName, txtClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_info_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtName = findViewById(R.id.txtName);
        txtClass = findViewById(R.id.txtClass);

        Bundle extras = getIntent().getExtras();
        if(extras == null){
            return;
        }
        String nk = extras.getString("name_key");
        txtName.setText(nk);
        String ck = extras.getString("class_key");
        txtClass.setText(ck);

        Toast.makeText(StudentInfoPage.this, "Name: " + nk + "; Class: " + ck, Toast.LENGTH_LONG).show();
    }
}