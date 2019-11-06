package com.example.assignment2;

import android.content.Context;
import android.media.Image;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.w3c.dom.Text;

import java.util.ArrayList;

class TasksListAdapter extends ArrayAdapter<Task> {

    private Context mContext;
    int mResource;
    DataManager mDataManager;

    public TasksListAdapter(@NonNull Context context, int resource, ArrayList<Task> objects, DataManager dataManager) {
        super(context, resource, objects);

        mContext = context;
        mResource = resource;
        mDataManager = dataManager;

    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        int taskID = getItem(position).getTaskID();
        String taskDesc = getItem(position).getTaskDesc();
        String taskDeadline = getItem(position).getDeadline();
        int clientID = getItem(position).getClientID();
        String clientName = getItem(position).getClientName();
        String clientAddress = getItem(position).getClientAddress();
        String clientBirthDate = getItem(position).getClientBirthDate();
        String clientPhotoURL = getItem(position).getClientPhotoURL();

        Task task = new Task(
                taskID, taskDesc, taskDeadline, clientID, clientName, clientAddress, clientBirthDate, clientPhotoURL
        );

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView description = (TextView) convertView.findViewById(R.id.description);
        description.setTag(taskID);
        TextView deadline = (TextView) convertView.findViewById(R.id.deadline);
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView address = (TextView) convertView.findViewById(R.id.address);
        TextView birthdate = (TextView) convertView.findViewById(R.id.birthdate);

        ImageView photo = (ImageView) convertView.findViewById(R.id.imageView);

        description.setText(taskDesc);
        deadline.setText(taskDeadline);
        name.setText(clientName);
        address.setText(clientAddress);
        birthdate.setText(clientBirthDate);

        photo.setImageResource( R.drawable.usericon );


        return convertView;

    }
}
