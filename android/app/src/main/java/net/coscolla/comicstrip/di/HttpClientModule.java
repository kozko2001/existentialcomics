package net.coscolla.comicstrip.di;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import net.coscolla.comicstrip.BuildConfig;

import java.util.concurrent.TimeUnit;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

@Module
public class HttpClientModule {
  @Provides
  public OkHttpClient providerHttpClient() {
    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();

    if (BuildConfig.DEBUG) {
      interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
    }

    return new OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .addInterceptor(interceptor)
        .addNetworkInterceptor(new StethoInterceptor())
        .build();
  }
}
