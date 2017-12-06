package com.schaffer.base.test;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.schaffer.base.DefineInterface;
import com.schaffer.base.IMyAidlInterface;

import java.util.List;

/**
 * Created by AndroidSchaffer on 2017/12/5.
 */

public class TestAidlActivity extends AppCompatActivity {

    private Binder binder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binder = new Binder();
        bindService(new Intent(this,TestAidlService.class),connection,BIND_AUTO_CREATE);
    }

    public ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DefineInterface define = DefineInterface.Stub.asInterface(service);

            try {
                define.setBinderDeath(binder);
                List<Book> list = define.getList();
                define.myInterface(new IMyAidlInterface() {
                    @Override
                    public String back(int type) throws RemoteException {
                        return null;
                    }

                    @Override
                    public IBinder asBinder() {

                        return null;
                    }
                });
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
}
