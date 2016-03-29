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

package net.coscolla.comicstrip.db;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fernandocejas.frodo.annotation.RxLogObservable;
import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio.sqlite.impl.DefaultStorIOSQLite;
import com.pushtorefresh.storio.sqlite.queries.Query;

import net.coscolla.comicstrip.entities.Comic;
import net.coscolla.comicstrip.entities.Strip;
import net.coscolla.comicstrip.db.resolvers.ComicDeleteResolver;
import net.coscolla.comicstrip.db.resolvers.ComicGetResolver;
import net.coscolla.comicstrip.db.resolvers.ComicPutResolver;
import net.coscolla.comicstrip.db.resolvers.StripGetResolver;
import net.coscolla.comicstrip.db.resolvers.StripPutResolver;
import net.coscolla.comicstrip.db.resolvers.StripsDeleteResolver;
import net.coscolla.comicstrip.db.tables.ComicsTable;
import net.coscolla.comicstrip.db.tables.StripsTable;

import java.util.List;

import rx.Observable;

public class ComicCache {

  private final DefaultStorIOSQLite storio;

  public ComicCache(Context context) {
    storio = initializeDatabase(context);
  }

  /**
   * Initializes the storio library indicating which are the mapping for each type in the
   * database and with each operation (insert, select, delete)
   *
   * @param context application context to access to the database
   * @return StorIO configuration
   */
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


  /**
   * Fetches the database and gets the current comics
   *
   * @return list of comics that are cached into the database
   */
  @NonNull
  public Observable<List<Comic>> listComics() {
    return storio.get()
        .listOfObjects(Comic.class)
        .withQuery(Query.builder()
            .table(ComicsTable.TABLE)
            .build())
        .prepare()
        .asRxObservable();
  }

  /**
   * Inserts a list of comics to the database if need
   *
   * @param comics list of comics to be added to the cache
   */
  public void insertComics(@NonNull  List<Comic> comics) {
      storio.put()
          .objects(comics)
          .prepare()
          .executeAsBlocking();
  }

  /**
   * Fetches the database for the current strips cached and maintain a subscription to notify when
   * new data is inserted
   *
   * @param comic the comic to filter
   * @return an observable of the database that triggers a list of all strips each time something
   * changes in the datable that affect this comic
   */
  @RxLogObservable
  @NonNull
  public Observable<List<Strip>> listStrips(@NonNull  String comic) {
    return storio.get()
        .listOfObjects(Strip.class)
        .withQuery(cacheStripsSortedByOrderQuery(comic).build())
        .prepare()
        .asRxObservable();
  }

  /**
   * Insert a new list of strips
   *
   * If they are already inserted, just update them
   *
   * @param strips strip model list to be inserted
   */
  public void insertStrips(@NonNull List<Strip> strips) {
    storio.put()
        .objects(strips)
        .prepare()
        .executeAsBlocking();
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
   * @param comic name of the comic to find strips
   * @return the id of the most recent strip stored in the cache or null if no strip is cached at all
   */
  @Nullable
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

  /**
   * Returns the strip item already stored in the database in a form of a Observable
   *
   * @param id id of the strip we want to get from the database
   * @return An observable with the strip or with a null inside
   */
  public Observable<Strip> getStripById(String id) {
    return storio.get()
        .object(Strip.class)
        .withQuery(Query.builder()
            .table(StripsTable.TABLE)
            .where(StripsTable.COLUMN_ID + " = ? ")
            .whereArgs(id)
            .build()
        ).prepare()
        .asRxObservable()
        .first();
  }
}
