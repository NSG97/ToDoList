package com.example.nishantgahlawat.todolist;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Nishant Gahlawat on 29-06-2017.
 */

public class ToDoItem implements Serializable{

    private long id;
    private String title;
    private String description;
    private boolean done;
    private long created;
    private long reminder;

    public ToDoItem(long id, String title, String description, boolean done, long created) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.done = done;
        this.created = created;

    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription(){
        return description;
    }

    public boolean isDone() {
        return done;
    }

    public long getCreated() {
        return created;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void toggleDone() {
        done = !done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public void setReminder(long reminder) {
        this.reminder = reminder;
    }

    public boolean hasReminder() {
        return (reminder==-1)?false:true;
    }

    public long getReminder() {
        return reminder;
    }
}
