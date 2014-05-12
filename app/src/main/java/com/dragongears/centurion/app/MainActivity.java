package com.dragongears.centurion.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;


public class MainActivity extends ActionBarActivity {
    final Context context = this;
    private BluetoothSPP mBTSPP;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Button toastButton;
        setContentView(R.layout.activity_main);

        mBTSPP = new BluetoothSPP();
        mBTSPP.initialize(this, messageHandler);

        toastButton = (Button)findViewById(R.id.toastButton);

        // Define and attach listeners
        toastButton.setOnClickListener(new View.OnClickListener()  {
            public void onClick(View v) {
                try {
                    mBTSPP.write("t");
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    protected void onPause() {
        mBTSPP.disconnect();
        super.onPause();
    }

    @Override
    protected void onResume() {
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

    private Handler messageHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            ImageView img = (ImageView) findViewById(R.id.toasterImageView);

            if (msg.what == BluetoothSPP.BT_CONNECTING) {
                progressDialog = ProgressDialog.show(context, "Bluetooth SPP", "Connecting");// http://stackoverflow.com/a/11130220/1287554
            } else if (msg.what == BluetoothSPP.BT_CONNECTED) {
                img.setImageResource(R.drawable.ic_toast_up);
                progressDialog.dismiss();
                Toast.makeText(context, "Connected to device", Toast.LENGTH_LONG).show();
            } else if (msg.what == BluetoothSPP.BT_NOT_CONNECTED) {
                img.setImageResource(R.drawable.ic_no_connection);
                progressDialog.dismiss();
                Toast.makeText(context, "Could not connect to device. Is it a Serial device? Also check if the UUID is correct in the settings", Toast.LENGTH_LONG).show();
            } else {
                Log.i("Main Activity", "From toaster: " + ((char)msg.arg1));
                char cmd = (char)msg.arg1;
                if (cmd == '+') {
                    img.setImageResource(R.drawable.ic_toast_down);
                } else if (cmd == '-') {
                    img.setImageResource(R.drawable.ic_toast_up);
                }
            }
        }
    };

}
