package net.coscolla.comicstrip.ui.detail;

import android.support.annotation.NonNull;

import net.coscolla.comicstrip.db.ComicCache;
import net.coscolla.comicstrip.entities.Strip;
import net.coscolla.comicstrip.net.api.UrlBuilder;
import net.coscolla.comicstrip.usecases.DetailStripUseCase;

import rx.Observable;

import static rx.schedulers.Schedulers.io;

public class DetailStripUseCaseImpl implements DetailStripUseCase {

  private final UrlBuilder urlBuilder;
  private final ComicCache cache;

  public DetailStripUseCaseImpl(UrlBuilder urlBuilder, ComicCache cache) {
    this.urlBuilder = urlBuilder;
    this.cache = cache;
  }

  @Override
  @NonNull
  public String getStripImageUrl(@NonNull  Strip strip) {
    return urlBuilder.urlImage(strip._id);
  }

  @Override
  public Observable<Strip> getStripById(String id) {
    return cache.getStripById(id)
        .subscribeOn(io());
  }
}
