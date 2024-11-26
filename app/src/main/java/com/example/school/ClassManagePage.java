package com.example.school;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class ClassManagePage extends AppCompatActivity {
    private ListView studentListView;
    private Button btnSave, btnBack;
    private DBHandler dbHandler;
    private String classId;
    private String timeSlot;
    private ArrayList<HashMap<String, String>> studentList;
    public HashMap<String, Boolean> checkStates;
    private String currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_manage_page);

        studentListView = findViewById(R.id.studentListView);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);
        dbHandler = new DBHandler(this);
        checkStates = new HashMap<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        currentDate = dateFormat.format(Calendar.getInstance().getTime());
        classId = getIntent().getStringExtra("classId");
        timeSlot = getIntent().getStringExtra("timeSlot");

        if (classId == null || timeSlot == null) {
            Toast.makeText(this, "Error: Class or time slot not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        loadStudents();
        loadPreviousSelections();
        btnSave.setOnClickListener(v -> saveSelections());
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(ClassManagePage.this, SchedulePage.class);
            startActivity(intent);
        });
    }

    private void loadStudents() {
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        String query = "SELECT StudentID, FirstName, LastName FROM Student WHERE ClassID = ? ORDER BY LastName, FirstName";
        Cursor cursor = db.rawQuery(query, new String[]{classId});
        studentList = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            int studentIdIndex = cursor.getColumnIndexOrThrow("StudentID");
            int lastNameIndex = cursor.getColumnIndexOrThrow("LastName");
            int firstNameIndex = cursor.getColumnIndexOrThrow("FirstName");

            do {
                HashMap<String, String> student = new HashMap<>();
                student.put("studentId", cursor.getString(studentIdIndex));
                student.put("name", cursor.getString(lastNameIndex) + ", " +
                        cursor.getString(firstNameIndex));
                studentList.add(student);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
    }

    private void loadPreviousSelections() {
        SQLiteDatabase db = dbHandler.getReadableDatabase();

        checkStates.clear();
        for (HashMap<String, String> student : studentList) {
            String studentId = student.get("studentId");
            checkStates.put(studentId + "_attendance", false);
            checkStates.put(studentId + "_behavior", false);
            checkStates.put(studentId + "_homework", false);
            checkStates.put(studentId + "_late", false);
            checkStates.put(studentId + "_disruption", false);
            String query = "SELECT AttendanceMarked, GoodBehaviorMarked, NoHomeworkMarked, LateMarked, DisruptionMarked " +
                    "FROM DailyRecords WHERE StudentID = ? AND Date = ? AND ClassID = ? AND TimeSlot = ?";
            Cursor cursor = db.rawQuery(query, new String[]{studentId, currentDate, classId, timeSlot});

            if (cursor != null && cursor.moveToFirst()) {
                int attendanceIndex = cursor.getColumnIndexOrThrow("AttendanceMarked");
                int behaviorIndex = cursor.getColumnIndexOrThrow("GoodBehaviorMarked");
                int homeworkIndex = cursor.getColumnIndexOrThrow("NoHomeworkMarked");
                int lateIndex = cursor.getColumnIndexOrThrow("LateMarked");
                int disruptionIndex = cursor.getColumnIndexOrThrow("DisruptionMarked");

                checkStates.put(studentId + "_attendance", cursor.getInt(attendanceIndex) == 1);
                checkStates.put(studentId + "_behavior", cursor.getInt(behaviorIndex) == 1);
                checkStates.put(studentId + "_homework", cursor.getInt(homeworkIndex) == 1);
                checkStates.put(studentId + "_late", cursor.getInt(lateIndex) == 1);
                checkStates.put(studentId + "_disruption", cursor.getInt(disruptionIndex) == 1);
                cursor.close();
            }
        }
        db.close();
        StudentListAdapter adapter = new StudentListAdapter(
                this,
                studentList,
                new String[]{"name"},
                new int[]{R.id.studentName}
        );
        studentListView.setAdapter(adapter);
    }

    private void saveSelections() {
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        db.beginTransaction();

        try {
            db.delete("DailyRecords", "ClassID = ? AND Date = ? AND TimeSlot = ?",
                    new String[]{classId, currentDate, timeSlot});
            for (HashMap<String, String> student : studentList) {
                String studentId = student.get("studentId");
                ContentValues dailyValues = new ContentValues();
                dailyValues.put("StudentID", studentId);
                dailyValues.put("ClassID", classId);
                dailyValues.put("Date", currentDate);
                dailyValues.put("TimeSlot", timeSlot);  // Add TimeSlot to the record
                dailyValues.put("AttendanceMarked", checkStates.getOrDefault(studentId + "_attendance", false) ? 1 : 0);
                dailyValues.put("GoodBehaviorMarked", checkStates.getOrDefault(studentId + "_behavior", false) ? 1 : 0);
                dailyValues.put("NoHomeworkMarked", checkStates.getOrDefault(studentId + "_homework", false) ? 1 : 0);
                dailyValues.put("LateMarked", checkStates.getOrDefault(studentId + "_late", false) ? 1 : 0);
                dailyValues.put("DisruptionMarked", checkStates.getOrDefault(studentId + "_disruption", false) ? 1 : 0);

                db.insert("DailyRecords", null, dailyValues);
                String updateQuery = "UPDATE Student SET " +
                        "AttendanceCount = (SELECT COUNT(*) FROM DailyRecords WHERE StudentID = ? AND AttendanceMarked = 1), " +
                        "GoodBehaviorCount = (SELECT COUNT(*) FROM DailyRecords WHERE StudentID = ? AND GoodBehaviorMarked = 1), " +
                        "NoHomeworkCount = (SELECT COUNT(*) FROM DailyRecords WHERE StudentID = ? AND NoHomeworkMarked = 1), " +
                        "LateCount = (SELECT COUNT(*) FROM DailyRecords WHERE StudentID = ? AND LateMarked = 1), " +
                        "DisruptionsCount = (SELECT COUNT(*) FROM DailyRecords WHERE StudentID = ? AND DisruptionMarked = 1) " +
                        "WHERE StudentID = ?";

                db.execSQL(updateQuery, new String[]{studentId, studentId, studentId, studentId, studentId, studentId});
            }

            db.setTransactionSuccessful();
            Toast.makeText(this, "Changes saved successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error saving changes", Toast.LENGTH_SHORT).show();
        } finally {
            db.endTransaction();
            db.close();
        }
    }
}