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

import net.coscolla.comicstrip.entities.Strip;
import net.coscolla.comicstrip.db.tables.StripsTable;


public class StripPutResolver extends DefaultPutResolver<Strip> {

  @NonNull
  @Override
  protected InsertQuery mapToInsertQuery(@NonNull Strip strip) {
    return InsertQuery.builder()
        .table(StripsTable.TABLE)
        .build();
  }

  @NonNull
  @Override
  protected UpdateQuery mapToUpdateQuery(@NonNull Strip strip) {
    return UpdateQuery.builder()
        .table(StripsTable.TABLE)
        .where(StripsTable.COLUMN_ID + " = ? ")
        .whereArgs(strip._id)
        .build();
  }

  @NonNull
  @Override
  protected ContentValues mapToContentValues(@NonNull Strip strip) {
    ContentValues cv = new ContentValues();
    cv.put(StripsTable.COLUMN_ID, strip._id);
    cv.put(StripsTable.COLUMN_COMIC, strip.comic);
    cv.put(StripsTable.COLUMN_ORDER, strip.order);
    cv.put(StripsTable.COLUMN_TEXT, strip.text);
    cv.put(StripsTable.COLUMN_TITLE, strip.title);
    cv.put(StripsTable.COLUMN_URL, strip.url);
    return cv;
  }
}
