package com.example.textedd.data;

import android.util.Log;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Converters {
    private static final String TAG = "Conventers";
    @TypeConverter
    public static String toStringArrayList(ArrayList<String> value) {
        return value == null ? null : new Gson().toJson(value);
    }

    @TypeConverter
    public static ArrayList<String> fromStringArrayList(String value) {
        Type listType = new TypeToken<ArrayList<String>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String ListToString(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            sb.append(s);
            sb.append(" ,");
        }
        Log.d(TAG, "List was converted to String" + sb);
        return sb.toString();
    }

    @TypeConverter
    public static List<String> StringToList(String value) {
        if (value == null)
            return null;
        //Gson gson = new Gson();
        //List<String> myList = Lists.newArrayList(Splitter.on(" , ").split(string));
        //List<String> myList = new ArrayList<String>(Arrays.asList(value.split("\\s*, \\s*")));
        //Type listType = new TypeToken<List<String>>() {}.getType();
        //myList = gson.fromJson(value, listType);
        Log.d(TAG, "String " + value + " was converted to List");
        return Arrays.asList(value.split(" ,"));
    }

    @TypeConverter
    public static File StringToFile(String value) {
        Log.d(TAG, "String was converted to File");
        return value == null ? null : new File(value);
    }

    @TypeConverter
    public static String FileToString(File value) {
        Log.d(TAG, "File was converted to String");
        return value == null ? null : value.toString();
    }
}
