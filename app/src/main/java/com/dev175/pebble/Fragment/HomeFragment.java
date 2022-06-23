package com.dev175.pebble.Fragment;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dev175.pebble.Activity.HomeActivity;
import com.dev175.pebble.Adapter.PairedDeviceAdapter;
import com.dev175.pebble.Model.BluetoothConnection;
import com.dev175.pebble.Model.Device;
import com.dev175.pebble.Model.TableControllerDevice;
import com.dev175.pebble.Model.ViewAnimation;
import com.dev175.pebble.R;
import java.util.ArrayList;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private RecyclerView devicesRecyclerView;
    TextView connectTv;
    private Switch bluetoothSwitch;
    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names

    public static BluetoothConnection bluetoothConnection;

    //Expandable
    private ImageButton bt_toggle;
    private View lyt_expand;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        init(view);

        bluetoothConnection = new BluetoothConnection((HomeActivity) getActivity(),0);
        //Scan devices
        connectTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanDevices(v);
            }
        });
        //Setting Switch on/off based on bluetooth
        if (!bluetoothConnection.getmBTAdapter().isEnabled())
        {
            bluetoothSwitch.setChecked(false);
        }
        else
        {
            bluetoothSwitch.setChecked(true);
            setStateOn();
        }


        bluetoothSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    bluetoothOn();
                }
                else {
                    bluetoothOff();
                }
            }
        });


        ListView mDevicesListView = (ListView) view.findViewById(R.id.devicesListView);
        mDevicesListView.setAdapter(bluetoothConnection.getmBTArrayAdapter()); // assign model to view
        mDevicesListView.setOnItemClickListener(bluetoothConnection.mDeviceClickListener);


        try {
            // Ask for location permission if not already allowed
            if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
        catch (Exception e)
        {
            Log.e(TAG, "onCreateView: "+e.getMessage() );
        }

        readRecords();
        return view;
    }

    private void init(View view) {
        devicesRecyclerView = view.findViewById(R.id.devicesRecyclerView);
        connectTv = view.findViewById(R.id.connectTv);

        // text section
        bt_toggle = (ImageButton)view.findViewById(R.id.bt_toggle_text);

        lyt_expand = (View)view.findViewById(R.id.lyt_expand_text);
        lyt_expand.setVisibility(View.GONE);

        bluetoothSwitch = view.findViewById(R.id.bluetoothSwitch);


        bt_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSectionText(bt_toggle);
            }
        });


    }
    private void toggleSectionText(View view) {
        boolean show = toggleArrow(view);
        if (show) {
            ViewAnimation.expand(lyt_expand, new ViewAnimation.AnimListener() {
                @Override
                public void onFinish() {
                //    nestedScrollTo(nested_scroll_view, lyt_expand_text);
                }
            });
        } else {
            ViewAnimation.collapse(lyt_expand);
        }

    }


    public boolean toggleArrow(View view) {
        if (view.getRotation() == 0) {
            view.animate().setDuration(200).rotation(180);
            return true;
        } else {
            view.animate().setDuration(200).rotation(0);
            return false;
        }
    }


    private void bluetoothOn(){
        if (!bluetoothConnection.getmBTAdapter().isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }
    private void bluetoothOff(){
        bluetoothConnection.disableBtAdapter(); // turn off
        Toast.makeText(getContext(),"Bluetooth turned Off", Toast.LENGTH_SHORT).show();
        setStateOff();
    }

    private void setStateOff(){

        SharedPreferences.Editor editor = requireActivity().getSharedPreferences("save", MODE_PRIVATE).edit();
        editor.putBoolean("value", false);
        editor.apply();
        bluetoothSwitch.setChecked(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent Data) {
        // Check which request we're responding to
        super.onActivityResult(requestCode, resultCode, Data);
        if (requestCode == REQUEST_ENABLE_BT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                //When switch is checked
                setStateOn();
            }
            else {
                setStateOff();
            }
        }
    }

    private void setStateOn()
    {
        SharedPreferences.Editor editor = requireActivity().getSharedPreferences("save", MODE_PRIVATE).edit();
        editor.putBoolean("value", true);
        editor.apply();
        bluetoothSwitch.setChecked(true);
    }



    public void readRecords() {
        ArrayList<Device> deviceArrayList = new TableControllerDevice(getContext()).read();
        PairedDeviceAdapter pairedDeviceAdapter = new PairedDeviceAdapter(deviceArrayList, (HomeActivity) getActivity());
        GridLayoutManager gridLayoutManager= new GridLayoutManager(getContext(),1,GridLayoutManager.HORIZONTAL,false);
        devicesRecyclerView.setLayoutManager(gridLayoutManager);
        devicesRecyclerView.setAdapter(pairedDeviceAdapter);
        devicesRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    public void scanDevices(View view) {
        discover();
    }


    private void discover(){
        // Check if the device is already discovering
        if(bluetoothConnection.getmBTAdapter().isDiscovering()){
            bluetoothConnection.cancelDiscovery();
            Toast.makeText(getContext(),"Discovery stopped",Toast.LENGTH_SHORT).show();
        }
        else{
            if(bluetoothConnection.getmBTAdapter().isEnabled()) {
               bluetoothConnection.clearBtArrayAdapterItems();
                bluetoothConnection.startDiscovery();
                Toast.makeText(getContext(), "Discovery started", Toast.LENGTH_SHORT).show();
                requireActivity().registerReceiver(blReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            }
            else{
                Toast.makeText(getContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
            }
        }
    }

    BroadcastReceiver blReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name to the list
               try {
                   bluetoothConnection.addDevice(device.getName() , device.getAddress());
               }
               catch (Exception e)
               {
                   Log.e(TAG, "onReceive: "+e.getMessage() );
               }

            }
        }
    };







}
