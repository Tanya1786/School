package com.example.school;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindStudentPage extends AppCompatActivity {
    private Spinner spClasses, spStudents;
    private Button btnSubmit, btnGoBack;
    private DBHandler dbHandler;
    private Map<String, Integer> studentIdMap = new HashMap<>();

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
        dbHandler = new DBHandler(this);
        spClasses = findViewById(R.id.spClasses);
        spStudents = findViewById(R.id.spStudents);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnGoBack = findViewById(R.id.btnGoBack);
        populateClassesSpinner();
        spClasses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedClass = parent.getItemAtPosition(position).toString();
                populateStudentsSpinner(selectedClass);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spStudents.setAdapter(null);
            }
        });
        btnSubmit.setOnClickListener(v -> {
            String selectedStudent = spStudents.getSelectedItem().toString();
            Integer studentId = studentIdMap.get(selectedStudent);

            if (studentId != null) {
                Intent intent = new Intent(FindStudentPage.this, StudentInfoPage.class);
                intent.putExtra("student_id", studentId);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Please select a student", Toast.LENGTH_SHORT).show();
            }
        });
        btnGoBack.setOnClickListener(v -> finish());
    }
    private void populateClassesSpinner() {
        List<String> classes = new ArrayList<>();
        Cursor cursor = dbHandler.getReadableDatabase().query(
                DBHandler.TABLE_CLASS,
                new String[]{DBHandler.CLASS_DIVISION},
                null, null, null, null, null
        );
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int divisionIndex = cursor.getColumnIndex(DBHandler.CLASS_DIVISION);
                classes.add(cursor.getString(divisionIndex));
            }
            cursor.close();
        }
        ArrayAdapter<String> classAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                classes
        );
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spClasses.setAdapter(classAdapter);
    }
    private void populateStudentsSpinner(String selectedClass) {
        List<String> students = new ArrayList<>();
        studentIdMap.clear();
        Cursor classCursor = dbHandler.getReadableDatabase().query(
                DBHandler.TABLE_CLASS,
                new String[]{DBHandler.CLASS_ID},
                DBHandler.CLASS_DIVISION + " = ?",
                new String[]{selectedClass},
                null, null, null
        );
        int classId = -1;
        if (classCursor != null && classCursor.moveToFirst()) {
            int classIdIndex = classCursor.getColumnIndex(DBHandler.CLASS_ID);
            classId = classCursor.getInt(classIdIndex);
            classCursor.close();
        }
        if (classId != -1) {
            Cursor studentCursor = dbHandler.getReadableDatabase().query(
                    DBHandler.TABLE_STUDENT,
                    new String[]{DBHandler.STUDENT_ID, DBHandler.STUDENT_FIRST_NAME, DBHandler.STUDENT_LAST_NAME},
                    DBHandler.STUDENT_CLASS_ID + " = ?",
                    new String[]{String.valueOf(classId)},
                    null, null, null
            );
            if (studentCursor != null) {
                while (studentCursor.moveToNext()) {
                    int studentIdIndex = studentCursor.getColumnIndex(DBHandler.STUDENT_ID);
                    int firstNameIndex = studentCursor.getColumnIndex(DBHandler.STUDENT_FIRST_NAME);
                    int lastNameIndex = studentCursor.getColumnIndex(DBHandler.STUDENT_LAST_NAME);
                    int studentId = studentCursor.getInt(studentIdIndex);
                    String firstName = studentCursor.getString(firstNameIndex);
                    String lastName = studentCursor.getString(lastNameIndex);
                    String fullName = firstName + " " + lastName;

                    students.add(fullName);
                    studentIdMap.put(fullName, studentId);
                }
                studentCursor.close();
            }
        }
        ArrayAdapter<String> studentAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                students
        );
        studentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spStudents.setAdapter(studentAdapter);
    }
}