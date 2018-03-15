package com.schaffer.base.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.schaffer.base.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author : SchafferWang at AndroidSchaffer
 * @date : 2018/3/15
 * Project : SchafferBaseLibrary
 * Package : com.schaffer.base.test
 * Description :
 */

public class TestSocketCreateActivity extends AppCompatActivity {



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_socket_create);
        try {
            connect("192.168.1.24",1000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private Socket mSocket;
    private ExecutorService mPool;
    private OutputStream mOutputStream;
    private InputStream mInputStream;

    public boolean connect(String host, int port) throws IOException {
        if (mSocket == null) {
            mSocket = new Socket(host, port);
            mPool = Executors.newCachedThreadPool();
        }
        return mSocket.isConnected();
    }

    /**
     * 需要在子线程进行
     *
     * @throws IOException
     */
    public String socketMessage() throws IOException {
        if (mSocket != null && mSocket.isConnected()) {
            if (mInputStream == null) {
                mInputStream = mSocket.getInputStream();
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(mInputStream));

            String s = reader.readLine();
            reader.close();
            return s;
        }else{
            Log.d("TAG","mSocket==null or !mSocket.isConnected()");
        }
        return null;
    }

    /**
     * 需要在子线程进行
     *
     * @throws IOException
     */
    public void sendMessage(String message) throws IOException {
        if (mSocket != null && mSocket.isConnected()) {
            if (mOutputStream == null) {
                mOutputStream = mSocket.getOutputStream();
            }
            mOutputStream.write(message.getBytes());
            mOutputStream.flush();
        }else{
            Log.d("TAG","mSocket==null or !mSocket.isConnected()");
        }
    }

    public void close() throws IOException {
        if (mSocket != null && mSocket.isConnected()) {
            if (mInputStream != null) {
                mInputStream.close();
                mInputStream = null;
            }
            if (mOutputStream != null) {
                mOutputStream.close();
                mOutputStream = null;
            }
            mSocket.close();
            mSocket = null;
            mPool.shutdown();
        }
    }

    public void get(){
        mPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String s = socketMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void send(){
        mPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    sendMessage("这是发送给服务器的消息");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
