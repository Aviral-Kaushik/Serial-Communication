package com.aviral.assignment4;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.util.Log;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.util.HashMap;
import java.util.Map;


public class SerialCommunication {

    private static final String TAG = "SerialCommunication";

    private static final int BAUD_RATE = 9600;
    private static final int DATA_BITS = UsbSerialInterface.DATA_BITS_8;
    private static final int STOP_BITS = UsbSerialInterface.STOP_BITS_1;
    private static final int PARITY = UsbSerialInterface.PARITY_NONE;
    private static final int FLOW_CONTROL = UsbSerialInterface.FLOW_CONTROL_OFF;

    private byte[] response;
    private Context context;
    private UsbSerialDevice serialPort;
    private UsbManager usbManager;
    private Handler handler;

    public SerialCommunication(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
        usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
    }

    public void connect() {
        UsbDevice device = findDevice();

        if (device != null) {
            serialPort = UsbSerialDevice.createUsbSerialDevice(device, usbManager.openDevice(device));

            if (serialPort != null) {
                if (serialPort.open()) {
                    serialPort.setBaudRate(BAUD_RATE);
                    serialPort.setDataBits(DATA_BITS);
                    serialPort.setStopBits(STOP_BITS);
                    serialPort.setParity(PARITY);
                    serialPort.setFlowControl(FLOW_CONTROL);

                    serialPort.read(mCallback);

                    serialPort.syncRead(response, 1024);
                } else {
                    Log.d(TAG, "connect: Failed to open the serial port");
                }
            } else {
                Log.d(TAG, "connect: Device is not supported or no permissions");
            }
        } else {
            Log.d(TAG, "connect: Device not found");
        }
    }

    public void disconnect() {
        if (serialPort != null) {
            serialPort.close();
            serialPort = null;
        }
    }

    private UsbDevice findDevice() {
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();

        for (Map.Entry<String, UsbDevice> entry : deviceList.entrySet()) {
            UsbDevice device = entry.getValue();

            if (device.getVendorId() == 1234 && device.getProductId() == 5678) {
                return device;
            }
        }

        return null;
    }

    private UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() {
        @Override
        public void onReceivedData(byte[] data) {
            // Process the received data from the sensor
            String sensorData = new String(data);  // Convert the byte array to a string

            // Send the data to the UI thread for further processing
            handler.obtainMessage(MainActivity.MESSAGE_READ, sensorData).sendToTarget();
        }
    };

}
