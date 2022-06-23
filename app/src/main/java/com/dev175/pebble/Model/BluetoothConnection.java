package com.dev175.pebble.Model;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.dev175.pebble.Activity.HomeActivity;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.UUID;

public class BluetoothConnection extends Application {

    private static final String TAG = "BluetoothConnection";
    private ArrayAdapter<String> mBTArrayAdapter;
    private BluetoothAdapter mBTAdapter;
    private HomeActivity context;
    private Device selectedDevice;

    private Handler mHandler; // Our main handler that will receive callback notifications
    public static ConnectedThread mConnectedThread; // bluetooth background worker thread to send and receive data
    private BluetoothSocket mBTSocket = null; // bi-directional client-to-client data path

    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier

    // #defines for identifying shared types between calling functions
    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status
//    private final static int Count = 0; // used in bluetooth handler to identify message status


    public BluetoothConnection(final HomeActivity context, final int flag) {
        this.context = context;
        mBTAdapter = BluetoothAdapter.getDefaultAdapter(); // get a handle on the bluetooth radio
        mBTArrayAdapter = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1);

        mHandler = new Handler(){
            public void handleMessage(android.os.Message msg){
                if(msg.what == MESSAGE_READ){
                    String readMessage = null;
                    String k="";
                    try {

                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                        Log.d(TAG, "handleMessage: "+readMessage+"]");
                        int i=0;

                        while (Character.isLetterOrDigit(readMessage.charAt(i)) || readMessage.charAt(i)=='?'
                        || readMessage.charAt(i)==' ' || readMessage.charAt(i)=='.' || readMessage.charAt(i)==',')
                        {
                            k = k+readMessage.charAt(i);
                            i++;
                        }

                        Log.d(TAG, "newMessage : "+k);
                    }
                    catch (UnsupportedEncodingException e) {
                        Log.e(TAG, "handleMessageException: "+e.getMessage());
                    }
                    catch (Exception e) {
                        Log.e(TAG, "handleMessage: "+e.getMessage());
                    }

                    // mReadBuffer.setText(readMessage);
                    //for(int i=0;i < readMessage.length();i++);
                  try {
                     // String k=Integer.toString(Integer.parseInt(readMessage.replaceAll("[\\D]","")));
                      Log.d(TAG, "handleMessage: "+k);
                      //Notification
                      Notification notification = new Notification("Message from "+selectedDevice.getName(),k);
                      context.showNotification(context,notification);

                      boolean isNotificationStored = new TableControllerNotification(context).create(notification);
                      if (isNotificationStored)
                      {
                          Log.d("TAG", "onCreate: Notification Stored");
                      }
                  }
                  catch (Exception e)
                  {
                      Log.e(TAG, "handleMessage: "+e.getMessage() );
                  }

//                    String kStr = Integer.toString(k);

                }

                if(msg.what == CONNECTING_STATUS){
                    //Save clicked device in Sqlite db , So that we can show that device in HomeActivity as Paired Devices
                    //This code is needed to be execute when the device is successfully paired

                    //flag = 0 ; when clicked from discovery
                    //flag = 1 ; when clicked from saved devices
                    if (flag==0)
                    {
                        boolean createSuccessful = new TableControllerDevice(context).create(selectedDevice);
                        Log.d(TAG, "handleMessage: "+selectedDevice.getName());
                        Log.d(TAG, "handleMessage: "+selectedDevice.getAddress());
                        if(createSuccessful){
                            Toast.makeText(context, "Device saved", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(context, "Unable to save device information.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    if(msg.arg1 == 1)
                    {
                        //  mBluetoothStatus.setText("Connected to Device: " + (String)(msg.obj));
                        Toast.makeText(context, "Connecting to "+(String)(msg.obj), Toast.LENGTH_LONG).show();
                        HomeActivity.selectedDeviceState = true;
                        Toast.makeText(context, "Connecting...", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        if (flag==0)
                        {
                            Toast.makeText(context, "Trying to connect...", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(context, "Disconnecting...", Toast.LENGTH_LONG).show();
                        }
                        // mBluetoothStatus.setText("Connection Failed");
                        HomeActivity.selectedDeviceState = false;
                        HomeActivity.selectedDeviceMAC = "";
                        BluetoothConnection.mConnectedThread = null;
                        try {
                            mBTSocket.close();
                        } catch (IOException e) {
                            Log.e(TAG, "handleMessage: "+e.getMessage() );
                        }
                        catch (Exception e)
                        {
                            Log.e(TAG, "handleMessage: "+e.getMessage() );
                        }
                        if (flag==0)
                        {
                            Toast.makeText(context, "Connection Failed", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            }
        };
    }


    public AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

            if(!mBTAdapter.isEnabled()) {
                Toast.makeText(context, "Bluetooth not on", Toast.LENGTH_SHORT).show();
                return;
            }

            //mBluetoothStatus.setText("Connecting...");
            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            final String address = info.substring(info.length() - 17);
            final String name = info.substring(0,info.length() - 17);

            //Create Device Object
            Device deviceObj = new Device();
            deviceObj.setName(name);
            deviceObj.setAddress(address);
            selectedDevice = deviceObj;
            HomeActivity.selectedDeviceMAC = address;
            HomeActivity.selectedDeviceState = true;

            // Spawn a new thread to avoid blocking the GUI one
            new Thread()
            {
                public void run() {
                    boolean fail = false;

                    BluetoothDevice device = mBTAdapter.getRemoteDevice(address);

                    try {
                        mBTSocket = createBluetoothSocket(device);
                    } catch (IOException e) {
                        fail = true;
//                    Toast.makeText(getContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "run: "+"Socket creation failed");
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, "run: "+e.getMessage() );
                    }
                    // Establish the Bluetooth socket connection.
                    try {
                        mBTSocket.connect();
                    } catch (IOException e)
                    {
                        try
                        {
                            fail = true;
                            mBTSocket.close();
                            mHandler.obtainMessage(CONNECTING_STATUS, -1, -1)
                                    .sendToTarget();
                        }
                        catch (IOException e2) {
                            //insert code to deal with this
//                        Toast.makeText(getContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                            Log.d("TAG", "run: "+"Socket creation failed");
                        }
                        catch (Exception e3)
                        {
                            Log.e(TAG, "run: "+e3.getMessage() );
                        }
                    }
                    if(!fail)
                    {
                        mConnectedThread = new ConnectedThread(mBTSocket);
                        mConnectedThread.start();

                        mHandler.obtainMessage(CONNECTING_STATUS, 1, -1, name)
                                .sendToTarget();
                    }
                }
            }.start();

        }
    };


    private  BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, BTMODULEUUID);
        } catch (Exception e) {
            Log.e("TAG", "Could not create Insecure RFComm Connection",e);
        }
        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    public  class  ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e("TAG", "ConnectedThread: "+e.getMessage() );
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.available();
                    if(bytes != 0) {
                        buffer = new byte[1024];
                        SystemClock.sleep(100); //pause and wait for rest of data. Adjust this depending on your sending speed.
                        bytes = mmInStream.available(); // how many bytes are ready to be read?
                        bytes = mmInStream.read(buffer, 0, bytes); // record how many bytes we actually read
                        mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                                .sendToTarget(); // Send the obtained bytes to the UI activity
                    }
                } catch (IOException e) {
                    e.printStackTrace();

                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String input) {
            byte[] bytes = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(bytes);
            }
            catch (IOException e) {
                Log.e("TAG", "write: "+e.getMessage() );
            }
            catch (Exception e)
            {
                Log.e(TAG, "write: "+e.getMessage() );
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
                Toast.makeText(context, "Connection closed", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Log.e("TAG", "cancel: "+e.getMessage() );
            }
            catch (Exception e)
            {
                Log.e(TAG, "cancel: "+e.getMessage() );
            }
        }
    }

    public Device getSelectedDevice() {
        return selectedDevice;
    }

    public void setSelectedDevice(Device selectedDevice) {
        this.selectedDevice = selectedDevice;
    }

    public BluetoothAdapter getmBTAdapter()
    {
        return this.mBTAdapter;
    }

    public BluetoothSocket getmBTSocket() {
        return mBTSocket;
    }

    public ArrayAdapter<String> getmBTArrayAdapter() {
        return mBTArrayAdapter;
    }

    public void disableBtAdapter(){
        this.mBTAdapter.disable();
    }

    public void cancelDiscovery()
    {
        this.mBTAdapter.cancelDiscovery();
    }
    public void startDiscovery()
    {
        this.mBTAdapter.startDiscovery();
    }

    public void clearBtArrayAdapterItems()
    {
        this.mBTArrayAdapter.clear(); // clear items
    }
    public void addDevice(String name,String address)
    {
        this.mBTArrayAdapter.add(name+ "\n" + address);
        this.mBTArrayAdapter.notifyDataSetChanged();
    }

    public void onClickPairedDevice(final String name, final String address)
    {

        if(!mBTAdapter.isEnabled()) {
            Toast.makeText(context, "Bluetooth not on", Toast.LENGTH_SHORT).show();
            return;
        }
        // Spawn a new thread to avoid blocking the GUI one
        new Thread()
        {
            public void run() {
                boolean fail = false;

                BluetoothDevice device = mBTAdapter.getRemoteDevice(address);

                try {
                    mBTSocket = createBluetoothSocket(device);
                } catch (IOException e) {
                    fail = true;
//                    Toast.makeText(getContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                    Log.d("TAG", "run: "+"Socket creation failed");
                }
                // Establish the Bluetooth socket connection.
                try {
                    mBTSocket.connect();
                } catch (IOException e) {
                    try {
                        fail = true;
                        mBTSocket.close();
                        mHandler.obtainMessage(CONNECTING_STATUS, -1, -1)
                                .sendToTarget();
                    } catch (IOException e2) {
                        //insert code to deal with this
//                        Toast.makeText(getContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                        Log.d("TAG", "run: "+"Socket creation failed");
                    }
                }
                if(fail == false) {
                    mConnectedThread = new ConnectedThread(mBTSocket);
                    mConnectedThread.start();

                    mHandler.obtainMessage(CONNECTING_STATUS, 1, -1, name)
                            .sendToTarget();
                }
            }
        }.start();

    }
}
