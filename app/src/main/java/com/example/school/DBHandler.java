package com.example.school;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DBHandler extends SQLiteOpenHelper {

    private static final String DB_NAME = "schoolDB";
    private static final int DB_VERSION = 4;

    private static final String TABLE_TEACHER = "Teacher";
    private static final String TABLE_CLASS = "Class";
    private static final String TABLE_STUDENT = "Student";
    private static final String TABLE_SCHEDULE = "Schedule";
    private static final String TABLE_EVALUATION = "Evaluation";
    private static final String TABLE_STUDENT_EVALUATION = "StudentEvaluation";

    private static final String TEACHER_ID = "TeacherID";
    private static final String TEACHER_USERNAME = "Username";
    private static final String TEACHER_PASSWORD = "Password";
    private static final String TEACHER_NAME = "Name";

    private static final String CLASS_ID = "ClassID";
    private static final String CLASS_DIVISION = "Division";

    private static final String STUDENT_ID = "StudentID";
    private static final String STUDENT_FIRST_NAME = "FirstName";
    private static final String STUDENT_LAST_NAME = "LastName";
    private static final String STUDENT_CLASS_ID = "ClassID";
    private static final String ATTENDANCE_COUNT = "AttendanceCount";
    private static final String GOOD_BEHAVIOR_COUNT = "GoodBehaviorCount";
    private static final String NO_HOMEWORK_COUNT = "NoHomeworkCount";
    private static final String LATE_COUNT = "LateCount";
    private static final String DISRUPTIONS_COUNT = "DisruptionsCount";
    private static final String CALCULATED_GRADE = "CalculatedGrade";

    private static final String SCHEDULE_ID = "ScheduleID";
    private static final String SCHEDULE_TEACHER_ID = "TeacherID";
    private static final String DAY_OF_WEEK = "DayOfWeek";
    private static final String SCHEDULE_CLASS_ID = "ClassID";
    private static final String SLOT_IN_DAY = "SlotInDay";

    private static final String EVALUATION_ID = "EvaluationID";
    private static final String EVALUATION_CLASS_ID = "ClassID";
    private static final String EVALUATION_TYPE = "Type";
    private static final String EVALUATION_PERCENTAGE = "Percentage";

    private static final String STUDENT_EVALUATION_ID = "StudentEvaluationID";
    private static final String EVALUATION_STUDENT_ID = "StudentID";
    private static final String EVALUATION_ID_REF = "EvaluationID";
    private static final String GRADE = "Grade";

    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TEACHER_TABLE = "CREATE TABLE " + TABLE_TEACHER + " (" +
                TEACHER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TEACHER_USERNAME + " TEXT NOT NULL UNIQUE, " +
                TEACHER_PASSWORD + " TEXT NOT NULL, " +
                TEACHER_NAME + " TEXT NOT NULL)";
        String CREATE_CLASS_TABLE = "CREATE TABLE " + TABLE_CLASS + " (" +
                CLASS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CLASS_DIVISION + " TEXT NOT NULL)";
        String CREATE_STUDENT_TABLE = "CREATE TABLE " + TABLE_STUDENT + " (" +
                STUDENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                STUDENT_FIRST_NAME + " TEXT NOT NULL, " +
                STUDENT_LAST_NAME + " TEXT NOT NULL, " +
                STUDENT_CLASS_ID + " INTEGER, " +
                ATTENDANCE_COUNT + " INTEGER DEFAULT 0, " +
                GOOD_BEHAVIOR_COUNT + " INTEGER DEFAULT 0, " +
                NO_HOMEWORK_COUNT + " INTEGER DEFAULT 0, " +
                LATE_COUNT + " INTEGER DEFAULT 0, " +
                DISRUPTIONS_COUNT + " INTEGER DEFAULT 0, " +
                CALCULATED_GRADE + " DECIMAL(5,2) DEFAULT 0.00)";
        String CREATE_SCHEDULE_TABLE = "CREATE TABLE " + TABLE_SCHEDULE + " (" +
                SCHEDULE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SCHEDULE_TEACHER_ID + " INTEGER, " +
                DAY_OF_WEEK + " TEXT NOT NULL, " +
                SCHEDULE_CLASS_ID + " INTEGER, " +
                SLOT_IN_DAY + " INTEGER)";

        String CREATE_EVALUATION_TABLE = "CREATE TABLE " + TABLE_EVALUATION + " (" +
                EVALUATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                EVALUATION_CLASS_ID + " INTEGER, " +
                EVALUATION_TYPE + " TEXT CHECK (Type IN ('Assignment', 'Lab', 'Quiz', 'Midterm', 'Final exam', 'Homework', 'Behavior')), " +
                EVALUATION_PERCENTAGE + " DECIMAL(5,2) NOT NULL)";
        String CREATE_STUDENT_EVALUATION_TABLE = "CREATE TABLE " + TABLE_STUDENT_EVALUATION + " (" +
                STUDENT_EVALUATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                EVALUATION_STUDENT_ID + " INTEGER, " +
                EVALUATION_ID_REF + " INTEGER, " +
                GRADE + " DECIMAL(5,2) NOT NULL)";

        db.execSQL(CREATE_TEACHER_TABLE);
        db.execSQL(CREATE_CLASS_TABLE);
        db.execSQL(CREATE_STUDENT_TABLE);
        db.execSQL(CREATE_SCHEDULE_TABLE);
        db.execSQL(CREATE_EVALUATION_TABLE);
        db.execSQL(CREATE_STUDENT_EVALUATION_TABLE);
        insertInitialData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEACHER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHEDULE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVALUATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENT_EVALUATION);

        onCreate(db);
    }

    public boolean checkTeacherCredentials(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_TEACHER + " WHERE " + TEACHER_USERNAME + " = ? AND " + TEACHER_PASSWORD + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username, password});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public void insertInitialData(SQLiteDatabase db) {
        ContentValues teacherValues = new ContentValues();
        teacherValues.put(TEACHER_USERNAME, "teacher1");
        teacherValues.put(TEACHER_PASSWORD, "password123");
        teacherValues.put(TEACHER_NAME, "John Doe");
        long result = db.insert(TABLE_TEACHER, null, teacherValues);
        if (result == -1) Log.e("DBHandler", "Failed to insert teacher data");

        String[] divisions = {"5", "19", "22", "10", "15", "30", "25"};
        for (String division : divisions) {
            ContentValues classValues = new ContentValues();
            classValues.put(CLASS_DIVISION, division);
            long classResult = db.insert(TABLE_CLASS, null, classValues);
            if (classResult == -1) Log.e("DBHandler", "Failed to insert class data");
        }

        String[][] students = {
                {"Alice", "Johnson", "1"},
                {"Bob", "Smith", "1"},
                {"Cameron", "White", "1"},
                {"Diana", "Hall", "1"},
                {"Ethan", "Young", "1"},
                {"Fiona", "Green", "1"},
                {"George", "King", "1"},
                {"Hannah", "Carter", "1"},
                {"Ian", "Baker", "1"},
                {"Jasmine", "Clark", "1"},
                {"Kyle", "Adams", "1"},
                {"Lara", "Thompson", "1"},
                {"Mason", "Scott", "1"},
                {"Natalie", "Nelson", "1"},
                {"Oscar", "Ward", "1"},
                {"Charlie", "Davis", "2"},
                {"David", "Evans", "2"},
                {"John", "Doe", "2"},
                {"Jane", "Smith", "2"},
                {"Max", "Brown", "2"},
                {"Lucy", "Johnson", "2"},
                {"Oliver", "Taylor", "2"},
                {"Sophia", "Anderson", "2"},
                {"Jackson", "Lee", "2"},
                {"Mia", "Moore", "2"},
                {"Isabella", "Harris", "2"},
                {"Liam", "Clark", "2"},
                {"Emma", "Lewis", "2"},
                {"Noah", "King", "2"},
                {"Ava", "Young", "2"},
                {"Eve", "Brown", "3"},
                {"Frank", "Wilson", "3"},
                {"Henry", "Moore", "3"},
                {"Ella", "White", "3"},
                {"Lucas", "Hall", "3"},
                {"Zoe", "King", "3"},
                {"Mason", "Green", "3"},
                {"Carter", "Scott", "3"},
                {"Grace", "Clark", "3"},
                {"Chloe", "Adams", "3"},
                {"Ella", "Baker", "3"},
                {"James", "Jackson", "3"},
                {"Scarlett", "Thompson", "3"},
                {"Aria", "Nelson", "3"},
                {"Michael", "Ward", "3"},
                {"Grace", "Taylor", "4"},
                {"Henry", "Moore", "4"},
                {"Ella", "White", "4"},
                {"Liam", "Brown", "4"},
                {"Emily", "Davis", "4"},
                {"Matthew", "Johnson", "4"},
                {"Amelia", "Garcia", "4"},
                {"Benjamin", "Martinez", "4"},
                {"Charlotte", "Hernandez", "4"},
                {"Oliver", "Lopez", "4"},
                {"Sophia", "Gonzalez", "4"},
                {"Lucas", "Wilson", "4"},
                {"Isabella", "Anderson", "4"},
                {"Jack", "Thomas", "4"},
                {"Aiden", "Robinson", "4"},
                {"Isabella", "Anderson", "5"},
                {"Jack", "Thomas", "5"},
                {"Ava", "Taylor", "5"},
                {"Ethan", "Jones", "5"},
                {"Sophia", "Brown", "5"},
                {"Liam", "Williams", "5"},
                {"Mia", "Davis", "5"},
                {"Zoe", "Garcia", "5"},
                {"Max", "Martinez", "5"},
                {"Emily", "Rodriguez", "5"},
                {"Jacob", "Wilson", "5"},
                {"Charlotte", "Lopez", "5"},
                {"Amelia", "Gonzalez", "5"},
                {"William", "Hernandez", "5"},
                {"Elijah", "Lee", "5"},
                {"Kathy", "Martin", "6"},
                {"Leo", "Jackson", "6"},
                {"Sofia", "Thompson", "6"},
                {"Daniel", "Clark", "6"},
                {"Olivia", "King", "6"},
                {"Alexander", "Scott", "6"},
                {"Avery", "Young", "6"},
                {"Grace", "Harris", "6"},
                {"Harper", "Lewis", "6"},
                {"Scarlett", "Robinson", "6"},
                {"Aria", "Walker", "6"},
                {"Layla", "Hall", "6"},
                {"Gavin", "Allen", "6"},
                {"Chloe", "Ward", "6"},
                {"Aiden", "Jackson", "6"},
                {"Mia", "Harris", "7"},
                {"Noah", "Lewis", "7"},
                {"Olivia", "Walker", "7"},
                {"Aiden", "Adams", "7"},
                {"Layla", "Baker", "7"},
                {"Ella", "Hernandez", "7"},
                {"Sophia", "Thompson", "7"},
                {"James", "Jackson", "7"},
                {"Lucas", "Green", "7"},
                {"Abigail", "Carter", "7"},
                {"Grace", "Scott", "7"},
                {"Benjamin", "Hall", "7"},
                {"Isabella", "Young", "7"},
                {"Daniel", "Clark", "7"},
                {"Zoe", "Nelson", "7"}
        };

        for (String[] student : students) {
            ContentValues studentValues = new ContentValues();
            studentValues.put(STUDENT_FIRST_NAME, student[0]);
            studentValues.put(STUDENT_LAST_NAME, student[1]);
            studentValues.put(STUDENT_CLASS_ID, student[2]);
            long studentResult = db.insert(TABLE_STUDENT, null, studentValues);
            if (studentResult == -1) Log.e("DBHandler", "Failed to insert student data");
        }

        String[][] schedules = {
                {"1", "Monday", "1", "1"},
                {"1", "Monday", "2", "2"},
                {"1", "Monday", "3", "3"},
                {"1", "Monday", "4", "4"},
                {"1", "Monday", "5", "5"},
                {"1", "Monday", "6", "6"},
                {"1", "Tuesday", "2", "1"},
                {"1", "Tuesday", "2", "2"},
                {"1", "Tuesday", "1", "3"},
                {"1", "Tuesday", "4", "4"},
                {"1", "Tuesday", "6", "5"},
                {"1", "Tuesday", "7", "6"},
                {"1", "Wednesday", "1", "1"},
                {"1", "Wednesday", "1", "2"},
                {"1", "Wednesday", "3", "3"},
                {"1", "Wednesday", "4", "4"},
                {"1", "Wednesday", "7", "5"},
                {"1", "Wednesday", "7", "6"},
                {"1", "Thursday", "4", "1"},
                {"1", "Thursday", "4", "2"},
                {"1", "Thursday", "3", "3"},
                {"1", "Thursday", "5", "4"},
                {"1", "Thursday", "6", "5"},
                {"1", "Thursday", "6", "6"},
                {"1", "Friday", "1", "1"},
                {"1", "Friday", "2", "2"},
                {"1", "Friday", "3", "3"},
                {"1", "Friday", "4", "4"},
                {"1", "Friday", "5", "5"},
                {"1", "Friday", "6", "6"}
        };

        for (String[] schedule : schedules) {
            ContentValues scheduleValues = new ContentValues();
            scheduleValues.put(SCHEDULE_TEACHER_ID, schedule[0]);
            scheduleValues.put(DAY_OF_WEEK, schedule[1]);
            scheduleValues.put(SCHEDULE_CLASS_ID, schedule[2]);
            scheduleValues.put(SLOT_IN_DAY, schedule[3]);
            long scheduleResult = db.insert(TABLE_SCHEDULE, null, scheduleValues);
            if (scheduleResult == -1) Log.e("DBHandler", "Failed to insert schedule data");
        }


        String[][] evaluations = {
                {"1", "Assignment", "10.00"},
                {"1", "Quiz", "15.00"}
        };
        for (String[] evaluation : evaluations) {
            ContentValues evaluationValues = new ContentValues();
            evaluationValues.put(EVALUATION_CLASS_ID, evaluation[0]);
            evaluationValues.put(EVALUATION_TYPE, evaluation[1]);
            evaluationValues.put(EVALUATION_PERCENTAGE, evaluation[2]);
            long evaluationResult = db.insert(TABLE_EVALUATION, null, evaluationValues);
            if (evaluationResult == -1) Log.e("DBHandler", "Failed to insert evaluation data");
        }

    }


}
