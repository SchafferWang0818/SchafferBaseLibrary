package com.schaffer.base.common.base;

import com.schaffer.base.BuildConfig;
import com.schaffer.base.api.ApiInterface;
import com.schaffer.base.common.utils.LtUtils;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.net.Proxy;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
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
                    OkHttpClient.Builder builder = new OkHttpClient().newBuilder()
                            .connectTimeout(20, TimeUnit.SECONDS)
                            .readTimeout(20, TimeUnit.SECONDS)
                            .writeTimeout(20, TimeUnit.SECONDS)
                            .addInterceptor(createHttpLoggingInterceptor())
                            //.addInterceptor(new MultiBaseUrlInterceptor())//多BaseUrl的情况
                            .addInterceptor(new MyHeaderInterceptor());
                    /*阻止Flidder抓包*/
                    if (!BuildConfig.DEBUG){
                        builder.proxy(Proxy.NO_PROXY);
                    }
                    OkHttpClient client = builder.build();

                    if (ApiInterface.HOST_BASE_URL.startsWith("https")) {
                        //https 跳过SSL认证
                        solveHttps(client);
                    }

                    retrofit = new Retrofit.Builder()
                            .baseUrl(ApiInterface.HOST_BASE_URL)
                            .client(client)
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            /*用于文本上传*/
//                            .addConverterFactory(new FileRequestBodyConverterFactory())
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


    static class FileRequestBodyConverterFactory extends Converter.Factory {
        @Override
        public Converter<File, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
            return new FileRequestBodyConverter();
        }
    }

    static class FileRequestBodyConverter implements Converter<File, RequestBody> {

        @Override
        public RequestBody convert(File file) throws IOException {
            return RequestBody.create(MediaType.parse("application/otcet-stream"), file);
        }
    }

    /**
     * 当base_url需要发生变化时需要加@Headers({"apiSign:shop"}) 作为一个标记
     * 例如 目标链接为 https://shop.fentuapp.com.cn/
     */
    private static class MultiBaseUrlInterceptor implements Interceptor {


        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            HttpUrl oldHttpUrl = originalRequest.url();
            try {
                Request.Builder builder = originalRequest.newBuilder();
                List<String> headers = originalRequest.headers("apiSign");
                if (headers != null && headers.size() > 0) {
                    builder.removeHeader("apiSign");
                    String s = headers.get(0);

                    HttpUrl baseUrl = null;
                    if (s.equals("shop")) {
                        baseUrl = HttpUrl.parse(ApiInterface.HOST_BASE_URL);
                        HttpUrl.Builder port = oldHttpUrl.newBuilder()
                                .scheme(baseUrl.scheme())
                                .host(baseUrl.host())
                                .port(baseUrl.port());
                        HttpUrl newUrl = null;
                        if (ApiInterface.HOST_BASE_URL.contains("fentu_server/public/")) {
                            //测试库
                            //http://192.168.1.167/fentu_server/public/
                            //https://shop.fentuapp.com.cn/
                            newUrl = port.removePathSegment(0).removePathSegment(0).build();
                        } else {
                            //线上库
                            //https://api.fentuapp.com.cn/
                            //https://shop.fentuapp.com.cn/
                            newUrl = port.build();
                        }
                        Request newRequest = builder.url(newUrl).build();
                        return chain.proceed(newRequest);
                    } else {
                        return chain.proceed(originalRequest);
                    }
                } else {
                    return chain.proceed(originalRequest);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return chain.proceed(originalRequest);
            }
        }
    }
}
