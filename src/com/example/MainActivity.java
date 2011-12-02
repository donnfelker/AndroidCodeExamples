package com.example;

import android.os.Bundle;
import android.widget.TextView;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

public class MainActivity extends RoboActivity
{

    @InjectView(R.id.exampleText) protected TextView exampleText;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        exampleText.setText( R.string.friend );

    }
}
