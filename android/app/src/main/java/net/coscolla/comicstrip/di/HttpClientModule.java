package net.coscolla.comicstrip.di;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import net.coscolla.comicstrip.BuildConfig;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import timber.log.Timber;

@Module
public class HttpClientModule {
  @Provides
  public OkHttpClient providerHttpClient(@Named("app_version") Integer version) {
    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
    StethoInterceptor stethoInterceptor = new StethoInterceptor();
    RequestHeaderVersionInterceptor versionInterceptor = new RequestHeaderVersionInterceptor(version);

    if (BuildConfig.DEBUG) {
      interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    return new OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .addInterceptor(interceptor)
        .addNetworkInterceptor(stethoInterceptor)
        .addNetworkInterceptor(versionInterceptor)
        .build();
  }

  public class RequestHeaderVersionInterceptor implements Interceptor {
    private final int version;

    public RequestHeaderVersionInterceptor(int version) {
      this.version = version;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
      Request request = chain.request();
      Request newRequest;

        newRequest = request.newBuilder()
            .addHeader("APP", "" + version)
            .build();
      return chain.proceed(newRequest);
    }
  }
}
