package com.ocado.basket.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.List;

public class JsonUtils {
    private static final Gson gson = new Gson();

    // Method for parsing JSON to a map of lists
    public static Map<String, List<String>> parseJsonToMap(String json) {
        Type type = new TypeToken<Map<String, List<String>>>() {}.getType();
        return gson.fromJson(json, type);
    }

    // Method for converting an object to JSON string
    public static String toJson(Object object) {
        return gson.toJson(object);
    }

    // Method for loading an object from JSON contained in a file
    public static <T> T readJsonFromFile(String filePath, Class<T> classOfT) {
        try (FileReader reader = new FileReader(filePath)) { // 'filePath' should be a string path
            return gson.fromJson(reader, classOfT);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read JSON from file: " + filePath, e);
        }
    }

}
