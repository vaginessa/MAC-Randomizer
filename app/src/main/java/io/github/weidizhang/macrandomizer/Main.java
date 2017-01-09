package io.github.weidizhang.macrandomizer;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends AppCompatActivity implements View.OnClickListener {

    Network network = new Network();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        doRootCheck();

        Button randomizeBtn = (Button) findViewById(R.id.button1);
        randomizeBtn.setOnClickListener(this);

        Button restoreBtn = (Button) findViewById(R.id.button2);
        restoreBtn.setOnClickListener(this);

        updateActualMac();
        updateCurrentMac();
    }

    private void doRootCheck() {
        String getID = Command.runAsRoot("id");
        if (!getID.contains("uid=0")) {
            Toast.makeText(getApplicationContext(), "Error: Root permission not granted, application will not work", Toast.LENGTH_LONG).show();
        }
    }

    private void updateActualMac() {

    }

    private void updateCurrentMac() {
        TextView currentMacText = (TextView) findViewById(R.id.textView4);
        currentMacText.setText(network.getCurrentMac());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button1) {
            handleRandomizeButton();
        }
        else if (v.getId() == R.id.button2) {
            handleRestoreButton();
        }
    }

    private void handleRandomizeButton() {
        boolean success = false;

        for (int attempt = 0; attempt < 10; attempt++) {
            String randomMac = network.generateRandomMac();
            network.setMacAddress(randomMac);

            updateCurrentMac();

            if (network.getCurrentMac().equals(randomMac)) {
                success = true;
                Toast.makeText(getApplicationContext(), "Your MAC address has been randomized", Toast.LENGTH_SHORT).show();

                break;
            }
        }

        if (!success) {
            Toast.makeText(getApplicationContext(), "Unable to randomize your MAC address, try again", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleRestoreButton() {
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(false);
        wifiManager.setWifiEnabled(true);

        updateCurrentMac();

        Toast.makeText(getApplicationContext(), "Your actual MAC address has been restored", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
