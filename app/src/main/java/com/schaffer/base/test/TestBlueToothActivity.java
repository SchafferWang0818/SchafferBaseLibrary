package com.schaffer.base.test;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.schaffer.base.R;
import com.schaffer.base.common.base.BaseEmptyActivity;

import java.util.ArrayList;
import java.util.List;

public class TestBlueToothActivity extends BaseEmptyActivity<TestBlueToothActivity, TestBlueToothPresenter> {


    protected ListView mLvDevices;
    private BluetoothAdapter mBluetoothAdapter;
    private ArrayAdapter<String> mDeviceAdapter;

    @Override
    protected void inflateView() {
        inflateContent(R.layout.test_bluetooth);
        initView();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            showSnackbar("您的设备不支持蓝牙!!!\n您的设备不支持蓝牙!!!\n您的设备不支持蓝牙!!!");
        }
    }

    @Override
    public void initView() {
        super.initView();
        mLvDevices = (ListView) findViewById(R.id.test_bt_lv_devices);
        mDeviceAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, founds);
        mLvDevices.setAdapter(mDeviceAdapter);
        mLvDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final BluetoothDevice device = mDevises.get(i);
                new AlertDialog.Builder(TestBlueToothActivity.this).setMessage("连接?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                                    BluetoothGatt gatt = device.connectGatt(TestBlueToothActivity.this, true, mGattCallback);
                                }
                            }
                        }).create().show();

            }
        });
    }

    @Override
    protected TestBlueToothPresenter initPresenter() {
        return new TestBlueToothPresenter();
    }

    @Override
    public boolean isShowTitleBar() {
        return true;
    }

    @Override
    protected void refreshData() {

    }

    public void onOpenBlueTooth(View view) {
        if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 1);
                //mBluetoothAdapter.enable();//直接粗暴
            } else {
                mBluetoothAdapter.disable();
            }
        }

    }


    public void onBlueToothVisible(View view) {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        /*0 = 一直可见*/
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
        startActivityForResult(discoverableIntent, 2);
    }

    boolean flag = false;

    public void onSearch(View view) {
        if (mBluetoothAdapter == null) return;
        founds.clear();
        showSnackbar(flag ? "结束查找" : "开始查找");
        if (!flag) {
            mBluetoothAdapter.startDiscovery();
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
//                mBluetoothAdapter.startLeScan(mCallback);
//            }
        } else {
            mBluetoothAdapter.cancelDiscovery();
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
//                mBluetoothAdapter.stopLeScan(mCallback);
//            }
            mDeviceAdapter.notifyDataSetChanged();
        }
        flag = !flag;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED && requestCode == 2) {
            showSnackbar("被可见失败");
        }
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case 1:
                int state = mBluetoothAdapter.getState();
                showSnackbar("请求成功!!!-->state:" + state);
                break;
            case 2:
                showSnackbar("被可见成功");
                break;
        }
    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }


    @Override
    protected void initData() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
    }

    List<String> founds = new ArrayList<>();

    //发现设备方式1:广播----------------------------------------------------------------------------------
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {/*type*/
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    founds.add(device.getName() + "->" + device.getAddress() + "->" + device.getBondState() + "->" + device.getType());
                    mDevises.add(device);
                    mDeviceAdapter.notifyDataSetChanged();
                }
            }
        }
    };


    //发现设备方式2:LeScanCallback----------------------------------------------------------------------------------
    private BluetoothAdapter.LeScanCallback mCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int i, byte[] bytes) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (device != null) {
                        if (!TextUtils.isEmpty(device.getName())) {
                            String address = device.getAddress();
                            showLog(device.getName() + "->" + device.getAddress() + "->" + device.getBondState());
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                                for (BluetoothDevice devise : mDevises) {
                                    if (devise.getAddress().equals(address)) {
                                        return;
                                    }
                                }
                                founds.add(device.getName() + "->" + device.getAddress() + "->" + device.getBondState() + "->" + device.getType());
                                mDevises.add(device);
                                mDeviceAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            });
        }
    };

    List<BluetoothDevice> mDevises = new ArrayList<>();


    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        // 这里有9个要实现的方法，看情况要实现那些，用到那些就实现那些
        //当连接状态发生改变的时候
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            showToast(status + ">>>>" + newState);
        }

        //回调响应特征写操作的结果。
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

        }


        //回调响应特征读操作的结果。
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

        }

        //当服务被发现的时候回调的结果
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {

        }

        //当连接能被被读的操作
        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {

            super.onDescriptorRead(gatt, descriptor, status);
        }
    };

}
