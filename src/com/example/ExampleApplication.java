package com.example;

import com.google.inject.Module;
import roboguice.application.RoboApplication;

import java.util.List;

public class ExampleApplication extends RoboApplication {

    @Override
    protected void addApplicationModules(List<Module> modules) {
        modules.add( new ExampleModule() );
    }
}
