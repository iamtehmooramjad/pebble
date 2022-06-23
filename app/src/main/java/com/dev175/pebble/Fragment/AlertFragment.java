package com.dev175.pebble.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dev175.pebble.R;
import com.google.android.material.textfield.TextInputEditText;

import static com.dev175.pebble.Model.BluetoothConnection.mConnectedThread;


public class AlertFragment extends Fragment {

    TextInputEditText alertEditText;
    Button alertBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alert,container,false);
        alertEditText = view.findViewById(R.id.alertEditText);
        alertBtn = view.findViewById(R.id.alertBtn);
        alertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String alertMessage = alertEditText.getText().toString();
                alertEditText.setText("");
                if(mConnectedThread != null) //First check to make sure thread created
                    {
                        mConnectedThread.write(alertMessage);
                        Toast.makeText(getContext(), "Alert sent Successfully", Toast.LENGTH_SHORT).show();
                    }
                else 
                {
                    Toast.makeText(getContext(), "Please connect with your device", Toast.LENGTH_SHORT).show();
                }

            }
        });
        return view;
    }

}






