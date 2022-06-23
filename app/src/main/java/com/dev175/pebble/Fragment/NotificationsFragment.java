package com.dev175.pebble.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.dev175.pebble.Adapter.NotificationsAdapter;
import com.dev175.pebble.Model.Notification;
import com.dev175.pebble.Model.TableControllerNotification;
import com.dev175.pebble.R;
import java.util.ArrayList;

public class NotificationsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications,container,false);

        RecyclerView notificationRecyclerView = view.findViewById(R.id.notificationRecyclerView);
        ArrayList<Notification> notificationsList = new TableControllerNotification(getContext()).readNotifications();
        if (notificationsList.size()==0)
        {
            Notification notification = new Notification();
            notification.setMessage("");
            notification.setTitle("Notifications not found");
            notificationsList.add(notification);
        }
//        Collections.reverse(notificationsList);
        NotificationsAdapter notificationsAdapter = new NotificationsAdapter(notificationsList, getContext());
        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        notificationRecyclerView.setAdapter(notificationsAdapter);

        return view;
    }

}
