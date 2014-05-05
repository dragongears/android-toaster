package com.dragongears.centurion.app;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends ActionBarActivity {
    private BluetoothAdapter mBTAdapter;
    private BluetoothSocket mBTSocket;
    private BluetoothDevice mDevice;
    private UUID mDeviceUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // Standard SPP UUID

    private static final int BT_ENABLE_REQUEST = 10; // This is the code we use for BT Enable
    private ProgressDialog progressDialog;
    private boolean mIsBluetoothConnected = false;
    private boolean mIsUserInitiatedDisconnect = false;
    private ReadInput mReadThread = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Button toastButton;
        setContentView(R.layout.activity_main);

        Log.e("Centurion", "Bluetooth getDefaultAdapter");
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBTAdapter == null) {
            Log.e("Centurion", "Bluetooth not found");
        } else if (!mBTAdapter.isEnabled()) {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, BT_ENABLE_REQUEST);
            Log.e("Centurion", "Getting paired devices");
        } else {
            new SearchDevices().execute();
            Log.e("Centurion", "Search devices");
        }

        toastButton = (Button)findViewById(R.id.toastButton);

        // Define and attach listeners
        toastButton.setOnClickListener(new View.OnClickListener()  {
            public void onClick(View v) {
                try {
                    mBTSocket.getOutputStream().write("t".getBytes());
                    ImageView img= (ImageView) findViewById(R.id.toasterImageView);
                    img.setImageResource(R.drawable.ic_toast_down);
                    } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    protected void onPause() {
        if (mBTSocket != null && mIsBluetoothConnected) {
            new DisConnectBT().execute();
        }
        Log.d("Centurion", "Paused");
        super.onPause();
    }

    @Override
    protected void onResume() {
//        if (mBTSocket == null || !mIsBluetoothConnected) {
//            new ConnectBT().execute();
//        }
        Log.d("Centurion", "Resumed");
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.d("Centurion", "Stopped");
        super.onStop();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Searches for paired devices. Doesn't do a scan! Only devices which are paired through Settings->Bluetooth
     * will show up with this. I didn't see any need to re-build the wheel over here
     * @author ryder
     *
     */
    private class SearchDevices extends AsyncTask<Void, Void, BluetoothDevice> {

        @Override
        protected BluetoothDevice doInBackground(Void... params) {
            BluetoothDevice dev = null;
            Set<BluetoothDevice> pairedDevices = mBTAdapter.getBondedDevices();
            for (BluetoothDevice device : pairedDevices) {
                Log.e("Centurion", "Device: " + device.getName());
                if (device.getName().contains("RN42-049C")) {
                    dev = device;
                }
            }
            return dev;

        }

        @Override
        protected void onPostExecute(BluetoothDevice dev) {
            mDevice = dev;

            if (dev != null) {
                if (mBTSocket == null || !mIsBluetoothConnected) {
                    new ConnectBT().execute();
                }

                Log.e("Centurion", "Toaster found");
            } else {
                Log.e("Centurion", "Toaster not found");
            }
        }
    }

    private class DisConnectBT extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {

            if (mReadThread != null) {
                mReadThread.stop();
                while (mReadThread.isRunning())
                    ; // Wait until it stops
                mReadThread = null;

            }

            try {
                mBTSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mIsBluetoothConnected = false;
            if (mIsUserInitiatedDisconnect) {
                finish();
            }
        }

    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean mConnectSuccessful = true;

        @Override
        protected void onPreExecute() {
            Log.e("Centurion", "Connecting...");
            progressDialog = ProgressDialog.show(MainActivity.this, "Hold on", "Connecting");// http://stackoverflow.com/a/11130220/1287554
        }

        @Override
        protected Void doInBackground(Void... devices) {

            try {
                if (mBTSocket == null || !mIsBluetoothConnected) {
                    mBTSocket = mDevice.createInsecureRfcommSocketToServiceRecord(mDeviceUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    mBTSocket.connect();
                }
            } catch (IOException e) {
                // Unable to connect to device
                e.printStackTrace();
                mConnectSuccessful = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            ImageView img = (ImageView) findViewById(R.id.toasterImageView);

            if (!mConnectSuccessful) {
                img.setImageResource(R.drawable.ic_no_connection);

                Toast.makeText(MainActivity.this, "Could not connect to device. Is it a Serial device? Also check if the UUID is correct in the settings", Toast.LENGTH_LONG).show();
            } else {
                img.setImageResource(R.drawable.ic_toast_up);

                Toast.makeText(MainActivity.this, "Connected to device", Toast.LENGTH_LONG).show();
                mIsBluetoothConnected = true;
                mReadThread = new ReadInput(); // Kick off input reader
            }

            Log.e("Centurion", "Connected...");
            progressDialog.dismiss();
        }

    }

    private class ReadInput implements Runnable {

        private boolean bStop = false;
        private Thread t;

        public ReadInput() {
            t = new Thread(this, "Input Thread");
            t.start();
        }

        public boolean isRunning() {
            return t.isAlive();
        }

        @Override
        public void run() {
            InputStream inputStream;

            try {
                inputStream = mBTSocket.getInputStream();
                while (!bStop) {
                    byte[] buffer = new byte[256];
                    if (inputStream.available() > 0) {
                        inputStream.read(buffer);
                        int i = 0;
						/*
						 * This is needed because new String(buffer) is taking the entire buffer i.e. 256 chars on Android 2.3.4 http://stackoverflow.com/a/8843462/1287554
						 */
                        for (i = 0; i < buffer.length && buffer[i] != 0; i++) {
                        }
                        final String strInput = new String(buffer, 0, i);

						/*
						 * If checked then receive text, better design would probably be to stop thread if unchecked and free resources, but this is a quick fix
						 */

//                        if (chkReceiveText.isChecked()) {
//                            mTxtReceive.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    mTxtReceive.append(strInput);
//                                    //Uncomment below for testing
//                                    //mTxtReceive.append("\n");
//                                    //mTxtReceive.append("Chars: " + strInput.length() + " Lines: " + mTxtReceive.getLineCount() + "\n");
//
//                                    int txtLength = mTxtReceive.getEditableText().length();
//                                    if(txtLength > mMaxChars){
//                                        mTxtReceive.getEditableText().delete(0, txtLength - mMaxChars);
//                                    }
//
//                                    if (chkScroll.isChecked()) { // Scroll only if this is checked
//                                        scrollView.post(new Runnable() { // Snippet from http://stackoverflow.com/a/4612082/1287554
//                                            @Override
//                                            public void run() {
//                                                scrollView.fullScroll(View.FOCUS_DOWN);
//                                            }
//                                        });
//                                    }
//                                }
//                            });
//                        }

                    }
                    Thread.sleep(500);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        public void stop() {
            bStop = true;
        }

    }


}
