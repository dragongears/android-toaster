package com.dragongears.centurion.app;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;


public class MainActivity extends ActionBarActivity {
    private BluetoothSPP mBTSPP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Button toastButton;
        setContentView(R.layout.activity_main);

        mBTSPP = new BluetoothSPP(this);

        toastButton = (Button)findViewById(R.id.toastButton);

        // Define and attach listeners
        toastButton.setOnClickListener(new View.OnClickListener()  {
            public void onClick(View v) {
                try {
                    mBTSPP.write("t");
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

}
