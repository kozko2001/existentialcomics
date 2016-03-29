package net.coscolla.comicstrip.push.gcm;

import net.coscolla.comicstrip.db.ComicCache;
import net.coscolla.comicstrip.entities.Strip;
import net.coscolla.comicstrip.net.api.ComicApi;
import net.coscolla.comicstrip.usecases.PushReceiveUseCase;

import rx.Observable;

import static rx.Observable.from;
import static rx.schedulers.Schedulers.io;

public class PushReceiveUseCaseImpl implements PushReceiveUseCase {

  private final ComicApi api;
  private final ComicCache cache;

  public PushReceiveUseCaseImpl(ComicApi api, ComicCache cache) {
    this.api = api;
    this.cache = cache;
  }

  @Override
  public Observable<Strip> getLastStrip(String comic) {
    String lastId = cache.lastStripId(comic);
    if(lastId == null) {
      lastId = "unknown";
    }

    return api.listStrips(comic, lastId)
        .subscribeOn(io())
        .flatMap(result -> from(result.result))
        .first();
  }
}
