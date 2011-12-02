package com.example;

import com.example.mappers.IJsonMapper;
import com.example.mappers.JsonMapper;
import com.example.models.Foo;
import com.google.inject.TypeLiteral;
import roboguice.config.AbstractAndroidModule;

public class ExampleModule extends AbstractAndroidModule {


    @Override
    protected void configure() {

        // Singletons
        bind( new TypeLiteral<IJsonMapper< Foo >>(){} ).to( new TypeLiteral<JsonMapper<Foo>>(){} ).asEagerSingleton();

    }
}
