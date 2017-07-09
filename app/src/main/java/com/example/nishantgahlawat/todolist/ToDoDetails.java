package com.example.nishantgahlawat.todolist;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.text.TimeZoneFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class ToDoDetails extends AppCompatActivity {

    TextView timeTextView;
    ImageButton doneButton;
    EditText titleET;
    EditText descriptionET;
    EditText DatePickerET;
    EditText TimePickerET;
    Button clearReminderButton;

    int position;
    ToDoItem toDoItem;
    Calendar newReminder;
    boolean reminderChanged=false;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_details);

        timeTextView = (TextView)findViewById(R.id.DetailsTimeTextView);
        doneButton = (ImageButton)findViewById(R.id.DetailsDoneStatusButton);
        clearReminderButton = (Button)findViewById(R.id.DetailsClearReminderButton);

        titleET = (EditText)findViewById(R.id.DetailsTitleEditText);
        descriptionET = (EditText)findViewById(R.id.DetailsDescriptionTextView);
        DatePickerET = (EditText)findViewById(R.id.DetailsDatePickerET);
        DatePickerET.setInputType(InputType.TYPE_NULL);
        TimePickerET = (EditText)findViewById(R.id.DetailsTimePickerET);
        TimePickerET.setInputType(InputType.TYPE_NULL);

        Intent intent = getIntent();

        position = intent.getIntExtra(IntentConstraints.DetailsPositionExtra,-1);
        toDoItem = (ToDoItem) intent.getSerializableExtra(IntentConstraints.DetailsToDoExtra);


        newReminder = Calendar.getInstance();
        newReminder.setTimeInMillis(toDoItem.getReminder()==-1?toDoItem.getReminder():System.currentTimeMillis());
        newReminder.set(Calendar.MILLISECOND,0);
        newReminder.set(Calendar.SECOND,0);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd - hh:mm a");
        String time = simpleDateFormat.format(new Date(toDoItem.getCreated()));

        timeTextView.setText(time);

        if(toDoItem.hasReminder()){
            DatePickerET.setText(new java.text.SimpleDateFormat("dd-MM-yyyy").format(toDoItem.getReminder()));
            TimePickerET.setText(new java.text.SimpleDateFormat("hh:mm a").format(toDoItem.getReminder()));
        }

        doneButton.setBackgroundResource(toDoItem.isDone()?android.R.drawable.checkbox_on_background:android.R.drawable.checkbox_off_background);

        titleET.setText(toDoItem.getTitle());
        descriptionET.setText(toDoItem.getDescription());

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toDoItem.toggleDone();
                doneButton.setBackgroundResource(toDoItem.isDone()?android.R.drawable.checkbox_on_background:android.R.drawable.checkbox_off_background);
            }
        });

        DatePickerET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(ToDoDetails.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        newReminder.set(year,month,dayOfMonth);
                        DatePickerET.setText(new java.text.SimpleDateFormat("dd-MM-yyyy").format(newReminder.getTime()));
                        reminderChanged=true;
                    }
                },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        TimePickerET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                TimePickerDialog timePickerDialog = new TimePickerDialog(ToDoDetails.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour_of_day, int minute) {
                        newReminder.set(Calendar.HOUR_OF_DAY,hour_of_day);
                        newReminder.set(Calendar.MINUTE,minute);
                        TimePickerET.setText(new java.text.SimpleDateFormat("hh:mm a").format(newReminder.getTime()));
                        reminderChanged=true;
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
                reminderChanged=true;
                newReminder=Calendar.getInstance();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detailstodo_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.EditToDo:
                String newTitle = titleET.getText().toString().trim();
                if (newTitle.equals("")){
                    titleET.setError("This field can not be blank.");
                    return true;
                }
                String newDescription = descriptionET.getText().toString().trim();

                toDoItem.setTitle(newTitle);
                toDoItem.setDescription(newDescription);

                if(reminderChanged){
                    if(DatePickerET.getText().toString().equals("")){
                        if(TimePickerET.getText().toString().equals("")){
                            toDoItem.setReminder(-1);
                        }
                        else {
                            Toast.makeText(this,"Reminder Date Set To Today",Toast.LENGTH_SHORT).show();
                            toDoItem.setReminder(newReminder.getTimeInMillis());
                        }
                    }
                    else{
                        if(TimePickerET.getText().toString().equals("")){
                            Toast.makeText(this,"Reminder Time Set To Midnight",Toast.LENGTH_SHORT).show();
                            newReminder.set(Calendar.HOUR_OF_DAY,0);
                            newReminder.set(Calendar.MINUTE,0);
                            toDoItem.setReminder(newReminder.getTimeInMillis());
                        }
                        else {
                            toDoItem.setReminder(newReminder.getTimeInMillis());
                        }
                    }
                }
                if(position!=-1){
                    Intent intent = new Intent();
                    intent.putExtra(IntentConstraints.DetailsPositionExtra,position);
                    intent.putExtra(IntentConstraints.DetailsToDoExtra,toDoItem);
                    intent.putExtra(IntentConstraints.DetailsReminderChanged,reminderChanged);
                    setResult(RESULT_OK,intent);
                    finish();
                }
                else{
                    Intent intent = new Intent(ToDoDetails.this,MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(IntentConstraints.DetailsToDoExtra,toDoItem);
                    intent.putExtra(IntentConstraints.DetailsReminderChanged,reminderChanged);
                    startActivity(intent);
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
