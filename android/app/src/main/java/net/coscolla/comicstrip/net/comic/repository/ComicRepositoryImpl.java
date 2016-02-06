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

import java.util.List;

import rx.Observable;
import timber.log.Timber;

public class ComicRepositoryImpl implements ComicRepository {

  private final ComicApi api;
  private final ComicCache cache;

  public ComicRepositoryImpl(ComicApi api, ComicCache cache) {
    this.api = api;
    this.cache = cache;
  }

  @Override
  @RxLogObservable
  public Observable<Comic> getComics() {

    Observable<Comic> cacheObservable = cache.listComics();
    Observable<Comic> network = listComicsApi();

    return cacheObservable
        .concatWith(network)
        .filter(comic -> comic != null)
        .doOnNext(comic -> cache.insertComic(comic));
  }

  @Override
  @RxLogObservable
  public Observable<List<Strip>> getStrips(String comic) {

    Observable<List<Strip>> cacheObservable = cache.listStrips(comic).toList();
    Observable<List<Strip>> network = getStripsApi(comic)
        .doOnNext(strip -> cache.insertStrip(strip))
        .toList();

    return cacheObservable
        .concatWith(network)
        .filter(strip -> strip != null);
  }


  public Observable<Strip> getStripsApi(String comic) {
    return api.listStrips(comic)
        .flatMap(result -> Observable.from(result.result));
  }

  @RxLogObservable
  private Observable<Comic> listComicsApi() {
    return api.listComics()
        .flatMap(result -> Observable.from(result.comics));
  }

}
