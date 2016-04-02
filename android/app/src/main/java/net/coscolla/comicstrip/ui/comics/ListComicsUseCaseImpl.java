package net.coscolla.comicstrip.ui.comics;

import com.fernandocejas.frodo.annotation.RxLogObservable;

import net.coscolla.comicstrip.db.ComicCache;
import net.coscolla.comicstrip.entities.Comic;
import net.coscolla.comicstrip.net.api.ComicApi;
import net.coscolla.comicstrip.usecases.ListComicsUseCase;

import java.util.List;

import rx.Observable;

import static rx.schedulers.Schedulers.io;

public class ListComicsUseCaseImpl implements ListComicsUseCase {

  private final ComicCache cache;
  private final ComicApi api;

  public ListComicsUseCaseImpl(ComicCache cache, ComicApi api) {
    this.cache = cache;
    this.api = api;
  }

  /**
   * @return an Observable that emits the current status of the model
   */
  @Override
  @RxLogObservable
  public Observable<List<Comic>> model() {
    return cache.listComics();
  }

  /**
   * Reloads the data from the backend and stores it to the cache database
   *
   * @return Observable with the new data fetched
   */
  @Override
  public Observable<List<Comic>> refresh() {
    return api.listComics()
        .subscribeOn(io())
        .map(apiResult -> apiResult.comics)
        .doOnNext(cache::insertComics);
  }
}
