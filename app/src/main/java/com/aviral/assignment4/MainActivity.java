package com.aviral.assignment4;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.aviral.assignment4.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private static final int PERMISSION_REQUEST_CODE = 1;
    public static final int MESSAGE_READ = 2;

    private SerialCommunication serialCommunication;

    private final Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            if (message.what == MESSAGE_READ) {
                String sensorData = (String) message.obj;
                binding.data.setText(sensorData);
            }
            return true;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        initializeSerialCommunication();

    }

    private void initializeSerialCommunication() {
        serialCommunication = new SerialCommunication(this, handler);
        serialCommunication.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serialCommunication != null) {
            serialCommunication.disconnect();
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeSerialCommunication();
            } else {
                Snackbar.make(
                        binding.layoutMain,
                        "Permission denied",
                        Snackbar.LENGTH_SHORT
                ).show();
            }
        }
    }
}