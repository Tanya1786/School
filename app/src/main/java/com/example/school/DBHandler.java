package com.example.school;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DBHandler extends SQLiteOpenHelper {
    private static final String DB_NAME = "schoolDB";
    private static final int DB_VERSION = 10;
    public static final String TABLE_TEACHER = "Teacher";
    public static final String TABLE_CLASS = "Class";
    public static final String TABLE_STUDENT = "Student";
    public static final String TABLE_SCHEDULE = "Schedule";
    public static final String TABLE_EVALUATION = "Evaluation";
    public static final String TABLE_STUDENT_EVALUATION = "StudentEvaluation";
    public static final String CLASS_ID = "ClassID";
    public static final String CLASS_DIVISION = "Division";
    public static final String TEACHER_ID = "TeacherID";
    public static final String TEACHER_USERNAME = "Username";
    public static final String TEACHER_PASSWORD = "Password";
    public static final String TEACHER_NAME = "Name";
    public static final String STUDENT_ID = "StudentID";
    public static final String STUDENT_FIRST_NAME = "FirstName";
    public static final String STUDENT_LAST_NAME = "LastName";
    public static final String STUDENT_CLASS_ID = "ClassID";
    public static final String STUDENT_EMAIL = "Email";
    public static final String STUDENT_PHONE = "Phone";
    public static final String ATTENDANCE_COUNT = "AttendanceCount";
    public static final String GOOD_BEHAVIOR_COUNT = "GoodBehaviorCount";
    public static final String NO_HOMEWORK_COUNT = "NoHomeworkCount";
    public static final String LATE_COUNT = "LateCount";
    public static final String DISRUPTIONS_COUNT = "DisruptionsCount";
    public static final String CALCULATED_GRADE = "CalculatedGrade";
    public static final String SCHEDULE_ID = "ScheduleID";
    public static final String SCHEDULE_TEACHER_ID = "TeacherID";
    public static final String SCHEDULE_CLASS_ID = "ClassID";
    public static final String DAY_OF_WEEK = "DayOfWeek";
    public static final String SLOT_IN_DAY = "SlotInDay";
    public static final String TIME_SLOT = "TimeSlot";
    public static final String EVALUATION_ID = "EvaluationID";
    public static final String EVALUATION_CLASS_ID = "ClassID";
    public static final String EVALUATION_TYPE = "Type";
    public static final String EVALUATION_PERCENTAGE = "Percentage";
    public static final String EVALUATION_NAME = "EvaluationName";
    public static final String STUDENT_EVALUATION_ID = "StudentEvaluationID";
    public static final String EVALUATION_STUDENT_ID = "StudentID";
    public static final String EVALUATION_ID_REF = "EvaluationID";
    public static final String GRADE = "Grade";

    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTeacherTable = "CREATE TABLE IF NOT EXISTS " + TABLE_TEACHER + " (" +
                TEACHER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TEACHER_USERNAME + " TEXT NOT NULL UNIQUE, " +
                TEACHER_PASSWORD + " TEXT NOT NULL, " +
                TEACHER_NAME + " TEXT NOT NULL)";
        String createClassTable = "CREATE TABLE IF NOT EXISTS " + TABLE_CLASS + " (" +
                CLASS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CLASS_DIVISION + " TEXT NOT NULL)";
        String createStudentTable = "CREATE TABLE IF NOT EXISTS " + TABLE_STUDENT + " (" +
                STUDENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                STUDENT_FIRST_NAME + " TEXT NOT NULL, " +
                STUDENT_LAST_NAME + " TEXT NOT NULL, " +
                STUDENT_CLASS_ID + " INTEGER, " +
                STUDENT_EMAIL + " TEXT NOT NULL, " +
                STUDENT_PHONE + " TEXT NOT NULL, " +
                ATTENDANCE_COUNT + " INTEGER DEFAULT 0, " +
                GOOD_BEHAVIOR_COUNT + " INTEGER DEFAULT 0, " +
                NO_HOMEWORK_COUNT + " INTEGER DEFAULT 0, " +
                LATE_COUNT + " INTEGER DEFAULT 0, " +
                DISRUPTIONS_COUNT + " INTEGER DEFAULT 0, " +
                CALCULATED_GRADE + " DECIMAL(5,2) DEFAULT 0.00, " +
                "FOREIGN KEY(" + STUDENT_CLASS_ID + ") REFERENCES " + TABLE_CLASS + "(" + CLASS_ID + "))";
        String createScheduleTable = "CREATE TABLE IF NOT EXISTS " + TABLE_SCHEDULE + " (" +
                SCHEDULE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SCHEDULE_TEACHER_ID + " INTEGER, " +
                DAY_OF_WEEK + " TEXT NOT NULL, " +
                SCHEDULE_CLASS_ID + " INTEGER, " +
                SLOT_IN_DAY + " INTEGER, " +
                "FOREIGN KEY(" + SCHEDULE_TEACHER_ID + ") REFERENCES " + TABLE_TEACHER + "(" + TEACHER_ID + "), " +
                "FOREIGN KEY(" + SCHEDULE_CLASS_ID + ") REFERENCES " + TABLE_CLASS + "(" + CLASS_ID + "))";
        String createEvaluationTable = "CREATE TABLE " + TABLE_EVALUATION + " (" +
                EVALUATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                EVALUATION_CLASS_ID + " INTEGER, " +
                EVALUATION_NAME + " TEXT NOT NULL UNIQUE, " +
                EVALUATION_TYPE + " TEXT CHECK (" + EVALUATION_TYPE + " IN ('Assignment', 'Lab', 'Quiz', 'Midterm', 'Final exam', 'Homework', 'Behavior')), " +
                EVALUATION_PERCENTAGE + " DECIMAL(5,2) NOT NULL, " +
                "FOREIGN KEY(" + EVALUATION_CLASS_ID + ") REFERENCES " + TABLE_CLASS + "(" + CLASS_ID + "))";
        String createStudentEvaluationTable = "CREATE TABLE IF NOT EXISTS " + TABLE_STUDENT_EVALUATION + " (" +
                STUDENT_EVALUATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                EVALUATION_STUDENT_ID + " INTEGER, " +
                EVALUATION_ID_REF + " INTEGER, " +
                GRADE + " DECIMAL(5,2) NOT NULL, " +
                "FOREIGN KEY(" + EVALUATION_STUDENT_ID + ") REFERENCES " + TABLE_STUDENT + "(" + STUDENT_ID + "), " +
                "FOREIGN KEY(" + EVALUATION_ID_REF + ") REFERENCES " + TABLE_EVALUATION + "(" + EVALUATION_ID + "))";
        String createDailyRecordsTable = "CREATE TABLE IF NOT EXISTS DailyRecords (" +
                "RecordID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "StudentID INTEGER NOT NULL, " +
                "ClassID INTEGER NOT NULL, " +
                "Date TEXT NOT NULL, " +
                "TimeSlot TEXT DEFAULT 'Default', " +
                "AttendanceMarked INTEGER DEFAULT 0, " +
                "GoodBehaviorMarked INTEGER DEFAULT 0, " +
                "NoHomeworkMarked INTEGER DEFAULT 0, " +
                "LateMarked INTEGER DEFAULT 0, " +
                "DisruptionMarked INTEGER DEFAULT 0, " +
                "FOREIGN KEY(StudentID) REFERENCES " + TABLE_STUDENT + "(StudentID), " +
                "FOREIGN KEY(ClassID) REFERENCES " + TABLE_CLASS + "(ClassID))";
        db.execSQL(createTeacherTable);
        db.execSQL(createClassTable);
        db.execSQL(createStudentTable);
        db.execSQL(createScheduleTable);
        db.execSQL(createEvaluationTable);
        db.execSQL(createStudentEvaluationTable);
        db.execSQL(createDailyRecordsTable);
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_daily_records_student ON DailyRecords(StudentID)");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_daily_records_class ON DailyRecords(ClassID)");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_daily_records_date ON DailyRecords(Date)");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_daily_records_timeslot ON DailyRecords(TimeSlot)");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_evaluation_class ON " + TABLE_EVALUATION + "(" + EVALUATION_CLASS_ID + ")");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_student_evaluation_student ON " + TABLE_STUDENT_EVALUATION + "(" + EVALUATION_STUDENT_ID + ")");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_student_evaluation_evaluation ON " + TABLE_STUDENT_EVALUATION + "(" + EVALUATION_ID_REF + ")");
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
        db.execSQL("DROP TABLE IF EXISTS DailyRecords");
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
                {"Alice", "Johnson", "1", "Alice.Johnson@example.com", "555-111-1111"},
                {"Bob", "Smith", "1", "Bob.Smith@example.com", "555-222-2222"},
                {"Cameron", "White", "1", "Cameron.White@example.com", "555-333-3333"},
                {"Diana", "Hall", "1", "Diana.Hall@example.com", "555-444-4444"},
                {"Ethan", "Young", "1", "Ethan.Young@example.com", "555-555-5555"},
                {"Fiona", "Green", "1", "Fiona.Green@example.com", "555-666-6666"},
                {"George", "King", "1", "George.King@example.com", "555-777-7777"},
                {"Hannah", "Carter", "1", "Hannah.Carter@example.com", "555-888-8888"},
                {"Ian", "Baker", "1", "Ian.Baker@example.com", "555-999-9999"},
                {"Jasmine", "Clark", "1", "Jasmine.Clark@example.com", "555-000-0000"},
                {"Kyle", "Adams", "1", "Kyle.Adams@example.com", "555-111-2222"},
                {"Lara", "Thompson", "1", "Lara.Thompson@example.com", "555-222-3333"},
                {"Mason", "Scott", "1", "Mason.Scott@example.com", "555-333-4444"},
                {"Natalie", "Nelson", "1", "Natalie.Nelson@example.com", "555-444-5555"},
                {"Oscar", "Ward", "1", "Oscar.Ward@example.com", "555-555-6666"},
                {"Charlie", "Davis", "2", "Charlie.Davis@example.com", "555-666-7777"},
                {"David", "Evans", "2", "David.Evans@example.com", "555-777-8888"},
                {"John", "Doe", "2", "John.Doe@example.com", "555-888-9999"},
                {"Jane", "Smith", "2", "Jane.Smith@example.com", "555-999-0000"},
                {"Max", "Brown", "2", "Max.Brown@example.com", "555-000-1111"},
                {"Lucy", "Johnson", "2", "Lucy.Johnson@example.com", "555-111-2222"},
                {"Oliver", "Taylor", "2", "Oliver.Taylor@example.com", "555-222-3333"},
                {"Sophia", "Anderson", "2", "Sophia.Anderson@example.com", "555-333-4444"},
                {"Jackson", "Lee", "2", "Jackson.Lee@example.com", "555-444-5555"},
                {"Mia", "Moore", "2", "Mia.Moore@example.com", "555-555-6666"},
                {"Isabella", "Harris", "2", "Isabella.Harris@example.com", "555-666-7777"},
                {"Liam", "Clark", "2", "Liam.Clark@example.com", "555-777-8888"},
                {"Emma", "Lewis", "2", "Emma.Lewis@example.com", "555-888-9999"},
                {"Noah", "King", "2", "Noah.King@example.com", "555-999-0000"},
                {"Ava", "Young", "2", "Ava.Young@example.com", "555-000-1111"},
                {"Eve", "Brown", "3", "Eve.Brown@example.com", "555-111-2222"},
                {"Frank", "Wilson", "3", "Frank.Wilson@example.com", "555-222-3333"},
                {"Henry", "Moore", "3", "Henry.Moore@example.com", "555-333-4444"},
                {"Ella", "White", "3", "Ella.White@example.com", "555-444-5555"},
                {"Lucas", "Hall", "3", "Lucas.Hall@example.com", "555-555-6666"},
                {"Zoe", "King", "3", "Zoe.King@example.com", "555-666-7777"},
                {"Mason", "Green", "3", "Mason.Green@example.com", "555-777-8888"},
                {"Carter", "Scott", "3", "Carter.Scott@example.com", "555-888-9999"},
                {"Grace", "Clark", "3", "Grace.Clark@example.com", "555-999-0000"},
                {"Chloe", "Adams", "3", "Chloe.Adams@example.com", "555-000-1111"},
                {"Ella", "Baker", "3", "Ella.Baker@example.com", "555-111-2222"},
                {"James", "Jackson", "3", "James.Jackson@example.com", "555-222-3333"},
                {"Scarlett", "Thompson", "3", "Scarlett.Thompson@example.com", "555-333-4444"},
                {"Aria", "Nelson", "3", "Aria.Nelson@example.com", "555-444-5555"},
                {"Michael", "Ward", "3", "Michael.Ward@example.com", "555-555-6666"},
                {"Grace", "Taylor", "4", "Grace.Taylor@example.com", "555-666-7777"},
                {"Henry", "Moore", "4", "Henry.Moore@example.com", "555-777-8888"},
                {"Ella", "White", "4", "Ella.White@example.com", "555-888-9999"},
                {"Liam", "Brown", "4", "Liam.Brown@example.com", "555-999-0000"},
                {"Emily", "Davis", "4", "Emily.Davis@example.com", "555-000-1111"},
                {"Matthew", "Johnson", "4", "Matthew.Johnson@example.com", "555-111-2222"},
                {"Amelia", "Garcia", "4", "Amelia.Garcia@example.com", "555-222-3333"},
                {"Benjamin", "Martinez", "4", "Benjamin.Martinez@example.com", "555-333-4444"},
                {"Charlotte", "Hernandez", "4", "Charlotte.Hernandez@example.com", "555-444-5555"},
                {"Oliver", "Lopez", "4", "Oliver.Lopez@example.com", "555-555-6666"},
                {"Sophia", "Gonzalez", "4", "Sophia.Gonzalez@example.com", "555-666-7777"},
                {"Lucas", "Wilson", "4", "Lucas.Wilson@example.com", "555-777-8888"},
                {"Isabella", "Anderson", "4", "Isabella.Anderson@example.com", "555-888-9999"},
                {"Jack", "Thomas", "4", "Jack.Thomas@example.com", "555-999-0000"},
                {"Aiden", "Robinson", "4", "Aiden.Robinson@example.com", "555-000-1111"},
                {"Isabella", "Anderson", "5", "Isabella.Anderson@example.com", "555-111-2222"},
                {"Jack", "Thomas", "5", "Jack.Thomas@example.com", "555-222-3333"},
                {"Ava", "Taylor", "5", "Ava.Taylor@example.com", "555-333-4444"},
                {"Ethan", "Jones", "5", "Ethan.Jones@example.com", "555-444-5555"},
                {"Sophia", "Brown", "5", "Sophia.Brown@example.com", "555-555-6666"},
                {"Liam", "Williams", "5", "Liam.Williams@example.com", "555-666-7777"},
                {"Mia", "Davis", "5", "Mia.Davis@example.com", "555-777-8888"},
                {"Zoe", "Garcia", "5", "Zoe.Garcia@example.com", "555-888-9999"},
                {"Max", "Martinez", "5", "Max.Martinez@example.com", "555-999-0000"},
                {"Emily", "Rodriguez", "5", "Emily.Rodriguez@example.com", "555-000-1111"},
                {"Jacob", "Wilson", "5", "Jacob.Wilson@example.com", "555-111-2222"},
                {"Charlotte", "Lopez", "5", "Charlotte.Lopez@example.com", "555-222-3333"},
                {"Amelia", "Gonzalez", "5", "Amelia.Gonzalez@example.com", "555-333-4444"},
                {"William", "Hernandez", "5", "William.Hernandez@example.com", "555-444-5555"},
                {"Elijah", "Lee", "5", "Elijah.Lee@example.com", "555-555-6666"},
                {"Kathy", "Martin", "6", "Kathy.Martin@example.com", "555-666-7777"},
                {"Leo", "Jackson", "6", "Leo.Jackson@example.com", "555-777-8888"},
                {"Sofia", "Thompson", "6", "Sofia.Thompson@example.com", "555-888-9999"},
                {"Daniel", "Clark", "6", "Daniel.Clark@example.com", "555-999-0000"},
                {"Olivia", "King", "6", "Olivia.King@example.com", "555-000-1111"},
                {"Alexander", "Scott", "6", "Alexander.Scott@example.com", "555-111-2222"},
                {"Avery", "Young", "6", "Avery.Young@example.com", "555-222-3333"},
                {"Grace", "Harris", "6", "Grace.Harris@example.com", "555-333-4444"},
                {"Harper", "Lewis", "6", "Harper.Lewis@example.com", "555-444-5555"},
                {"Scarlett", "Robinson", "6", "Scarlett.Robinson@example.com", "555-555-6666"},
                {"Aria", "Walker", "6", "Aria.Walker@example.com", "555-666-7777"},
                {"Layla", "Hall", "6", "Layla.Hall@example.com", "555-777-8888"},
                {"Gavin", "Allen", "6", "Gavin.Allen@example.com", "555-888-9999"},
                {"Chloe", "Ward", "6", "Chloe.Ward@example.com", "555-999-0000"},
                {"Aiden", "Jackson", "6", "Aiden.Jackson@example.com", "555-000-1111"},
                {"Mia", "Harris", "7", "Mia.Harris@example.com", "555-111-2222"},
                {"Noah", "Lewis", "7", "Noah.Lewis@example.com", "555-222-3333"},
                {"Olivia", "Walker", "7", "Olivia.Walker@example.com", "555-333-4444"},
                {"Aiden", "Adams", "7", "Aiden.Adams@example.com", "555-444-5555"},
                {"Layla", "Baker", "7", "Layla.Baker@example.com", "555-555-6666"},
                {"Ella", "Hernandez", "7", "Ella.Hernandez@example.com", "555-666-7777"},
                {"Sophia", "Thompson", "7", "Sophia.Thompson@example.com", "555-777-8888"},
                {"James", "Jackson", "7", "James.Jackson@example.com", "555-888-9999"},
                {"Lucas", "Green", "7", "Lucas.Green@example.com", "555-999-0000"},
                {"Abigail", "Carter", "7", "Abigail.Carter@example.com", "555-000-1111"},
                {"Grace", "Scott", "7", "Grace.Scott@example.com", "555-111-2222"},
                {"Benjamin", "Hall", "7", "Benjamin.Hall@example.com", "555-222-3333"},
                {"Isabella", "Young", "7", "Isabella.Young@example.com", "555-333-4444"},
                {"Daniel", "Clark", "7", "Daniel.Clark@example.com", "555-444-5555"},
                {"Zoe", "Nelson", "7", "Zoe.Nelson@example.com", "555-555-6666"}
        };
        for (String[] student : students) {
            ContentValues studentValues = new ContentValues();
            studentValues.put(STUDENT_FIRST_NAME, student[0]);
            studentValues.put(STUDENT_LAST_NAME, student[1]);
            studentValues.put(STUDENT_CLASS_ID, student[2]);
            studentValues.put(STUDENT_EMAIL, student[3]);
            studentValues.put(STUDENT_PHONE, student[4]);
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


    }
    public long addEvaluation(int classId, String evalName, String evalType, double evalPercentage) {
        ContentValues values = new ContentValues();
        values.put(EVALUATION_CLASS_ID, classId);
        values.put(EVALUATION_NAME, evalName);
        values.put(EVALUATION_TYPE, evalType);
        values.put(EVALUATION_PERCENTAGE, evalPercentage);
        return getWritableDatabase().insert(TABLE_EVALUATION, null, values);
    }
    public void recordStudentGrade(int studentId, int evaluationId, double grade) {
        ContentValues values = new ContentValues();
        values.put(EVALUATION_STUDENT_ID, studentId);
        values.put(EVALUATION_ID_REF, evaluationId);
        values.put(GRADE, grade);

        getWritableDatabase().insert(TABLE_STUDENT_EVALUATION, null, values);
    }
    public int updateEvaluation(String oldEvalName, int classId, String newEvalName, String evalType, double evalPercentage) {
        ContentValues values = new ContentValues();
        values.put(EVALUATION_CLASS_ID, classId);
        values.put(EVALUATION_NAME, newEvalName);
        values.put(EVALUATION_TYPE, evalType);
        values.put(EVALUATION_PERCENTAGE, evalPercentage);

        return getWritableDatabase().update(
                TABLE_EVALUATION,
                values,
                EVALUATION_NAME + " = ? AND " + EVALUATION_CLASS_ID + " = ?",
                new String[]{oldEvalName, String.valueOf(classId)}
        );
    }
    public void updateStudentGrades(int studentId, int evaluationId, double grade) {
        ContentValues values = new ContentValues();
        values.put(GRADE, grade);

        getWritableDatabase().update(
                TABLE_STUDENT_EVALUATION,
                values,
                EVALUATION_STUDENT_ID + " = ? AND " + EVALUATION_ID_REF + " = ?",
                new String[]{String.valueOf(studentId), String.valueOf(evaluationId)}
        );
    }
    public int updateEvaluation(int evaluationId, String name, String type, double percentage) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EVALUATION_NAME, name);
        values.put(EVALUATION_TYPE, type);
        values.put(EVALUATION_PERCENTAGE, percentage);
        return db.update(
                TABLE_EVALUATION,
                values,
                EVALUATION_ID + " = ?",
                new String[]{String.valueOf(evaluationId)}
        );
    }

    public void updateOrInsertStudentGrade(int studentId, int evaluationId, double grade) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EVALUATION_STUDENT_ID, studentId);
        values.put(EVALUATION_ID_REF, evaluationId);
        values.put(GRADE, grade);
        int rowsAffected = db.update(
                TABLE_STUDENT_EVALUATION,
                values,
                EVALUATION_STUDENT_ID + " = ? AND " + EVALUATION_ID_REF + " = ?",
                new String[]{String.valueOf(studentId), String.valueOf(evaluationId)}
        );
        if (rowsAffected == 0) {
            db.insert(TABLE_STUDENT_EVALUATION, null, values);
        }
    }


}
