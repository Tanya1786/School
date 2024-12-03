package com.example.school;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddEvalsPage extends AppCompatActivity {
    private DBHandler dbHandler;
    private Spinner divisionSpinner, evalTypeSpinner;
    private EditText evalNameET, evalPercentET;
    private ListView studentsListView;
    private Button saveButton, backButton;
    private StudentEvaluationsAdapter adapter;
    private List<Map<String, String>> studentList;
    private boolean isEditMode = false;
    private int evaluationId = -1;
    private String selectedDivision;
    private String selectedEvalName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_evals_page);
        dbHandler = new DBHandler(this);
        initializeViews();
        isEditMode = getIntent().getBooleanExtra("EDIT_EVAL_MODE", false);
        if (isEditMode) {
            selectedDivision = getIntent().getStringExtra("DIVISION");
            selectedEvalName = getIntent().getStringExtra("EVALUATION_NAME");
            loadExistingEvaluation();
        }
        setupSpinners();
        setupButtons();
    }
    private void initializeViews() {
        divisionSpinner = findViewById(R.id.divisionSpinner);
        evalTypeSpinner = findViewById(R.id.evalTypeSpinner);
        evalNameET = findViewById(R.id.evalNameEditText);
        evalPercentET = findViewById(R.id.evalPercentEditText);
        studentsListView = findViewById(R.id.studentsListView);
        saveButton = findViewById(R.id.saveButton);
        backButton = findViewById(R.id.backButton);
    }
    private void setupSpinners() {
        populateDivisionSpinner();
        populateEvalTypeSpinner();
        divisionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String selectedDivision = parent.getItemAtPosition(position).toString();
                    loadStudentsForDivision(selectedDivision);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
    private void setupButtons() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEvaluation();
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void populateDivisionSpinner() {
        List<String> divisions = new ArrayList<>();
        divisions.add("Select Division");

        Cursor cursor = dbHandler.getReadableDatabase().query(
                DBHandler.TABLE_CLASS,
                new String[]{DBHandler.CLASS_DIVISION},
                null, null, null, null, null
        );
        while (cursor.moveToNext()) {
            divisions.add(cursor.getString(0));
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                divisions
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        divisionSpinner.setAdapter(adapter);
        divisionSpinner.setSelection(0, false);
    }
    private void populateEvalTypeSpinner() {
        String[] evalTypes = {
                "Select Evaluation Type",
                "Midterm",
                "Final exam",
                "Homework",
                "Quiz",
                "Assignment",
                "Behavior"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                evalTypes
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        evalTypeSpinner.setAdapter(adapter);
        evalTypeSpinner.setSelection(0, false);
    }
    private void loadExistingEvaluation() {
        Cursor evalCursor = dbHandler.getReadableDatabase().rawQuery(
                "SELECT e." + DBHandler.EVALUATION_ID + ", e." + DBHandler.EVALUATION_TYPE +
                        ", e." + DBHandler.EVALUATION_PERCENTAGE +
                        " FROM " + DBHandler.TABLE_EVALUATION + " e" +
                        " JOIN " + DBHandler.TABLE_CLASS + " c ON e." + DBHandler.EVALUATION_CLASS_ID +
                        " = c." + DBHandler.CLASS_ID +
                        " WHERE c." + DBHandler.CLASS_DIVISION + " = ? AND e." +
                        DBHandler.EVALUATION_NAME + " = ?",
                new String[]{selectedDivision, selectedEvalName}
        );

        if (evalCursor.moveToFirst()) {
            evaluationId = evalCursor.getInt(0);
            final String evalType = evalCursor.getString(1);
            final double evalPercent = evalCursor.getDouble(2);
            evalNameET.setText(selectedEvalName);
            evalPercentET.setText(String.valueOf(evalPercent));
            divisionSpinner.post(new Runnable() {
                @Override
                public void run() {
                    int divPosition = ((ArrayAdapter)divisionSpinner.getAdapter())
                            .getPosition(selectedDivision);
                    divisionSpinner.setSelection(divPosition);
                }
            });

            evalTypeSpinner.post(new Runnable() {
                @Override
                public void run() {
                    int typePosition = ((ArrayAdapter)evalTypeSpinner.getAdapter())
                            .getPosition(evalType);
                    evalTypeSpinner.setSelection(typePosition);
                }
            });
        }
        evalCursor.close();
    }
    private void loadStudentsForDivision(String division) {
        studentList = new ArrayList<>();
        String query = "SELECT s." + DBHandler.STUDENT_ID +
                ", s." + DBHandler.STUDENT_FIRST_NAME +
                ", s." + DBHandler.STUDENT_LAST_NAME +
                ", se." + DBHandler.GRADE +
                " FROM " + DBHandler.TABLE_STUDENT + " s" +
                " LEFT JOIN " + DBHandler.TABLE_CLASS + " c ON s." + DBHandler.STUDENT_CLASS_ID + " = c." + DBHandler.CLASS_ID +
                " LEFT JOIN " + DBHandler.TABLE_STUDENT_EVALUATION + " se" +
                " ON s." + DBHandler.STUDENT_ID + " = se." + DBHandler.EVALUATION_STUDENT_ID +
                " AND se." + DBHandler.EVALUATION_ID_REF + " = " + evaluationId +
                " WHERE c." + DBHandler.CLASS_DIVISION + " = ?";

        Cursor cursor = dbHandler.getReadableDatabase().rawQuery(query, new String[]{division});
        while (cursor.moveToNext()) {
            Map<String, String> student = new HashMap<>();
            student.put("StudentID", cursor.getString(0));
            student.put("FirstName", cursor.getString(1));
            student.put("LastName", cursor.getString(2));
            if (!cursor.isNull(3)) {
                student.put("Grade", String.valueOf(cursor.getDouble(3)));
            }
            studentList.add(student);
        }
        cursor.close();

        adapter = new StudentEvaluationsAdapter(this, studentList,
                new StudentEvaluationsAdapter.OnGradeChangedListener() {
                    @Override
                    public void onGradeChanged(int position, String grade) {
                        studentList.get(position).put("Grade", grade);
                    }
                }
        );
        studentsListView.setAdapter(adapter);
    }
    private void saveEvaluation() {
        if (divisionSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select a division", Toast.LENGTH_SHORT).show();
            return;
        }
        if (evalTypeSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select an evaluation type", Toast.LENGTH_SHORT).show();
            return;
        }
        String division = divisionSpinner.getSelectedItem().toString();
        String evalType = evalTypeSpinner.getSelectedItem().toString();
        String evalName = evalNameET.getText().toString();
        String evalPercentStr = evalPercentET.getText().toString();
        if (evalName.isEmpty() || evalPercentStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        double evalPercent = Double.parseDouble(evalPercentStr);
        Cursor classCursor = dbHandler.getReadableDatabase().query(
                DBHandler.TABLE_CLASS,
                new String[]{DBHandler.CLASS_ID},
                DBHandler.CLASS_DIVISION + " = ?",
                new String[]{division},
                null, null, null
        );

        if (classCursor.moveToFirst()) {
            int classId = classCursor.getInt(0);
            classCursor.close();
            if (isEditMode) {
                dbHandler.getWritableDatabase().execSQL(
                        "UPDATE " + DBHandler.TABLE_EVALUATION +
                                " SET " + DBHandler.EVALUATION_TYPE + " = ?, " +
                                DBHandler.EVALUATION_PERCENTAGE + " = ?" +
                                " WHERE " + DBHandler.EVALUATION_ID + " = ?",
                        new Object[]{evalType, evalPercent, evaluationId}
                );
            } else {
                dbHandler.getWritableDatabase().execSQL(
                        "INSERT INTO " + DBHandler.TABLE_EVALUATION + " (" +
                                DBHandler.EVALUATION_NAME + ", " +
                                DBHandler.EVALUATION_TYPE + ", " +
                                DBHandler.EVALUATION_CLASS_ID + ", " +
                                DBHandler.EVALUATION_PERCENTAGE +
                                ") VALUES (?, ?, ?, ?)",
                        new Object[]{evalName, evalType, classId, evalPercent}
                );
            }
            saveGradesForStudents(evalName);
            finish();
        }
        classCursor.close();
    }
    private void saveGradesForStudents(String evalName) {
        for (Map<String, String> student : studentList) {
            String grade = student.get("Grade");
            if (grade != null) {
                dbHandler.getWritableDatabase().execSQL(
                        "INSERT OR REPLACE INTO " + DBHandler.TABLE_STUDENT_EVALUATION +
                                " (" + DBHandler.EVALUATION_STUDENT_ID + ", " +
                                DBHandler.EVALUATION_ID_REF + ", " +
                                DBHandler.GRADE + ") VALUES (?, ?, ?)",
                        new Object[]{student.get("StudentID"), evaluationId, grade}
                );
            }
        }
    }
}
