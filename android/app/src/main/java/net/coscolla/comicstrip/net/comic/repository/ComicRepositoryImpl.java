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

import net.coscolla.comicstrip.net.comic.api.ComicApi;
import net.coscolla.comicstrip.net.comic.api.entities.Comic;
import net.coscolla.comicstrip.net.comic.api.entities.Strip;
import net.coscolla.comicstrip.net.comic.db.ComicCache;
import net.coscolla.comicstrip.net.push.PushManager;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
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
  public Observable<List<Comic>> getComics() {

    Observable<List<Comic>> cacheObservable = cache.listComics().toList();
    Observable<List<Comic>> network = listComicsApi()
        .doOnNext(cache::insertComic)
        .toList();


    return cacheObservable
        .concatWith(network)
        .filter(comic -> comic != null);
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


  public Observable<List<Strip>> getStripsApi(String comic) {
    return api.listStrips(comic)
        .map(result -> result.result)
        .doOnNext(cache::insertStrips);
  }

  @RxLogObservable
  private Observable<Comic> listComicsApi() {
    return api.listComics()
        .flatMap(result -> Observable.from(result.comics));
  }

}
