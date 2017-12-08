package com.schaffer.base.test;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

/**
 * Created by AndroidSchaffer on 2017/12/7.
 */

public class TestSocketServerService extends Service {


    private boolean isDestroy = false;

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(new MyRunnable()).start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        isDestroy = true;
        super.onDestroy();
    }

    public class MyRunnable implements Runnable {
        public String[] strs = new String[]{"str1", "str2", "str3", "str4", "str5", "str6", "str7", "str8", "str9"};

        @Override
        public void run() {
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(8688);

            } catch (IOException e) {
                Log.d("TEST", "create server failed!!");
                e.printStackTrace();
                return;
            }

            while (!isDestroy) {
                try {
                    final Socket accept = serverSocket.accept();
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            try {
                                onResponse(accept);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }

        private void onResponse(Socket accept) throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(accept.getInputStream()));
            PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(accept.getOutputStream())),true);
            while (!isDestroy) {
                String s = reader.readLine();
                Log.d("TEST","client : "+s);
                String str = strs[new Random().nextInt(strs.length)];
                writer.println(str);
                Log.d("TEST","server : "+str);
            }
            reader.close();
            writer.close();
            Log.d("TEST","server : close successfully");
        }
    }
}
