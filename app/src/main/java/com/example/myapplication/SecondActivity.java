package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.databinding.ActivityMainBinding;

public class SecondActivity extends AppCompatActivity {

    public static TextView goals;

    public static Button mButton;
    public static EditText mEdit;
    public static int goalInput;

    //public static int steps = MainActivity.stepDetect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        binding = SecondActivity.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
        setContentView(R.layout.secondactivity);


        goals = findViewById(R.id.goals_display);

        mButton = (Button)findViewById(R.id.button1);
        mEdit = (EditText) findViewById(R.id.editText1);

    }
    @Override
    protected void onResume() {

        super.onResume();
        setGoalInput();
        //goals.setText("Your Daily Step Goal = " + goalInput);
        //goalsMet();

    }

    public void setGoalInput() {
       //binding.resetButton.setOnClickListener(new View.OnClickListener()
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                goalInput = Integer.parseInt(mEdit.getText().toString());
                goals.setText("Your Daily Step Goal = " + goalInput);
                Toast.makeText(getApplicationContext(), "Goal is set!", Toast.LENGTH_LONG).show();
                Intent mIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mIntent);
            }
        });
    }

//    public void goalsMet() {
//        if((goalInput == MainActivity.Pedometer)&&(goalInput != 0)) {
//            Toast.makeText(getApplicationContext(), "Congratulations! You've met your daily goal!", Toast.LENGTH_LONG).show();
//        }
//    }
}