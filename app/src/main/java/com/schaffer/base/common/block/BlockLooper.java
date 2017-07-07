package com.schaffer.base.common.block;

import android.content.Context;
import android.os.Debug;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by SchafferWang on 2017/6/26.
 * Step1. 判断是否停止检测UI线程阻塞，未停止则进入Step2；
 * Step2. 使用uiHandler不断发送ticker这个Runnable，ticker会对tickCounter进行累加；
 * Step3. BlockLooper进入指定时间的sleep（frequency是在initialize时传入，最小不能低于5s）；
 * Step4. 如果UI线程没有发生阻塞，则sleep过后，tickCounter一定与原来的值不相等，否则一定是UI线程发生阻塞；
 * Step5. 发生阻塞后，还需判断是否由于Debug程序引起的，不是则进入Step6；
 * Step6. 回调OnBlockListener，以及选择保存当前进程中所有线程的堆栈状态到SD卡等；
 */

public class BlockLooper implements Runnable {

    private final static String TAG = BlockLooper.class.getSimpleName();
    private final static String LOOPER_NAME = "block-looper-thread";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd-HH-mm-ss");

    private static BlockLooper sLooper;//当前类私有静态对象
    private final static long MIN_FREQUENCY = 500;//最小的轮询频率（单位：毫秒）
    private Context appContext;//上下文,用于文件保存等
    private Handler uiHandler = new Handler(Looper.getMainLooper());
    private volatile int tickCounter = 0;
    private Runnable ticker = new Runnable() {
        @Override
        public void run() {
            tickCounter = (tickCounter + 1) % Integer.MAX_VALUE;//防止次数过多导致抛出异常
        }
    };
    private long frequency;
    private boolean ignoreDebugger;
    private boolean reportAllThreadInfo;
    private boolean saveLog;
    private OnBlockListener onBlockListener;
    private boolean isStop = true;

    public static void initialize(Configuration configuration) {
        if (sLooper == null) {
            synchronized (BlockLooper.class) {
                if (sLooper == null) {
                    sLooper = new BlockLooper();
                }
            }
            sLooper.init(configuration);
        }
    }

    public static BlockLooper getBlockLooper() {
        if (sLooper == null) {
            throw new IllegalStateException("未使用initialize方法初始化BlockLooper");
        }
        return sLooper;
    }

    private BlockLooper() {
    }

    private void init(Configuration configuration) {
        this.appContext = configuration.appContext;
        this.frequency = configuration.frequency < MIN_FREQUENCY ? MIN_FREQUENCY : configuration.frequency;
        this.ignoreDebugger = configuration.ignoreDebugger;
        this.reportAllThreadInfo = configuration.reportAllThreadInfo;
        this.onBlockListener = configuration.onBlockListener;
        this.saveLog = configuration.saveLog;
    }

    @Override
    public void run() {
        int lastTickNumber;
        while (!isStop) {
            lastTickNumber = tickCounter;
            uiHandler.post(ticker);

            try {
                Thread.sleep(frequency);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }

            if (lastTickNumber == tickCounter) {

                //判断是否因为调试造成的消息阻塞
                if (!ignoreDebugger && Debug.isDebuggerConnected()) {
                    Log.w(TAG, "当前由调试模式引起消息阻塞引起ANR，可以通过setIgnoreDebugger(true)来忽略调试模式造成的ANR");
                    continue;
                }

                BlockError blockError;
                //是否报告所有线程的阻塞信息
                if (!reportAllThreadInfo) {
                    blockError = BlockError.getUiThread();
                } else {
                    blockError = BlockError.getAllThread();
                }

                if (onBlockListener != null) {
                    onBlockListener.onBlock(blockError);//错误回调
                }

                if (saveLog) {
                    if (isMounted()) {
                        File logDir = getLogDirectory();
                        saveLogToSdcard(blockError, logDir);
                    } else {
                        Log.w(TAG, "内存卡未挂载!");
                    }
                }
            }

        }
    }

    public static boolean isMounted() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    private void saveLogToSdcard(BlockError blockError, File dir) {
        if (blockError == null) {
            return;
        }
        if (dir != null && dir.exists() && dir.isDirectory()) {
            String fileName = getLogFileName();
            File logFile = new File(dir, fileName);
            if (!logFile.exists()) {
                try {
                    logFile.createNewFile();
                    PrintStream printStream = new PrintStream(new FileOutputStream(logFile, false), true);
                    blockError.printStackTrace(printStream);
                    printStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private File getLogDirectory() {
        File cacheDir = appContext.getExternalCacheDir();
        if (cacheDir != null) {
            File logDir = new File(cacheDir, "block");
            if (!logDir.exists()) {
                boolean successful = logDir.mkdirs();
                if (successful) {
                    return logDir;
                } else {
                    return null;
                }
            } else {
                return logDir;
            }
        }
        return null;
    }

    private String getLogFileName() {
        String timeStampString = DATE_FORMAT.format(new Date());
        String fileName = timeStampString + ".trace";
        return fileName;
    }

    public synchronized void start() {
        if (isStop) {
            isStop = false;
            Thread blockThread = new Thread(this);
            blockThread.setName(LOOPER_NAME);
            blockThread.start();
        }
    }

    public synchronized void stop() {
        if (!isStop) {
            isStop = true;
        }
    }

    public static class Builder {//建造者模式,用于传入配置信息
        private Context appContext;
        private long frequency;
        private boolean ignoreDebugger;
        private boolean reportAllThreadInfo = false;
        private boolean saveLog;
        private OnBlockListener onBlockListener;

        public Builder(Context appContext) {
            this.appContext = appContext;
        }

        public Builder setFrequency(long frequency) {
            this.frequency = frequency;
            return this;
        }

        /**
         * 设置是否忽略debugger模式引起的卡顿
         *
         * @param ignoreDebugger
         * @return
         */
        public Builder setIgnoreDebugger(boolean ignoreDebugger) {
            this.ignoreDebugger = ignoreDebugger;
            return this;
        }

        /**
         * 设置发生卡顿时，是否上报所有的线程信息，默认是false
         *
         * @param reportAllThreadInfo
         * @return
         */
        public Builder setReportAllThreadInfo(boolean reportAllThreadInfo) {
            this.reportAllThreadInfo = reportAllThreadInfo;
            return this;
        }

        public Builder setSaveLog(boolean saveLog) {
            this.saveLog = saveLog;
            return this;
        }

        /**
         * 设置发生卡顿时的回调
         *
         * @param onBlockListener
         * @return
         */
        public Builder setOnBlockListener(OnBlockListener onBlockListener) {
            this.onBlockListener = onBlockListener;
            return this;
        }

        public Configuration build() {
            Configuration configuration = new Configuration();
            configuration.appContext = appContext;
            configuration.frequency = frequency;
            configuration.ignoreDebugger = ignoreDebugger;
            configuration.reportAllThreadInfo = reportAllThreadInfo;
            configuration.saveLog = saveLog;
            configuration.onBlockListener = onBlockListener;
            return configuration;
        }
    }


    private static class Configuration {//阻塞配置
        private Context appContext;
        private long frequency;
        private boolean ignoreDebugger;
        private boolean reportAllThreadInfo;
        private boolean saveLog;
        private OnBlockListener onBlockListener;
    }

    /**
     * 阻塞信息回调
     */
    public static interface OnBlockListener {
        /**
         * 发生ANR时产生回调(在非UI线程中回调)
         *
         * @param blockError
         */
        public void onBlock(BlockError blockError);
    }
}
