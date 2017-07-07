package com.example.nishantgahlawat.todolist;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ToDoAdapter.ToDoButtonListener {

    final static int NEW_TODO = 1;
    final static int DETAILS_TODO = 2;

    ListView listView;
    ArrayList<ToDoItem> toDoItemArrayList;
    ToDoAdapter toDoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = (ListView)findViewById(R.id.ToDoListView);

        toDoItemArrayList = new ArrayList<ToDoItem>();

        toDoAdapter = new ToDoAdapter(this,toDoItemArrayList);

        listView.setAdapter(toDoAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this,ToDoDetails.class);
                intent.putExtra(IntentConstraints.DetailsPositionExtra,i);
                intent.putExtra(IntentConstraints.DetailsToDoExtra, toDoItemArrayList.get(i));
                startActivityForResult(intent,DETAILS_TODO);
            }
        });

        listView.isLongClickable();

        listView.setOnItemLongClickListener(new toDoItemLongClick());

        toDoAdapter.setToDoButtonListener(this);

        updateToDoList();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,NewToDo.class);
                startActivityForResult(intent,NEW_TODO);
            }
        });

        Intent notifIntent = getIntent();
        ToDoItem notifToDoItem = (ToDoItem) notifIntent.getSerializableExtra(IntentConstraints.NotificationToDoExtra);

        if(notifToDoItem!=null){
            Intent intent = new Intent(MainActivity.this,ToDoDetails.class);
            intent.putExtra(IntentConstraints.DetailsPositionExtra,toDoItemArrayList.indexOf(notifToDoItem));
            intent.putExtra(IntentConstraints.DetailsToDoExtra, notifToDoItem);
            startActivityForResult(intent,DETAILS_TODO);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case NEW_TODO:
                if(resultCode==RESULT_OK){
                    ToDoItem toDoItem = (ToDoItem) data.getSerializableExtra(IntentConstraints.NewToDoExtra);

                    toDoItemArrayList.add(toDoItem);
                    toDoAdapter.notifyDataSetChanged();

                    if(toDoItem.hasReminder()){
                        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

                        Intent intent = new Intent(MainActivity.this,AlarmReceiver.class);
                        intent.putExtra(IntentConstraints.NotificationToDoExtra,toDoItem);

                        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,(int)toDoItem.getId(),intent,PendingIntent.FLAG_UPDATE_CURRENT);

                        alarmManager.setExact(AlarmManager.RTC,toDoItem.getReminder(),pendingIntent);
                    }
                }
                break;
            case DETAILS_TODO:
                if(resultCode==RESULT_OK){
                    ToDoItem toDoItem = (ToDoItem)data.getSerializableExtra(IntentConstraints.DetailsToDoExtra);
                    int position = data.getIntExtra(IntentConstraints.DetailsPositionExtra,-1);

                    ToDoItem toDoItemToChange = toDoItemArrayList.get(position);
                    toDoItemToChange.setTitle(toDoItem.getTitle());
                    toDoItemToChange.setDescription(toDoItem.getDescription());
                    toDoItemToChange.setDone(toDoItem.isDone());
                    toDoItemToChange.setReminder(toDoItem.getReminder());

                    toDoAdapter.notifyDataSetChanged();

                    ToDoOpenHelper toDoOpenHelper = ToDoOpenHelper.getToDoOpenHelperInstance(this);
                    SQLiteDatabase sqLiteDatabase = toDoOpenHelper.getReadableDatabase();

                    String selection = ToDoOpenHelper.TODO_ID+"="+toDoItem.getId();

                    ContentValues cv = new ContentValues();
                    cv.put(ToDoOpenHelper.TODO_TITLE,toDoItem.getTitle());
                    cv.put(ToDoOpenHelper.TODO_DESCRIPTION,toDoItem.getDescription());
                    cv.put(ToDoOpenHelper.TODO_DONE,toDoItem.isDone()?1:0);
                    cv.put(ToDoOpenHelper.TODO_REMINDER,toDoItem.getReminder());

                    sqLiteDatabase.update(ToDoOpenHelper.TODO_TABLE_NAME,cv,selection,null);

                    boolean reminderChanged = data.getBooleanExtra(IntentConstraints.DetailsReminderChanged,false);
                    if(reminderChanged){
                        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

                        Intent intent = new Intent(MainActivity.this,AlarmReceiver.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,(int)toDoItem.getId(),intent,PendingIntent.FLAG_UPDATE_CURRENT);

                        alarmManager.cancel(pendingIntent);

                        intent = new Intent(MainActivity.this,AlarmReceiver.class);
                        intent.putExtra(IntentConstraints.NotificationToDoExtra,toDoItem);
                        pendingIntent = PendingIntent.getBroadcast(MainActivity.this,(int)toDoItem.getId(),intent,PendingIntent.FLAG_UPDATE_CURRENT);

                        alarmManager.setExact(AlarmManager.RTC,toDoItem.getReminder(),pendingIntent);
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void updateToDoList() {
        toDoItemArrayList.clear();
        ToDoOpenHelper toDoOpenHelper = ToDoOpenHelper.getToDoOpenHelperInstance(this);
        SQLiteDatabase sqLiteDatabase = toDoOpenHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(ToDoOpenHelper.TODO_TABLE_NAME,null,null,null,null,null,null);
        while (cursor.moveToNext()){
            String title = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TODO_TITLE));
            String description = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TODO_DESCRIPTION));
            long id = cursor.getLong(cursor.getColumnIndex(ToDoOpenHelper.TODO_ID));
            long created = cursor.getLong(cursor.getColumnIndex(ToDoOpenHelper.TODO_CREATED));
            boolean done = (cursor.getInt(cursor.getColumnIndex(ToDoOpenHelper.TODO_DONE))==0)?false:true;
            long reminder = cursor.getInt(cursor.getColumnIndex(ToDoOpenHelper.TODO_REMINDER));

            ToDoItem toDoItem = new ToDoItem(id,title,description,done,created);
            toDoItem.setReminder(reminder);

            toDoItemArrayList.add(toDoItem);
        }
        cursor.close();
        toDoAdapter.notifyDataSetChanged();
    }

    @Override
    public void onButtonClickListener(int position) {
        ToDoItem toDoItem = toDoItemArrayList.get(position);
        toDoItem.toggleDone();
        toDoAdapter.notifyDataSetChanged();

        ToDoOpenHelper toDoOpenHelper = ToDoOpenHelper.getToDoOpenHelperInstance(this);
        SQLiteDatabase sqLiteDatabase = toDoOpenHelper.getReadableDatabase();

        String selection = ToDoOpenHelper.TODO_ID+"="+toDoItem.getId();

        ContentValues cv = new ContentValues();
        cv.put(ToDoOpenHelper.TODO_DONE,toDoItem.isDone()?1:0);

        sqLiteDatabase.update(ToDoOpenHelper.TODO_TABLE_NAME,cv,selection,null);
    }

    private class toDoItemLongClick implements AdapterView.OnItemLongClickListener{

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view,final int position, long id) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            builder.setTitle("Delete");
            builder.setMessage("Are You Sure?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ToDoItem toDoItem = toDoItemArrayList.get(position);

                    ToDoOpenHelper toDoOpenHelper = ToDoOpenHelper.getToDoOpenHelperInstance(MainActivity.this);
                    SQLiteDatabase sqLiteDatabase = toDoOpenHelper.getReadableDatabase();

                    String selection = ToDoOpenHelper.TODO_ID+"="+toDoItem.getId();

                    sqLiteDatabase.delete(ToDoOpenHelper.TODO_TABLE_NAME,selection,null);

                    if(toDoItem.hasReminder()){
                        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

                        Intent intent = new Intent(MainActivity.this,AlarmReceiver.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,(int)toDoItem.getId(),intent,PendingIntent.FLAG_UPDATE_CURRENT);

                        alarmManager.cancel(pendingIntent);
                    }

                    MainActivity.this.toDoItemArrayList.remove(toDoItem);
                    MainActivity.this.toDoAdapter.notifyDataSetChanged();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }
    }
}
