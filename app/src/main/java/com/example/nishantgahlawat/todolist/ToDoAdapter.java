package com.example.nishantgahlawat.todolist;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Nishant Gahlawat on 29-06-2017.
 */

public class ToDoAdapter extends ArrayAdapter {

    ArrayList<ToDoItem> toDoArrayList;
    Context context;
    ToDoButtonListener toDoButtonListener;

    public ToDoAdapter(@NonNull Context context, ArrayList<ToDoItem> toDos) {
        super(context, 0);
        this.context = context;
        this.toDoArrayList = toDos;
    }

    @Override
    public int getCount() {
        return toDoArrayList.size();
    }

    public void setToDoButtonListener(ToDoButtonListener toDoButtonListener){
        this.toDoButtonListener = toDoButtonListener;
    }

    static class toDoViewHolder{
        TextView toDoTV;
        ImageButton completedButton;

        public toDoViewHolder(TextView toDoTV, ImageButton completedButton) {
            this.toDoTV = toDoTV;
            this.completedButton = completedButton;
        }
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        toDoViewHolder tdVH;
        if(convertView==null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.todo_list_item, null);
            TextView toDoTV = (TextView) convertView.findViewById(R.id.toDoTV);
            ImageButton completedButton = (ImageButton) convertView.findViewById(R.id.completedButton);
            tdVH = new toDoViewHolder(toDoTV, completedButton);
            convertView.setTag(tdVH);
        }else{
            tdVH = (toDoViewHolder)convertView.getTag();
        }
        ToDoItem tdI = toDoArrayList.get(position);
        tdVH.toDoTV.setText(tdI.getTitle());
        tdVH.completedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(toDoButtonListener!=null)
                    toDoButtonListener.onButtonClickListener(position);
            }
        });
        tdVH.completedButton.setBackgroundResource(tdI.isDone()?android.R.drawable.checkbox_on_background:android.R.drawable.checkbox_off_background);
        return convertView;
    }

    public interface ToDoButtonListener{
        public void onButtonClickListener(int position);
    }
}
