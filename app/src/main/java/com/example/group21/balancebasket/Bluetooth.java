package com.example.group21.balancebasket;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.Uart;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOService;

public class Bluetooth extends IOIOService {
    // Debugging
    private static final String TAG = "Bluetooth";
    private static final boolean D = BasketDrawer.D;

    private int mState;
    private byte[] coordinates = new byte[1];
    private static byte[] information = new byte[1];
    public static String input;
    public static String[] dataInput = new String[30];
    private final IBinder blueBinder =  new BlueBinder();
    private LocalBroadcastManager broadcaster;

    // Constants that indicate the current connection state
    public static final int STATE_BT_CONNECTED = 2; // now connected to a remote device
    public static final int STATE_BT_DISCONNECTED = 3; //
    public static boolean connection = false;

    public static final String CONNECTION_STATE = "com.example.group21.balancebasket.Bluetooth.CONNECTION_STATE";
    public static final String CONNECTION_MESSAGE = "com.example.group21.balancebasket.Bluetooth.CONNECTION_MESSAGE";
    public static final String STATE_CONNECTED = "com.example.group21.balancebasket.Bluetooth.CONNECTED";
    public static final String STATE_DISCONNECTED = "com.example.group21.balancebasket.Bluetooth.DISCONNECTED";

    /*
        Default constructor
    */

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Toast.makeText(this, "Service started", Toast.LENGTH_LONG).show();
        if (intent != null && intent.getAction() != null
                && intent.getAction().equals("stop")) {
            // User clicked the notification. Need to stop the service.
            stopSelf();
        }
    }

    // send a message with the connection status
    public void sendResult(String message) {
        Intent intent = new Intent(CONNECTION_STATE);
        if(message != null) {
            intent.putExtra(CONNECTION_MESSAGE, message);
        }
        broadcaster.sendBroadcast(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        IBinder blueBinder = new BlueBinder();
        return blueBinder;
    }

    public class BlueBinder extends Binder
    {
        Bluetooth getBluetooth() {
            return Bluetooth.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        broadcaster = LocalBroadcastManager.getInstance(this);
    }

    /**
     * This is the thread on which all the IOIO activity happens. It will be run
     * every time the application is resumed and aborted when it is paused. The
     * method setup() will be called right after a connection with the IOIO has
     * been established (which might happen several times!). Then, loop() will
     * be called repetitively until the IOIO gets disconnected.
     */
    class Looper extends BaseIOIOLooper {
        /** The on-board LED. */
        private DigitalOutput led_;
        Uart uart;
        OutputStream out;
        InputStream in;

        /**
         * Called every time a connection with IOIO has been established.
         * Typically used to open pins.
         * @see ioio.lib.util.IOIOLooper#setup(IOIO)
         */
        @Override
        protected void setup() throws ConnectionLostException {
            uart = ioio_.openUart(6, 7, 9600, Uart.Parity.NONE, Uart.StopBits.ONE);
            in = uart.getInputStream();
            out = uart.getOutputStream();

            mState = STATE_BT_CONNECTED;
            sendResult(STATE_CONNECTED);
            showVersions(ioio_, "IOIO connected!");
            led_ = ioio_.openDigitalOutput(0, true);
            connection = true;
        }

        /**
         * Called repetitively while the IOIO is connected.
         *
         * @throws ConnectionLostException
         *             When IOIO connection is lost.
         *
         * @see ioio.lib.util.IOIOLooper#loop()
         */
        @Override
        public void loop() throws ConnectionLostException, InterruptedException {
            try {
              out.write(coordinates);
              in.read(information);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Thread.sleep(100);

        }

        @Override
        public void disconnected() {
            mState = Bluetooth.STATE_BT_DISCONNECTED;
            sendResult(STATE_DISCONNECTED);
            toast("IOIO disconnected");
            connection = false;
        }

        @Override
        public void incompatible() {
            showVersions(ioio_, "Incompatible firmware version!");
        }
    }

    /*
        get and show version info
    */
    private void showVersions(IOIO ioio, String title) {
        toast(String.format("%s\n" +
                        "IOIOLib: %s\n" +
                        "Application firmware: %s\n" +
                        "Bootloader firmware: %s\n" +
                        "Hardware: %s",
                title,
                ioio.getImplVersion(IOIO.VersionType.IOIOLIB_VER),
                ioio.getImplVersion(IOIO.VersionType.APP_FIRMWARE_VER),
                ioio.getImplVersion(IOIO.VersionType.BOOTLOADER_VER),
                ioio.getImplVersion(IOIO.VersionType.HARDWARE_VER)));

    }

    private void toast(final String message) {
    }

    /**
     * Return the current connection state.
     */
    public synchronized int getState() {
        return mState;
    }

    /*
        Create IOIO thread
        @see ioio.lib.util.AbstractIOIOActivity#createIOIOThread
    */
    @Override
    protected IOIOLooper createIOIOLooper() {
        return new Looper();
    }

    /**
     * Write
     *
     * @param out The bytes to write
     */
    public void write(byte[] out) {
        coordinates = out;
    }

    public void write(String string) {
        write(string.getBytes());
    }

    public static void read(){
       input= String.valueOf(0);
        if(information[0]=='S') {
           int c = 1;
           while(true){
               if (information[c] == ';') // Keep reading until it reads a semicolon
                   break;
               dataInput[c]=new String(information);
               if (information[c] == -1) // Error while reading the string
                   return;
               ++c;
           }
           setString(dataInput,c);
       }
    }

    private static void setString(String[] dataInput,int c) {
        for(int n =1; n<c; n++){
            input = input + dataInput[n];
        }
    }
}