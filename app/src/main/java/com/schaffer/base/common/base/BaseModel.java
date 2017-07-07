package com.schaffer.base.common.base;

import com.schaffer.base.common.api.ApiService;
import com.schaffer.base.common.utils.LTUtils;

import java.lang.reflect.Field;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by a7352 on 2017/5/13.
 */

public abstract class BaseModel<T> {


	private T service;
	private volatile static Retrofit retrofit;

	public BaseModel() {
		if (retrofit == null) {
			synchronized (BaseModel.class) {
				if (retrofit == null) {
					OkHttpClient client = new OkHttpClient().newBuilder()
							.connectTimeout(60, TimeUnit.SECONDS)
							.readTimeout(60, TimeUnit.SECONDS)
							.writeTimeout(60, TimeUnit.SECONDS)
//                            .addInterceptor(createUserAgentInterceptor())
							.addInterceptor(createHttpLoggingInterceptor())
							.build();

					if (ApiService.HOST_BASE_URL.startsWith("https")) {//https 跳过SSL认证
						solveHttps(client);
					}

					retrofit = new Retrofit.Builder()
							.baseUrl(ApiService.HOST_BASE_URL)
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
		HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
			@Override
			public void log(String message) {
				if (message.startsWith("{") && message.endsWith("}")) {
					LTUtils.w("json:\n" + message + "\n");
				} else {
					LTUtils.d(message);
				}
			}
		});
		logging.setLevel(Boolean.parseBoolean("true") ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
		return logging;
	}


	public T getService() {
		return service;
	}

	protected abstract Class<T> getServiceClass();

	//    private static Interceptor createUserAgentInterceptor() {
//        return new Interceptor() {
//
//            @Override
//            public Response intercept(Chain chain) throws IOException {
//                Request request = chain.request().newBuilder()
//                        .header("User-Agent", USER_AGENT)
//                        .build();
//                return chain.proceed(request);
//            }
//
//        };
//    }


	public OkHttpClient solveHttps(OkHttpClient sClient) {
		SSLContext sc = null;
		try {
			sc = SSLContext.getInstance("SSL");
			sc.init(null, new TrustManager[]{new X509TrustManager() {
				@Override
				public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {

				}

				@Override
				public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {

				}

				@Override
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			}}, new SecureRandom());
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
}
