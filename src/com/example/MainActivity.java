package com.example;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

public class MainActivity extends RoboActivity
{

    @InjectView(R.id.exampleText) protected TextView exampleText;
    @InjectView(R.id.bubble_button) protected TextView bubbleButton;
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        exampleText.setText( R.string.friend );
        

        
        bubbleButton.setOnClickListener( new View.OnClickListener() {
            int pressedTimes = 0;
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Button was pressed! (" + (pressedTimes++) + ")", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
