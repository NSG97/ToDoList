package com.example.nishantgahlawat.todolist;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NewToDo extends AppCompatActivity {

    EditText titleET;
    EditText descriptionET;
    EditText DatePickerET;
    EditText TimePickerET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_to_do);

        titleET = (EditText)findViewById(R.id.NewTitleET);
        descriptionET = (EditText)findViewById(R.id.NewDescriptionET);
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
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year,month,dayOfMonth);
                        DatePickerET.setText(new SimpleDateFormat("dd-MM-yyyy").format(newDate.getTime()));
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
                        Calendar newTime = Calendar.getInstance();
                        newTime.set(Calendar.HOUR_OF_DAY,hour_of_day);
                        newTime.set(Calendar.MINUTE,minute);
                        TimePickerET.setText(new SimpleDateFormat("kk:mm a").format(newTime.getTime()));
                    }
                },calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false);
                timePickerDialog.show();
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
                Intent intent = new Intent();
                intent.putExtra(IntentConstraints.NewTitleExtra,newTitle);
                intent.putExtra(IntentConstraints.NewDescriptionExtra,newDescription);
                setResult(RESULT_OK,intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
