package com.example.school;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class StudentEvaluationsAdapter extends BaseAdapter {
    private Context context;
    private List<Map<String, String>> studentList;
    private OnGradeChangedListener gradeChangedListener;

    public interface OnGradeChangedListener {
        void onGradeChanged(int position, String grade);
    }

    public StudentEvaluationsAdapter(Context context, List<Map<String, String>> studentList,
                                     OnGradeChangedListener listener) {
        this.context = context;
        this.studentList = studentList;
        this.gradeChangedListener = listener;
    }

    @Override
    public int getCount() {
        return studentList.size();
    }

    @Override
    public Object getItem(int position) {
        return studentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.student_evaluation_item, parent, false);
        }

        TextView studentNameTV = convertView.findViewById(R.id.studentNameTV);
        EditText gradeET = convertView.findViewById(R.id.gradeEditText);
        final Map<String, String> student = studentList.get(position);
        String studentName = student.get("FirstName") + " " + student.get("LastName");
        studentNameTV.setText(studentName);
        String existingGrade = student.get("Grade");
        if (existingGrade != null && !existingGrade.isEmpty()) {
            gradeET.setText(existingGrade);
        }
        gradeET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                student.put("Grade", s.toString());
                if (gradeChangedListener != null) {
                    gradeChangedListener.onGradeChanged(position, s.toString());
                }
            }
        });

        return convertView;
    }

    public List<Map<String, String>> getStudentList() {
        return studentList;
    }
}
