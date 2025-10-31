package com.example.school;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HomePage extends AppCompatActivity {
    Button btnSchedule, btnStud, btnEval, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnSchedule = findViewById(R.id.btnSchedule);
        btnStud = findViewById(R.id.btnStud);
        btnEval = findViewById(R.id.btnEval);
        btnLogout = findViewById(R.id.btnLogout);

        btnSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, SchedulePage.class);
                startActivity(intent);
            }
        });

        btnStud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, FindStudentPage.class);
                startActivity(intent);
            }
        });

        btnEval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, EvaluationsPage.class);
                startActivity(intent);
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}