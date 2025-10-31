package com.example.school;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class StudentListAdapter extends SimpleAdapter {
    private final Context context;
    private final ArrayList<HashMap<String, String>> data;

    public StudentListAdapter(Context context, ArrayList<HashMap<String, String>> data,
                              String[] from, int[] to) {
        super(context, data, R.layout.student_list_item, from, to);
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.student_list_item, parent, false);
            holder = new ViewHolder();
            holder.studentName = convertView.findViewById(R.id.studentName);
            holder.checkAttendance = convertView.findViewById(R.id.checkAttendance);
            holder.checkBehavior = convertView.findViewById(R.id.checkBehavior);
            holder.checkHomework = convertView.findViewById(R.id.checkHomework);
            holder.checkLate = convertView.findViewById(R.id.checkLate);
            holder.checkDisruption = convertView.findViewById(R.id.checkDisruption);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        HashMap<String, String> item = data.get(position);
        String studentId = item.get("studentId");
        holder.studentName.setText(item.get("name"));
        ClassManagePage activity = (ClassManagePage) context;

        holder.checkAttendance.setOnCheckedChangeListener(null);
        holder.checkBehavior.setOnCheckedChangeListener(null);
        holder.checkHomework.setOnCheckedChangeListener(null);
        holder.checkLate.setOnCheckedChangeListener(null);
        holder.checkDisruption.setOnCheckedChangeListener(null);
        holder.checkAttendance.setChecked(activity.checkStates.getOrDefault(studentId + "_attendance", false));
        holder.checkBehavior.setChecked(activity.checkStates.getOrDefault(studentId + "_behavior", false));
        holder.checkHomework.setChecked(activity.checkStates.getOrDefault(studentId + "_homework", false));
        holder.checkLate.setChecked(activity.checkStates.getOrDefault(studentId + "_late", false));
        holder.checkDisruption.setChecked(activity.checkStates.getOrDefault(studentId + "_disruption", false));
        setupCheckBox(holder.checkAttendance, studentId + "_attendance", activity);
        setupCheckBox(holder.checkBehavior, studentId + "_behavior", activity);
        setupCheckBox(holder.checkHomework, studentId + "_homework", activity);
        setupCheckBox(holder.checkLate, studentId + "_late", activity);
        setupCheckBox(holder.checkDisruption, studentId + "_disruption", activity);
        return convertView;
    }

    private void setupCheckBox(CheckBox checkBox, String key, ClassManagePage activity) {
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) ->
                activity.checkStates.put(key, isChecked));
    }
    private static class ViewHolder {
        TextView studentName;
        CheckBox checkAttendance;
        CheckBox checkBehavior;
        CheckBox checkHomework;
        CheckBox checkLate;
        CheckBox checkDisruption;
    }
}