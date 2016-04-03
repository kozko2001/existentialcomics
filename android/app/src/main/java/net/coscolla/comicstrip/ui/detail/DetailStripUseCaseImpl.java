package net.coscolla.comicstrip.ui.detail;

import android.support.annotation.NonNull;

import net.coscolla.comicstrip.db.ComicCache;
import net.coscolla.comicstrip.entities.Strip;
import net.coscolla.comicstrip.net.api.UrlBuilder;
import net.coscolla.comicstrip.usecases.DetailStripUseCase;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import timber.log.Timber;

import static rx.Observable.defer;
import static rx.Observable.fromCallable;
import static rx.schedulers.Schedulers.io;

public class DetailStripUseCaseImpl implements DetailStripUseCase {

  private final UrlBuilder urlBuilder;
  private final ComicCache cache;
  private final OkHttpClient httpClient;

  public DetailStripUseCaseImpl(UrlBuilder urlBuilder, ComicCache cache, OkHttpClient httpClient) {
    this.urlBuilder = urlBuilder;
    this.cache = cache;
    this.httpClient = httpClient;
  }

  @Override
  public Observable<byte[]> getImage(@NonNull Strip strip) {
      return  defer(() ->
          fromCallable(() -> {
            String url = urlBuilder.urlImage(strip._id);

            Request request = new Request.Builder()
                .url(url)
                .build();

            try {
              Response response = httpClient.newCall(request).execute();
              return response.body().bytes();
            } catch (IOException e) {
              Timber.e(e, "Error fetching the strip image");
              throw e;
            }
          }).subscribeOn(io()));
  }

  @Override
  public Observable<Strip> getStripById(@NonNull String id) {
    return cache.getStripById(id)
        .subscribeOn(io());
  }
}
