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

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.operations.get.DefaultGetResolver;

import net.coscolla.comicstrip.entities.Comic;
import net.coscolla.comicstrip.db.tables.ComicsTable;

public class ComicGetResolver extends DefaultGetResolver<Comic> {

  @NonNull
  @Override
  public Comic mapFromCursor(@NonNull Cursor cursor) {
    Comic comic = new Comic();

    String name = cursor.getString(cursor.getColumnIndexOrThrow(ComicsTable.COLUMN_NAME));
    String id = cursor.getString(cursor.getColumnIndexOrThrow(ComicsTable.COLUMN_ID));
    String image = cursor.getString(cursor.getColumnIndexOrThrow(ComicsTable.COLUMN_IMAGE));
    String url = cursor.getString(cursor.getColumnIndexOrThrow(ComicsTable.COLUMN_URL));

    comic.name = name;
    comic.url = url;
    comic.image = image;
    comic.comic_id = id;

    return comic;
  }
}
