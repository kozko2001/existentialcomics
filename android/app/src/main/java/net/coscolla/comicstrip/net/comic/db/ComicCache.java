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

package net.coscolla.comicstrip.net.comic.db;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.fernandocejas.frodo.annotation.RxLogObservable;
import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio.sqlite.impl.DefaultStorIOSQLite;
import com.pushtorefresh.storio.sqlite.queries.Query;

import net.coscolla.comicstrip.net.comic.api.entities.Comic;
import net.coscolla.comicstrip.net.comic.api.entities.Strip;
import net.coscolla.comicstrip.net.comic.db.resolvers.ComicDeleteResolver;
import net.coscolla.comicstrip.net.comic.db.resolvers.ComicGetResolver;
import net.coscolla.comicstrip.net.comic.db.resolvers.ComicPutResolver;
import net.coscolla.comicstrip.net.comic.db.resolvers.StripGetResolver;
import net.coscolla.comicstrip.net.comic.db.resolvers.StripPutResolver;
import net.coscolla.comicstrip.net.comic.db.resolvers.StripsDeleteResolver;
import net.coscolla.comicstrip.net.comic.db.tables.ComicsTable;
import net.coscolla.comicstrip.net.comic.db.tables.StripsTable;

import java.util.List;

import rx.Observable;

public class ComicCache {

  private final DefaultStorIOSQLite storio;

  public ComicCache(Context context) {
    storio = initializeDatabase(context);
  }

  private DefaultStorIOSQLite initializeDatabase(Context context) {
    DatabaseOpenHelper db = new DatabaseOpenHelper(context);
    return DefaultStorIOSQLite.builder()
        .sqliteOpenHelper(db)
        .addTypeMapping(Comic.class, SQLiteTypeMapping.<Comic>builder()
            .putResolver(new ComicPutResolver())
            .getResolver(new ComicGetResolver())
            .deleteResolver(new ComicDeleteResolver())
            .build())
        .addTypeMapping(Strip.class, SQLiteTypeMapping.<Strip>builder()
            .putResolver(new StripPutResolver())
            .getResolver(new StripGetResolver())
            .deleteResolver(new StripsDeleteResolver())
            .build())
        .build();
  }


  public Observable<Comic> listComics() {
    return storio.get()
        .listOfObjects(Comic.class)
        .withQuery(Query.builder()
            .table(ComicsTable.TABLE)
            .build())
        .prepare()
        .asRxObservable()
        .first()
        .flatMap(Observable::from);
  }

  public void insertComic(Comic comic) {
      storio.put()
          .object(comic)
          .prepare()
          .executeAsBlocking();
  }

  @RxLogObservable
  public Observable<List<Strip>> listStrips(String comic) {
    return storio.get()
        .listOfObjects(Strip.class)
        .withQuery(cacheStripsSortedByOrderQuery(comic).build())
        .prepare()
        .asRxObservable();
  }

  @NonNull
  private Query.CompleteBuilder cacheStripsSortedByOrderQuery(String comic) {
    return Query.builder()
        .table(StripsTable.TABLE)
        .where(StripsTable.COLUMN_COMIC + " = ? ")
        .whereArgs(comic)
        .orderBy(StripsTable.COLUMN_ORDER + " DESC");
  }

  /**
   * Obtains the most new id strip from the cache or null if cache is empty
   *
   * @param comic
   * @return
   */
  public String lastStripId(String comic) {
    Strip firstStrip = storio.get()
        .object(Strip.class)
        .withQuery(cacheStripsSortedByOrderQuery(comic)
            .limit(1)
            .build())
        .prepare()
        .executeAsBlocking();

    if(firstStrip != null) {
      return firstStrip._id;
    } else {
      return null;
    }
  }

  public void insertStrips(List<Strip> strips) {
    storio.put()
        .objects(strips)
        .prepare()
        .executeAsBlocking();
  }
}
