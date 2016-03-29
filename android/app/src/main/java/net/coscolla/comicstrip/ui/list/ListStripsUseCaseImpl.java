package net.coscolla.comicstrip.ui.list;

import android.support.annotation.NonNull;

import net.coscolla.comicstrip.db.ComicCache;
import net.coscolla.comicstrip.entities.Strip;
import net.coscolla.comicstrip.net.api.ComicApi;
import net.coscolla.comicstrip.net.api.UrlBuilder;
import net.coscolla.comicstrip.usecases.ListStripsUseCase;
import java.util.List;
import rx.Observable;

import static rx.schedulers.Schedulers.io;

public class ListStripsUseCaseImpl implements ListStripsUseCase {

  private final ComicCache cache;
  private final ComicApi api;
  private final UrlBuilder urlBuilder;

  public ListStripsUseCaseImpl(ComicCache cache, ComicApi api, UrlBuilder urlBuilder) {
    this.cache = cache;
    this.api = api;
    this.urlBuilder = urlBuilder;
  }

  @Override
  public Observable<List<Strip>> observableModel(String comic) {
    return cache.listStrips(comic);
  }

  @Override
  public Observable<List<Strip>> refresh(String comic) {
    String lastId = getLastId(comic);

    return api.listStrips(comic, lastId)
        .subscribeOn(io())
        .map(apiResult -> apiResult.result)
        .doOnNext(cache::insertStrips);
  }

  /**
   * last strip id fetched in this comic collection so we only retrieve the new one
   * @param comic
   * @return last fetched strip id or if no strip at all, the string "unknown"
   */
  @NonNull
  private String getLastId(String comic) {
    String lastId = cache.lastStripId(comic);
    if(lastId == null) {
      lastId = "unknown";
    }
    return lastId;
  }

  /**
   * @param strip strip entity
   * @return url for the preview of this strip
   */
  public String getPreviewUrl(@NonNull Strip strip) {
    return urlBuilder.urlThumbnail(strip);
  }
}
