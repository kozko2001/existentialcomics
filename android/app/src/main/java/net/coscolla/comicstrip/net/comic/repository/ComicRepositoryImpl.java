/*
 * Copyright 2016 Jordi Coscolla.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.coscolla.comicstrip.net.comic.repository;

import com.fernandocejas.frodo.annotation.RxLogObservable;

import net.coscolla.comicstrip.net.api.ComicApi;
import net.coscolla.comicstrip.entities.Comic;
import net.coscolla.comicstrip.entities.Strip;
import net.coscolla.comicstrip.db.ComicCache;
import net.coscolla.comicstrip.push.PushManager;

import java.util.List;

import rx.Observable;
import timber.log.Timber;

import static rx.schedulers.Schedulers.io;

public class ComicRepositoryImpl implements ComicRepository {

  private final ComicApi api;
  private final ComicCache cache;
  private final PushManager pushManager;
  private String LOGTAG = "ComicRepositoryImpl";

  public ComicRepositoryImpl(ComicApi api, ComicCache cache, PushManager pushManager) {
    this.api = api;
    this.cache = cache;
    this.pushManager = pushManager;
  }


  @Override
  @RxLogObservable
  public Observable<List<Strip>> getStrips(String comic) {

    Observable<List<Strip>> dbObservable = cache.listStrips(comic);

    getStripsApi(comic)
        .subscribeOn(io())
        .subscribe((l) -> {

        }, (e) -> {
          Timber.e(LOGTAG, "Error fetching data from the api", e);
        });

    return dbObservable;
  }

  @Override
  public Observable<Boolean> isSubscribed(String comic) {
    return Observable.fromCallable(() -> pushManager.isSubscribed(comic));
  }

  @Override
  public Observable<Boolean> subscribe(String comic) {
    return pushManager.subscribe(comic)
        .map(r -> true); // TODO: always is true on the backend
  }

  @Override
  public Observable<Boolean> unsubscribe(String comic) {
    return pushManager.unsubscribe(comic)
        .map(r -> true);
  }

  public Observable<List<Strip>> getStripsApi(String comic) {
    String lastId = cache.lastStripId(comic);

    if(lastId == null) {
      lastId = "unknown";
    }

    return api.listStrips(comic, lastId)
        .map(result -> result.result)
        .doOnNext(cache::insertStrips);
  }

  @RxLogObservable
  private Observable<Comic> listComicsApi() {
    return api.listComics()
        .flatMap(result -> Observable.from(result.comics));
  }

}
