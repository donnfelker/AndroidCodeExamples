package com.example.mappers;

public interface IJsonMapper<TOutput> {
    TOutput mapFrom(String json);
}
