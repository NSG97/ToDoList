package com.example.nishantgahlawat.todolist;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NewToDo extends AppCompatActivity {

    EditText titleET;
    EditText descriptionET;
    EditText DatePickerET;
    EditText TimePickerET;
    Button clearReminderButton;
    Calendar reminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_to_do);

        reminder = Calendar.getInstance();

        titleET = (EditText)findViewById(R.id.NewTitleET);
        descriptionET = (EditText)findViewById(R.id.NewDescriptionET);

        clearReminderButton = (Button)findViewById(R.id.ClearReminderButton);

        DatePickerET = (EditText)findViewById(R.id.DatePickerET);
        DatePickerET.setInputType(InputType.TYPE_NULL);
        TimePickerET = (EditText)findViewById(R.id.TimePickerET);
        TimePickerET.setInputType(InputType.TYPE_NULL);

        DatePickerET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(NewToDo.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        reminder.set(year,month,dayOfMonth);
                        DatePickerET.setText(new SimpleDateFormat("dd-MM-yyyy").format(reminder.getTime()));
                    }
                },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        TimePickerET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                TimePickerDialog timePickerDialog = new TimePickerDialog(NewToDo.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour_of_day, int minute) {
                        reminder.set(Calendar.HOUR_OF_DAY,hour_of_day);
                        reminder.set(Calendar.MINUTE,minute);
                        TimePickerET.setText(new SimpleDateFormat("hh:mm a").format(reminder.getTime()));
                    }
                },calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false);
                timePickerDialog.show();
            }
        });

        clearReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerET.setText("");
                TimePickerET.setText("");
                reminder=Calendar.getInstance();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.addtodo_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.AddNewToDo:
                String newTitle = titleET.getText().toString().trim();
                if (newTitle.equals("")){
                    titleET.setError("This field can not be blank.");
                    return true;
                }
                String newDescription = descriptionET.getText().toString().trim();

                long currentTime = System.currentTimeMillis();

                ToDoOpenHelper toDoOpenHelper = ToDoOpenHelper.getToDoOpenHelperInstance(this);
                SQLiteDatabase sqLiteDatabase = toDoOpenHelper.getWritableDatabase();

                ContentValues contentValues = new ContentValues();
                contentValues.put(ToDoOpenHelper.TODO_TITLE,newTitle);
                contentValues.put(ToDoOpenHelper.TODO_DESCRIPTION,newDescription);
                contentValues.put(ToDoOpenHelper.TODO_DONE,0);
                contentValues.put(ToDoOpenHelper.TODO_CREATED,currentTime);

                long reminderInMillis;

                if(DatePickerET.getText().toString().equals("")){
                    if(TimePickerET.getText().toString().equals("")){
                        reminderInMillis=-1;
                        contentValues.put(ToDoOpenHelper.TODO_REMINDER,-1);
                    }
                    else {
                        Toast.makeText(this,"Reminder Date Set To Today",Toast.LENGTH_SHORT).show();
                        reminderInMillis=reminder.getTimeInMillis();
                        contentValues.put(ToDoOpenHelper.TODO_REMINDER,reminder.getTimeInMillis());
                    }
                }
                else{
                    if(TimePickerET.getText().toString().equals("")){
                        Toast.makeText(this,"Reminder Time Set To Midnight",Toast.LENGTH_SHORT).show();
                        reminder.set(Calendar.HOUR_OF_DAY,0);
                        reminder.set(Calendar.MINUTE,0);
                        reminderInMillis=reminder.getTimeInMillis();
                        contentValues.put(ToDoOpenHelper.TODO_REMINDER,reminder.getTimeInMillis());
                    }
                    else {
                        reminderInMillis=reminder.getTimeInMillis();
                        contentValues.put(ToDoOpenHelper.TODO_REMINDER,reminder.getTimeInMillis());
                    }
                }

                long id = sqLiteDatabase.insert(ToDoOpenHelper.TODO_TABLE_NAME,null,contentValues);

                ToDoItem toDoItem = new ToDoItem(id,newTitle,newDescription,false,currentTime);
                toDoItem.setReminder(reminderInMillis);

                Intent intent = new Intent();
                intent.putExtra(IntentConstraints.NewToDoExtra,toDoItem);
                setResult(RESULT_OK,intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
