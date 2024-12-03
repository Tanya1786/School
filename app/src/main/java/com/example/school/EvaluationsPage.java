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

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class EvaluationsPage extends AppCompatActivity {
    private DBHandler dbHandler;
    private Spinner divisionSpinner, evaluationSpinner;
    private Button addNewEvalButton, editExistingEvalButton, backButton;

    private List<String> divisions = new ArrayList<>();
    private List<String> existingEvaluations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluations_page);
        dbHandler = new DBHandler(this);
        divisionSpinner = findViewById(R.id.divisionSpinner);
        evaluationSpinner = findViewById(R.id.evaluationSpinner);
        addNewEvalButton = findViewById(R.id.addNewEvalButton);
        editExistingEvalButton = findViewById(R.id.editExistingEvalButton);
        backButton = findViewById(R.id.backButton);
        populateDivisionSpinner();
        divisionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDivision = parent.getItemAtPosition(position).toString();
                loadExistingEvaluations(selectedDivision);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                existingEvaluations.clear();
                updateEvaluationSpinner();
            }
        });
        addNewEvalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EvaluationsPage.this, AddEvalsPage.class);
                startActivity(intent);
            }
        });

        editExistingEvalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (divisionSpinner.getSelectedItem() == null) {
                    Toast.makeText(EvaluationsPage.this, "Please select a division", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (evaluationSpinner.getSelectedItem() == null) {
                    Toast.makeText(EvaluationsPage.this, "Please select an evaluation", Toast.LENGTH_SHORT).show();
                    return;
                }
                String selectedDivision = divisionSpinner.getSelectedItem().toString();
                String selectedEvaluation = evaluationSpinner.getSelectedItem().toString();
                Intent intent = new Intent(EvaluationsPage.this, AddEvalsPage.class);
                intent.putExtra("EDIT_EVAL_MODE", true);
                intent.putExtra("DIVISION", selectedDivision);
                intent.putExtra("EVALUATION_NAME", selectedEvaluation);
                startActivity(intent);
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
        divisions.clear();
        Cursor cursor = dbHandler.getReadableDatabase().query(
                DBHandler.TABLE_CLASS,
                new String[]{DBHandler.CLASS_DIVISION},
                null, null, DBHandler.CLASS_DIVISION, null, null
        );
        while (cursor.moveToNext()) {
            String division = cursor.getString(0);
            if (!divisions.contains(division)) {
                divisions.add(division);
            }
        }
        cursor.close();
        ArrayAdapter<String> divisionAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                divisions
        );
        divisionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        divisionSpinner.setAdapter(divisionAdapter);
    }

    private void loadExistingEvaluations(String division) {
        existingEvaluations.clear();
        Cursor classCursor = dbHandler.getReadableDatabase().query(
                DBHandler.TABLE_CLASS,
                new String[]{DBHandler.CLASS_ID},
                DBHandler.CLASS_DIVISION + " = ?",
                new String[]{division},
                null, null, null
        );
        int classId = -1;
        if (classCursor.moveToFirst()) {
            classId = classCursor.getInt(0);
        }
        classCursor.close();
        if (classId == -1) {
            updateEvaluationSpinner();
            return;
        }
        Cursor evalCursor = dbHandler.getReadableDatabase().query(
                DBHandler.TABLE_EVALUATION,
                new String[]{DBHandler.EVALUATION_NAME},
                DBHandler.EVALUATION_CLASS_ID + " = ?",
                new String[]{String.valueOf(classId)},
                null, null, null
        );
        while (evalCursor.moveToNext()) {
            existingEvaluations.add(evalCursor.getString(0));
        }
        evalCursor.close();
        updateEvaluationSpinner();
    }
    private void updateEvaluationSpinner() {
        ArrayAdapter<String> evaluationAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                existingEvaluations
        );
        evaluationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        evaluationSpinner.setAdapter(evaluationAdapter);
        editExistingEvalButton.setEnabled(!existingEvaluations.isEmpty());
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateDivisionSpinner();
    }
}