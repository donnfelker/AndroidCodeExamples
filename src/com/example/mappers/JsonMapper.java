package com.example.mappers;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class JsonMapper<TOutput> implements IJsonMapper<TOutput> {

    public TOutput mapFrom(String json) {

        Type type = new TypeToken<TOutput>(){}.getType();
        return new Gson().fromJson( json, type );

    }

}
