package com.example.serversample;

import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    public static String SOCKET_ADDRESS = "your.local.socket.address";

    // post this to the Handler when the background thread notifies
    private final NotificationRunnable notificationRunnable = new NotificationRunnable();

    // background threads use this Handler to post messages to
    // the main application thread
    private final Handler mHandler = new Handler();

    private TextView mText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mText = (TextView) findViewById(R.id.text2);
        new SocketListener(this.mHandler, this.notificationRunnable).start();
    }

    public class NotificationRunnable implements Runnable {
        private String message = null;

        public void run() {
            if (message != null && message.length() > 0) {
                mText.setText(mText.getText().toString()+"Client Says: "+ message + "\n");
            }
        }

        /**
         * @param message the message to set
         */
        public void setMessage(String message) {
            this.message = message;
        }
    }

    class SocketListener extends Thread {
        private Handler handler = null;
        private NotificationRunnable runnable = null;

        public SocketListener(Handler handler, NotificationRunnable runnable) {
            this.handler = handler;
            this.runnable = runnable;
            this.handler.post(this.runnable);
        }

        /**
         * Show UI notification.
         * @param message
         */
        private void showMessage(String message) {
            this.runnable.setMessage(message);
            this.handler.post(this.runnable);
        }

        @Override
        public void run() {
            showMessage("DEMO: SocketListener started!");
            try {
                LocalServerSocket server = new LocalServerSocket(SOCKET_ADDRESS);
                while (true) {
                    LocalSocket receiver = server.accept();
                    if (receiver != null) {
                        InputStream input = receiver.getInputStream();

                        // simply for java.util.ArrayList
                        int readed = input.read();
                        int size = 0;
                        int capacity = 0;
                        byte[] bytes = new byte[capacity];

                        // reading
                        while (readed != -1) {
                            // java.util.ArrayList.Add(E e);
                            capacity = (capacity * 3)/2 + 1;
                            //bytes = Arrays.copyOf(bytes, capacity);
                            byte[] copy = new byte[capacity];
                            System.arraycopy(bytes, 0, copy, 0, bytes.length);
                            bytes = copy;
                            bytes[size++] = (byte)readed;

                            // read next byte
                            readed = input.read();
                        }
                        showMessage(new String(bytes, 0, size));
                    }
                }
            } catch (IOException e) {
                Log.e(getClass().getName(), e.getMessage());
            }
        }
    }

    private void messageBox(String method, String message)
    {
        Log.d("EXCEPTION: " + method,  message);

        AlertDialog.Builder messageBox = new AlertDialog.Builder(this);
        messageBox.setTitle(method);
        messageBox.setMessage(message);
        messageBox.setCancelable(false);
        messageBox.setNeutralButton("OK", null);
        messageBox.show();
    }
}
