package com.example.nishantgahlawat.todolist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Nishant Gahlawat on 30-06-2017.
 */

public class ToDoOpenHelper extends SQLiteOpenHelper {

    public static final String TODO_TABLE_NAME = "ToDoList";
    public static final String TODO_ID = "ID";
    public static final String TODO_TITLE = "Title";
    public static final String TODO_DESCRIPTION = "Description";
    public static final String TODO_CREATED = "Created";
    public static final String TODO_DONE = "Done";

    private static ToDoOpenHelper toDoOpenHelper;

    public static ToDoOpenHelper getToDoOpenHelperInstance(Context context){
        if(toDoOpenHelper==null)
            toDoOpenHelper = new ToDoOpenHelper(context);
        return toDoOpenHelper;
    }

    private ToDoOpenHelper(Context context) {
        super(context, "ToDos.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE " + TODO_TABLE_NAME + "(" + TODO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                TODO_TITLE + " TEXT, " + TODO_DESCRIPTION + " TEXT, " + TODO_CREATED + " INTEGER, " +
                TODO_DONE + " INTEGER DEFAULT 0);";
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
