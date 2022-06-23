package com.dev175.pebble.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.dev175.pebble.Model.Notification;
import com.dev175.pebble.R;
import java.util.ArrayList;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.MyViewHolder> {

    private ArrayList<Notification> notificationArrayList ;
    private Context context;


    public NotificationsAdapter(ArrayList<Notification> notificationArrayList, Context context) {
        this.notificationArrayList = notificationArrayList;
        this.context = context;

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_notification,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        TextView title = holder.title;
        TextView message = holder.message;

        title.setText(notificationArrayList.get(position).getTitle());
        message.setText(notificationArrayList.get(position).getMessage());

    }

    @Override
    public int getItemCount()
    {
        return notificationArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView message;

        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            this.title = itemView.findViewById(R.id.title);
            this.message = itemView.findViewById(R.id.message);
        }
    }
}
