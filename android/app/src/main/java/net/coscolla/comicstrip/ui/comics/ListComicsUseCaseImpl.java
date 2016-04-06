package net.coscolla.comicstrip.ui.comics;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.fernandocejas.frodo.annotation.RxLogObservable;

import net.coscolla.comicstrip.db.ComicCache;
import net.coscolla.comicstrip.entities.Comic;
import net.coscolla.comicstrip.net.api.ComicApi;
import net.coscolla.comicstrip.push.PushManager;
import net.coscolla.comicstrip.usecases.ListComicsUseCase;

import java.util.List;

import rx.Observable;

import static rx.schedulers.Schedulers.io;

public class ListComicsUseCaseImpl implements ListComicsUseCase {

  private final ComicCache cache;
  private final ComicApi api;
  private final PushManager pushManager;

  public ListComicsUseCaseImpl(ComicCache cache, ComicApi api, PushManager pushManager) {
    this.cache = cache;
    this.api = api;
    this.pushManager = pushManager;
  }

  /**
   * @return an Observable that emits the current status of the model
   */
  @Override
  @RxLogObservable
  public Observable<List<Comic>> model() {
    return cache.listComics()
        .map(list -> Stream.of(list)
            .sorted((lhs, rhs) -> sortComicsByFavorite(lhs, rhs))
            .collect(Collectors.toList()));
  }

  /**
   * Comparator to sort the list of comics, first the ones we are subscribed/favorite and
   * then the ones we are not subscribed
   *
   * @param first
   * @param second
   * @return comparator ask to return -1 if left is less than right, 0 if both are equal, and 1 if
   * left is greater than right
   */
  private int sortComicsByFavorite(Comic first, Comic second) {
    boolean isFavoriteLeft = pushManager.isSubscribed(first.comic_id);
    boolean isFavoriteRight = pushManager.isSubscribed(second.comic_id);

    if(isFavoriteLeft && !isFavoriteRight) {
      return -1;
    } else if( isFavoriteRight && !isFavoriteLeft) {
      return 1;
    } else {
      return 0;
    }
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
