package com.example.school;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class SchedulePage extends AppCompatActivity {
    private Button btnGoBack;
    private ImageButton btnPrevDay, btnNextDay;
    private ListView scheduleListView;
    private TextView noClassesText, dateText;
    private DBHandler dbHandler;
    private Calendar currentDate;

    private static final String SLOT_IN_DAY = "SlotInDay";
    private static final String CLASS_ID = "ClassID";
    private static final String DIVISION = "Division";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_schedule_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnGoBack = findViewById(R.id.btnGoBack);
        btnPrevDay = findViewById(R.id.btnPrevDay);
        btnNextDay = findViewById(R.id.btnNextDay);
        scheduleListView = findViewById(R.id.scheduleListView);
        noClassesText = findViewById(R.id.noClassesText);
        dateText = findViewById(R.id.dateText);
        dbHandler = new DBHandler(this);

        //today's  date
        currentDate = Calendar.getInstance();
        //skip to Monday if today is Saturday or Sunday
        if (currentDate.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            currentDate.add(Calendar.DATE, 2); // Skip to Monday
        } else if (currentDate.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            currentDate.add(Calendar.DATE, 1); // Skip to Monday
        }

        updateDateDisplay();
        loadSchedule();

        btnPrevDay.setOnClickListener(v -> navigateDay(-1));
        btnNextDay.setOnClickListener(v -> navigateDay(1));

        scheduleListView.setOnItemClickListener((parent, view, position, id) -> {
            HashMap<String, String> item = (HashMap<String, String>) parent.getItemAtPosition(position);
            String classId = item.get("classId");
            if (classId != null) {
                Intent intent = new Intent(SchedulePage.this, ClassManagePage.class);
                intent.putExtra("classId", classId);
                startActivity(intent);
            }
        });
        btnGoBack.setOnClickListener(view -> {
            Intent intent = new Intent(SchedulePage.this, HomePage.class);
            startActivity(intent);
        });
    }

    //navigate to the previous or next day
    private void navigateDay(int days) {
        currentDate.add(Calendar.DATE, days);

        //Skip weekends
        if (currentDate.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            currentDate.add(Calendar.DATE, 2);
        } else if (currentDate.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            currentDate.add(Calendar.DATE, 1);
        }
        if (days < 0 && currentDate.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
            currentDate.add(Calendar.DATE, -3);
        }

        if (days > 0 && currentDate.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
            currentDate.add(Calendar.DATE, 3);
        }
        updateDateDisplay();
        loadSchedule();
    }

    private void updateDateDisplay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.US);
        dateText.setText(dateFormat.format(currentDate.getTime()));
    }

    private void loadSchedule() {
        SQLiteDatabase db = dbHandler.getReadableDatabase();

        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);
        String currentDay = dayFormat.format(currentDate.getTime());

        String query = "SELECT s." + SLOT_IN_DAY + ", s." + CLASS_ID + ", c." + DIVISION +
                " FROM Schedule s " +
                "JOIN Class c ON s.ClassID = c.ClassID " +
                "WHERE s.DayOfWeek = ? " +
                "ORDER BY s." + SLOT_IN_DAY;

        Cursor cursor = db.rawQuery(query, new String[]{currentDay});

        ArrayList<HashMap<String, String>> scheduleList = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            int slotIndex = cursor.getColumnIndex(SLOT_IN_DAY);
            int classIdIndex = cursor.getColumnIndex(CLASS_ID);
            int divisionIndex = cursor.getColumnIndex(DIVISION);

            if (slotIndex >= 0 && classIdIndex >= 0 && divisionIndex >= 0) {
                do {
                    HashMap<String, String> schedule = new HashMap<>();
                    schedule.put("slot", "Period " + cursor.getString(slotIndex));
                    schedule.put("class", "Class " + cursor.getString(divisionIndex));
                    schedule.put("classId", cursor.getString(classIdIndex));
                    scheduleList.add(schedule);
                } while (cursor.moveToNext());
            }
        }

        if (!scheduleList.isEmpty()) {
            SimpleAdapter adapter = new SimpleAdapter(
                    this,
                    scheduleList,
                    R.layout.schedule_item,
                    new String[]{"slot", "class"},
                    new int[]{R.id.timeSlotText, R.id.classText}
            );

            scheduleListView.setAdapter(adapter);
            scheduleListView.setVisibility(View.VISIBLE);
            noClassesText.setVisibility(View.GONE);
        } else {
            scheduleListView.setVisibility(View.GONE);
            noClassesText.setVisibility(View.VISIBLE);
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();
    }
}
