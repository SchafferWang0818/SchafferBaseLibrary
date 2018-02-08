package com.schaffer.base.common.base;

import com.schaffer.base.BuildConfig;
import com.schaffer.base.api.ApiInterface;
import com.schaffer.base.common.utils.LtUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author SchafferWang
 * @date 2017/5/13
 */

public abstract class BaseModel<T> {


    private T service;
    private volatile static Retrofit retrofit;

    public BaseModel() {
        if (retrofit == null) {
            synchronized (BaseModel.class) {
                if (retrofit == null) {
                    OkHttpClient client = new OkHttpClient().newBuilder()
                            .connectTimeout(20, TimeUnit.SECONDS)
                            .readTimeout(20, TimeUnit.SECONDS)
                            .writeTimeout(20, TimeUnit.SECONDS)
                            .addInterceptor(createHttpLoggingInterceptor())
                            .addInterceptor(new MyHeaderInterceptor())
                            .build();

                    if (ApiInterface.HOST_BASE_URL.startsWith("https")) {
                        //https 跳过SSL认证
                        solveHttps(client);
                    }

                    retrofit = new Retrofit.Builder()
                            .baseUrl(ApiInterface.HOST_BASE_URL)
                            .client(client)
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                }
            }
        }
        service = retrofit.create(getServiceClass());

    }


    private static Interceptor createHttpLoggingInterceptor() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new MyLogger());
        logging.setLevel(Boolean.parseBoolean("true") ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        return logging;
    }


    public T getService() {
        return service;
    }

    /**
     * 获取接口定义参数等信息对应的接口
     *
     * @return
     */
    protected abstract Class<T> getServiceClass();

    public OkHttpClient solveHttps(OkHttpClient sClient) {
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[]{new TrustManager()}, new SecureRandom());
        } catch (Exception e) {
            e.printStackTrace();
        }

        HostnameVerifier hv1 = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        String workerClassName = "okhttp3.OkHttpClient";
        try {
            Class workerClass = Class.forName(workerClassName);
            Field hostnameVerifier = workerClass.getDeclaredField("hostnameVerifier");
            hostnameVerifier.setAccessible(true);
            hostnameVerifier.set(sClient, hv1);

            Field sslSocketFactory = workerClass.getDeclaredField("sslSocketFactory");
            sslSocketFactory.setAccessible(true);
            sslSocketFactory.set(sClient, sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sClient;
    }

    private static class TrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            /*return new X509Certificate[0]*/
            return null;
        }
    }

    private static class MyLogger implements HttpLoggingInterceptor.Logger {

        @Override
        public void log(String message) {
            if (BuildConfig.DEBUG) {
                if (message.startsWith("{") && message.endsWith("}")) {
                    LtUtils.d(">>>:\n" + message + "\n");
                } else {
                    LtUtils.d(message);
                }
            }
        }
    }

    private static class MyHeaderInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request original = chain.request();
            Request request = original.newBuilder()
                    ////版本号
                    //.header("v", "AppUtils.getVersionCode(BaseApplication.getInstance())")
                    ////系统版本号
                    //.header("sv", Build.MANUFACTURER + " " + Build.VERSION.SDK)
                    ////2=android
                    .header("type", "2")
                    .method(original.method(), original.body())
                    .build();
            return chain.proceed(request);
        }
    }
}
