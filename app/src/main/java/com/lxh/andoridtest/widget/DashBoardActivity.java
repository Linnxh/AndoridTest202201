package com.lxh.andoridtest.widget;

import android.graphics.RectF;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.lxh.andoridtest.R;

import java.util.Random;

public class DashBoardActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        final DashBoard d = (DashBoard) findViewById(R.id.dash);


        RectF rect = new RectF(-200, -200, 200, 200);

//        findViewById(R.id.testlxh).setBackground(rect);


        findViewById(R.id.rand).setOnClickListener(view -> {
            int max = 100;
            int min = 1;
            Random random = new Random();
            int p = random.nextInt(max) % (max - min + 1) + min;
            d.cgangePer(p);
        });

        findViewById(R.id.retu).setOnClickListener(view -> d.cgangePer(0));
    }

}