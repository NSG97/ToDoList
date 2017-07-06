package com.example.nishantgahlawat.todolist;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.text.TimeZoneFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Date;

public class ToDoDetails extends AppCompatActivity {

    TextView timeTextView;
    ImageButton doneButton;
    EditText titleET;
    EditText descriptionET;

    int position;
    ToDoItem toDoItem;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_details);

        timeTextView = (TextView)findViewById(R.id.DetailsTimeTextView);
        doneButton = (ImageButton)findViewById(R.id.DetailsDoneStatusButton);
        titleET = (EditText)findViewById(R.id.DetailsTitleEditText);
        descriptionET = (EditText)findViewById(R.id.DetailsDescriptionTextView);

        Intent intent = getIntent();

        position = intent.getIntExtra(IntentConstraints.DetailsPositionExtra,-1);
        toDoItem = (ToDoItem) intent.getSerializableExtra(IntentConstraints.DetailsToDoExtra);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd - hh:mm a");
        String time = simpleDateFormat.format(new Date(toDoItem.getCreated()));

        timeTextView.setText(time);

        doneButton.setBackgroundResource(toDoItem.isDone()?R.drawable.checkedbox:R.drawable.uncheckedbox);

        titleET.setText(toDoItem.getTitle());
        descriptionET.setText(toDoItem.getDescription());

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toDoItem.toggleDone();
                doneButton.setBackgroundResource(toDoItem.isDone()?R.drawable.checkedbox:R.drawable.uncheckedbox);
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
                Intent intent = new Intent();
                toDoItem.setTitle(newTitle);
                toDoItem.setDescription(newDescription);
                intent.putExtra(IntentConstraints.DetailsPositionExtra,position);
                intent.putExtra(IntentConstraints.DetailsToDoExtra,toDoItem);
                setResult(RESULT_OK,intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
