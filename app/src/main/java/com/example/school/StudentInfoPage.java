package com.example.school;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class StudentInfoPage extends AppCompatActivity {
    TextView txtName, txtClass, txtEmail, txtPhone, txtAttend,
            txtGoodWord, txtDisrupt, txtLate, txtHW, txtGrades;
    DBHandler dbHandler;
    Button btnGoBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_info_page);
        btnGoBack = findViewById(R.id.btnGoBack);
        btnGoBack.setOnClickListener(view -> {
            Intent intent = new Intent(StudentInfoPage.this, HomePage.class);
            startActivity(intent);
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        dbHandler = new DBHandler(this);
        initializeTextViews();
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Toast.makeText(this, "No student data found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        int studentId = extras.getInt("student_id", -1);
        if (studentId == -1) {
            Toast.makeText(this, "Invalid student ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        displayStudentDetails(studentId);
    }
    private void initializeTextViews() {
        txtName = findViewById(R.id.txtName);
        txtClass = findViewById(R.id.txtClass);
        txtEmail = findViewById(R.id.txtEmail);
        txtPhone = findViewById(R.id.txtPhone);
        txtLate = findViewById(R.id.txtLate);
        txtAttend = findViewById(R.id.txtAttend);
        txtGoodWord = findViewById(R.id.txtGoodWord);
        txtDisrupt = findViewById(R.id.txtDisrupt);
        txtHW = findViewById(R.id.txtHW);
        txtGrades = findViewById(R.id.txtGrades);
    }

    private void displayStudentDetails(int studentId) {
        Cursor cursor = dbHandler.getReadableDatabase().query(
                DBHandler.TABLE_STUDENT,
                null,
                DBHandler.STUDENT_ID + " = ?",
                new String[]{String.valueOf(studentId)},
                null, null, null
        );
        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(DBHandler.STUDENT_FIRST_NAME);
            int lastNameIndex = cursor.getColumnIndex(DBHandler.STUDENT_LAST_NAME);
            int classIdIndex = cursor.getColumnIndex(DBHandler.STUDENT_CLASS_ID);
            int emailIndex = cursor.getColumnIndex(DBHandler.STUDENT_EMAIL);
            int phoneIndex = cursor.getColumnIndex(DBHandler.STUDENT_PHONE);
            String firstName = cursor.getString(nameIndex);
            String lastName = cursor.getString(lastNameIndex);
            txtName.setText(firstName + " " + lastName);
            int classId = cursor.getInt(classIdIndex);
            Cursor classCursor = dbHandler.getReadableDatabase().query(
                    DBHandler.TABLE_CLASS,
                    new String[]{DBHandler.CLASS_DIVISION},
                    DBHandler.CLASS_ID + " = ?",
                    new String[]{String.valueOf(classId)},
                    null, null, null
            );
            if (classCursor != null && classCursor.moveToFirst()) {
                int divisionIndex = classCursor.getColumnIndex(DBHandler.CLASS_DIVISION);
                txtClass.setText(classCursor.getString(divisionIndex));
                classCursor.close();
            }
            txtEmail.setText(cursor.getString(emailIndex));
            txtPhone.setText(cursor.getString(phoneIndex));
            cursor.close();
            calculateDailyRecordsMetrics(studentId);
            calculateStudentGrades(studentId, classId);
        } else {
            Toast.makeText(this, "Student not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    private void calculateDailyRecordsMetrics(int studentId) {
        Cursor studentCursor = dbHandler.getReadableDatabase().query(
                DBHandler.TABLE_STUDENT,
                new String[]{DBHandler.STUDENT_CLASS_ID},
                DBHandler.STUDENT_ID + " = ?",
                new String[]{String.valueOf(studentId)},
                null, null, null
        );
        if (studentCursor != null && studentCursor.moveToFirst()) {
            int classId = studentCursor.getInt(0);
            studentCursor.close();
            String totalSessionsQuery = "SELECT COUNT(*) " +
                    "FROM (SELECT DISTINCT Date, TimeSlot " +
                    "FROM DailyRecords " +
                    "WHERE ClassID = ?) as Sessions";

            Cursor totalSessionsCursor = dbHandler.getReadableDatabase().rawQuery(
                    totalSessionsQuery,
                    new String[]{String.valueOf(classId)}
            );
            String metricsQuery = "SELECT " +
                    "SUM(AttendanceMarked) as TotalAttendance, " +
                    "SUM(GoodBehaviorMarked) as TotalGoodBehavior, " +
                    "SUM(DisruptionMarked) as TotalDisruptions, " +
                    "SUM(NoHomeworkMarked) as TotalNoHomework, " +
                    "SUM(LateMarked) as TotalLate " +
                    "FROM DailyRecords " +
                    "WHERE StudentID = ?";
            Cursor metricsCursor = dbHandler.getReadableDatabase().rawQuery(
                    metricsQuery,
                    new String[]{String.valueOf(studentId)}
            );
            if (totalSessionsCursor != null && totalSessionsCursor.moveToFirst() &&
                    metricsCursor != null && metricsCursor.moveToFirst()) {
                int totalPossibleSessions = totalSessionsCursor.getInt(0);
                int attendedSessions = metricsCursor.getInt(0);
                double attendancePercentage = totalPossibleSessions > 0
                        ? (attendedSessions * 100.0) / totalPossibleSessions
                        : 0.0;
                txtAttend.setText(String.format("%.1f%%", attendancePercentage));
                txtGoodWord.setText(String.valueOf(metricsCursor.getInt(1)));
                txtDisrupt.setText(String.valueOf(metricsCursor.getInt(2)));
                txtHW.setText(String.valueOf(metricsCursor.getInt(3)));
                txtLate.setText(String.valueOf(metricsCursor.getInt(4)));
                metricsCursor.close();
                totalSessionsCursor.close();
            }
        }
    }
    private void calculateStudentGrades(int studentId, int classId) {
        String gradeQuery = "SELECT " +
                "e." + DBHandler.EVALUATION_NAME + ", " +
                "e." + DBHandler.EVALUATION_TYPE + ", " +
                "e." + DBHandler.EVALUATION_PERCENTAGE + ", " +
                "se." + DBHandler.GRADE + " " +
                "FROM " + DBHandler.TABLE_EVALUATION + " e " +
                "LEFT JOIN " + DBHandler.TABLE_STUDENT_EVALUATION + " se " +
                "ON e." + DBHandler.EVALUATION_ID + " = se." + DBHandler.EVALUATION_ID_REF +
                " AND se." + DBHandler.EVALUATION_STUDENT_ID + " = ? " +
                "WHERE e." + DBHandler.EVALUATION_CLASS_ID + " = ?";
       Cursor gradeCursor = dbHandler.getReadableDatabase().rawQuery(
                gradeQuery,
                new String[]{String.valueOf(studentId), String.valueOf(classId)}
        );
        double totalWeightedGrade = 0.0;
        double totalWeight = 0.0;
        if (gradeCursor != null) {
            while (gradeCursor.moveToNext()) {
                double percentage = gradeCursor.getDouble(2);
                double grade = gradeCursor.isNull(3) ? 0.0 : gradeCursor.getDouble(3);
                totalWeightedGrade += (grade * (percentage / 100.0));
                totalWeight += (percentage / 100.0);
            }
            gradeCursor.close();
        }
        double finalGrade = totalWeight > 0 ? totalWeightedGrade / totalWeight : 0.0;
        txtGrades.setText(String.format("%.2f", finalGrade));
    }
}