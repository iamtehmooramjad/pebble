package com.dev175.pebble.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.dev175.pebble.Activity.HomeActivity;
import com.dev175.pebble.Model.BluetoothConnection;
import com.dev175.pebble.Model.Device;
import com.dev175.pebble.Model.TableControllerDevice;
import com.dev175.pebble.R;
import java.util.ArrayList;
import static android.content.Context.MODE_PRIVATE;

public class PairedDeviceAdapter extends RecyclerView.Adapter<PairedDeviceAdapter.MyViewHolder> {

    private static final String TAG = "PairedDeviceAdapter";
    private ArrayList<Device> deviceArrayList;
    private HomeActivity context;

    public PairedDeviceAdapter(ArrayList<Device> deviceArrayList, HomeActivity context) {
        this.deviceArrayList = deviceArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_paired_device,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        ImageView caseImg = holder.caseImg;
        TextView pebbleTv = holder.pebbleTv;
        TextView deviceTv = holder.deviceTv;
        boolean state = holder.state;
        TextView address = holder.address;

        if (deviceArrayList.get(position).getAddress().equals(HomeActivity.selectedDeviceMAC) && HomeActivity.selectedDeviceState==true)
        {
            caseImg.setImageResource(R.drawable.caselogoon);
        }
        else
        {
            caseImg.setImageResource(R.drawable.caselogo);
        }
        address.setText(deviceArrayList.get(position).getAddress());
        caseImg.setTag(holder);


        deviceTv.setText(deviceArrayList.get(position).getName());
        holder.setListeners();
    }

    @Override
    public int getItemCount()
    {
        return deviceArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, PopupMenu.OnMenuItemClickListener, View.OnClickListener {

        CardView cardView;
        ImageView caseImg;
        TextView pebbleTv;
        TextView deviceTv;
        boolean state = false;
        TextView address;

        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            this.cardView = itemView.findViewById(R.id.cardview);
            this.caseImg = itemView.findViewById(R.id.caseImg);
            this.pebbleTv = itemView.findViewById(R.id.pebbleTv);
            this.deviceTv = itemView.findViewById(R.id.deviceTv);
            this.address = itemView.findViewById(R.id.address);
        }

        public void setListeners()
        {
            cardView.setOnLongClickListener(this);
            cardView.setOnClickListener(this);
        }


        @Override
        public boolean onLongClick(View v) {
            showPopupMenu(v);
            return false;
        }

        private void showPopupMenu(View v) {
            PopupMenu popupMenu = new PopupMenu(context,v);
            popupMenu.inflate(R.menu.deletepopup_menu);
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        }


        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId())
            {
                case R.id.removeDevice:
                {
                    //Remove device from database
                    String deviceName = deviceArrayList.get(getAdapterPosition()).getName().trim();
                    boolean isDeleted = new TableControllerDevice(context).delete(deviceArrayList.get(getAdapterPosition()).getAddress());
                    if (isDeleted)
                    {
                        if (deviceArrayList.get(getAdapterPosition()).getAddress()==HomeActivity.selectedDeviceMAC)
                        {
                            HomeActivity.selectedDeviceMAC = "";
                            HomeActivity.selectedDeviceState=false;
                        }

                        Toast.makeText(context, deviceName+" removed Successfully", Toast.LENGTH_SHORT).show();
                        deviceArrayList.remove(getAdapterPosition());

                        notifyDataSetChanged();
                    }
                    else
                    {
                        Toast.makeText(context, "Failed to remove device", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                default:
                    return false;
            }
        }

        @Override
        public void onClick(View v)
        {

            final PairedDeviceAdapter.MyViewHolder  myViewHolder = (PairedDeviceAdapter.MyViewHolder)((ImageView)itemView.findViewById(R.id.caseImg)).getTag();

            String name = myViewHolder.deviceTv.getText().toString();
            String address = myViewHolder.address.getText().toString();

            if (!address.equals(HomeActivity.selectedDeviceMAC) && HomeActivity.selectedDeviceMAC!="")
            {
                Toast.makeText(context, "First disconnect your device", Toast.LENGTH_SHORT).show();
                return;
            }
            
            BluetoothConnection bluetoothConnection = null;

            //Connecting with device
            HomeActivity.selectedDeviceMAC = address;
            Log.d("TAG", "onClick HomeActivity.selectedDeviceMAC: "+name + " "+address);

            Device deviceObj = new Device();
            deviceObj.setName(name);
            deviceObj.setAddress(address);

            BluetoothConnection.mConnectedThread = null;
            //flag = 0; because the data is already stored
            bluetoothConnection= new BluetoothConnection(context,1);
            bluetoothConnection.setSelectedDevice(deviceObj);
            bluetoothConnection.onClickPairedDevice(name,address);

            if (HomeActivity.selectedDeviceState==false)
            {


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (HomeActivity.selectedDeviceState)
                        {
                            myViewHolder.state = true;
                            Toast.makeText(context, "Connection successfull", Toast.LENGTH_SHORT).show();
                            myViewHolder.caseImg.setImageResource(R.drawable.caselogoon);
                            Log.d(TAG, "onClick: myViewHolder.state = "+myViewHolder.state);
                        }
                    }
                }, 10000);

            }
            else
            {
                myViewHolder.state = false;
                myViewHolder.caseImg.setImageResource(R.drawable.caselogo);

                //Add here code to turn off pairing with the device
                try
                {
                    BluetoothConnection.mConnectedThread.cancel();
                }
                catch (Exception e)
                {
                    Log.e("TAG", "onClick: "+e.getMessage() );
                }

                Log.d(TAG, "onClick: myViewHolder.state = "+myViewHolder.state);
            }
        }
    }
}
