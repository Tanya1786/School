package com.example.school;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class FindStudentPage extends AppCompatActivity {
    Spinner spClasses, spStudents;

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
        spStudents = findViewById(R.id.spStudents);
        // Create an ArrayAdapter using the string array and a default spinner layout.
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
//                this,
//                R.array.divisions,
//                android.R.layout.simple_spinner_item
//        );
//        // Specify the layout to use when the list of choices appears.
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        // Apply the adapter to the spinner.
//        spClasses.setAdapter(adapter);
    }
}