package com.schaffer.base.test;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by AndroidSchaffer on 2017/12/7.
 */

public class TestSocketClientActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Socket socket = null;
                try {
                    socket = new Socket("localhost", 8688);
                    PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);

                } catch (IOException e) {
                    e.printStackTrace();
                    SystemClock.sleep(1000);
                    Log.d("TEST", "client: connect server failed by IOException");
                }
            }
        }).start();
    }
}
