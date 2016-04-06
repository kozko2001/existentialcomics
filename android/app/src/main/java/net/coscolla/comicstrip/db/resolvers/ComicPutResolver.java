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

package net.coscolla.comicstrip.db.resolvers;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.operations.put.DefaultPutResolver;
import com.pushtorefresh.storio.sqlite.queries.InsertQuery;
import com.pushtorefresh.storio.sqlite.queries.UpdateQuery;

import net.coscolla.comicstrip.entities.Comic;
import net.coscolla.comicstrip.db.tables.ComicsTable;

public class ComicPutResolver extends DefaultPutResolver<Comic> {

  @NonNull
  @Override
  protected InsertQuery mapToInsertQuery(@NonNull Comic comic) {
    return InsertQuery.builder()
        .table(ComicsTable.TABLE)
        .build();
  }

  @NonNull
  @Override
  protected UpdateQuery mapToUpdateQuery(@NonNull Comic comic) {
    return UpdateQuery.builder()
        .table(ComicsTable.TABLE)
        .where(ComicsTable.COLUMN_ID + " = ? ")
        .whereArgs(comic.comic_id)
        .build();
  }

  @NonNull
  @Override
  protected ContentValues mapToContentValues(@NonNull Comic comic) {
    ContentValues cv = new ContentValues();
    cv.put(ComicsTable.COLUMN_NAME, comic.name);
    cv.put(ComicsTable.COLUMN_ID, comic.comic_id);
    cv.put(ComicsTable.COLUMN_URL, comic.url);
    cv.put(ComicsTable.COLUMN_IMAGE, comic.image);
    return cv;
  }
}
