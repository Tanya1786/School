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
import android.content.ContentValues;
import java.util.List;
import java.util.Map;

public class StudentEvaluationsAdapter extends BaseAdapter {
    private Context context;
    private List<Map<String, String>> studentList;
    private OnGradeChangedListener onGradeChangedListener;

    public interface OnGradeChangedListener {
        void onGradeChanged(int position, String grade);
    }
    public StudentEvaluationsAdapter(Context context,
                                     List<Map<String, String>> studentList,
                                     OnGradeChangedListener listener) {
        this.context = context;
        this.studentList = studentList;
        this.onGradeChangedListener = listener;
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

    private static class ViewHolder {
        TextView studentName;
        EditText gradeEditText;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.student_evaluation_item, parent, false);
            holder = new ViewHolder();
            holder.studentName = convertView.findViewById(R.id.studentNameTextView);
            holder.gradeEditText = convertView.findViewById(R.id.gradeEditText);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Map<String, String> student = studentList.get(position);

        String fullName = student.get("FirstName") + " " + student.get("LastName");
        holder.studentName.setText(fullName);
        if (holder.gradeEditText.getTag() != null) {
            holder.gradeEditText.removeTextChangedListener((TextWatcher) holder.gradeEditText.getTag());
        }
        String grade = student.get("Grade");
        holder.gradeEditText.setText(grade != null ? grade : "");
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String gradeText = s.toString().trim();
                student.put("Grade", gradeText);

                if (onGradeChangedListener != null) {
                    onGradeChangedListener.onGradeChanged(position, gradeText);
                }
            }
        };
        holder.gradeEditText.addTextChangedListener(watcher);
        holder.gradeEditText.setTag(watcher);
        return convertView;
    }
}