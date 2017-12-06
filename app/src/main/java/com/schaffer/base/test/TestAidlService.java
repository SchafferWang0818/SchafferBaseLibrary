package com.schaffer.base.test;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.schaffer.base.DefineInterface;
import com.schaffer.base.IMyAidlInterface;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by AndroidSchaffer on 2017/12/5.
 */

public class TestAidlService extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (checkCallingOrSelfPermission("com.schaffer.base.permission.BIND_TEST") == PackageManager.PERMISSION_DENIED) {
            return null;
        }
        return binder;
    }

    Binder binder = new DefineInterface.Stub() {
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public void myInterface(IMyAidlInterface inter) throws RemoteException {
            inter.back(1);
        }

        @Override
        public List<Book> getList() throws RemoteException {
            return books;
        }

        @Override
        public void setBinderDeath(IBinder binder) throws RemoteException {
            binder.linkToDeath(new MyDeathRecipient(), 0);
        }

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            /* 判断权限是否被允许 */
            if (checkCallingOrSelfPermission("com.schaffer.base.permission.BIND_TEST") == PackageManager.PERMISSION_DENIED) {
                return false;
            }
            /* 判断包名是否被允许 */
            String pn = null;
            String[] packages = getPackageManager().getPackagesForUid(getCallingUid());
            if (packages != null && packages.length > 0) {
                pn = packages[0];
            }
            if (!pn.contains("com.schaffer")) {
                return false;
            }
            return super.onTransact(code, data, reply, flags);
        }
    };

    CopyOnWriteArrayList<Book> books = new CopyOnWriteArrayList<>();

    public static class MyDeathRecipient implements IBinder.DeathRecipient {

        @Override
        public void binderDied() {
            Log.d("TAG", "binder 离线");
        }
    }

}
