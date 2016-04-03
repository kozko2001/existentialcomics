package net.coscolla.comicstrip.ui.detail;

import android.support.annotation.NonNull;

import net.coscolla.comicstrip.db.ComicCache;
import net.coscolla.comicstrip.entities.Strip;
import net.coscolla.comicstrip.net.api.ComicApi;
import net.coscolla.comicstrip.usecases.DetailStripUseCase;
import net.coscolla.comicstrip.utils.RxFileUtils;

import java.io.IOException;

import rx.Observable;
import timber.log.Timber;

import static rx.Observable.error;
import static rx.Observable.just;
import static rx.schedulers.Schedulers.io;

public class DetailStripUseCaseImpl implements DetailStripUseCase {

  private final ComicCache database;
  private final ComicApi api;
  private final RxFileUtils fs;

  public DetailStripUseCaseImpl(ComicApi api, ComicCache database, RxFileUtils fs) {
    this.api = api;
    this.database = database;
    this.fs = fs;
  }

  @Override
  public Observable<byte[]> getImage(@NonNull Strip strip) {

    String key = strip._id;

    Observable<byte[]> networkRequest = api.stripImage(strip._id)
        .subscribeOn(io())
        .flatMap(responseBody -> {
          try {
            return just(responseBody.bytes());
          } catch (IOException e) {
            return error(e);
          }
        })
        .doOnNext(data -> writeToCache(key, data));

    Observable<byte[]> fsRequest = fs.readFile(key);

    return fsRequest.switchIfEmpty(networkRequest);
  }

  private void writeToCache(String key, byte[] data) {
    fs.writeFile(key, data)
        .subscribeOn(io())
        .subscribe(
            (v) -> Timber.d("bytes inserted into fs cache for key %s", key),
            (e) -> Timber.e("Could not write into cache for key %s", key)
        );
  }

  @Override
  public Observable<Strip> getStripById(@NonNull String id) {
    return database.getStripById(id)
        .subscribeOn(io());
  }
}
