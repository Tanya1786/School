package com.example.school;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class FindStudentPage extends AppCompatActivity {
    Spinner spClasses, spStudents;
    Button btnGoBack;
    private DBHandler dbhandler;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_find_student_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        spClasses = findViewById(R.id.spClasses);
        spStudents = findViewById(R.id.spEval);
        btnGoBack = findViewById(R.id.btnGoBack);
        dbhandler = new DBHandler(this);

        btnGoBack.setOnClickListener(view -> {
            Intent intent = new Intent(FindStudentPage.this, HomePage.class);
            startActivity(intent);
        });

        // Fetch the data from the database
        ArrayList<String> data = dbhandler.getData();

        ArrayAdapter<String> classes = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data);
        classes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spClasses.setAdapter(classes);
    }
}