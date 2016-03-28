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

import net.coscolla.comicstrip.entities.Strip;
import net.coscolla.comicstrip.db.tables.StripsTable;

public class StripGetResolver extends DefaultGetResolver<Strip> {

  @NonNull
  @Override
  public Strip mapFromCursor(@NonNull Cursor cursor) {
    Strip strip = new Strip();

    String id = cursor.getString(cursor.getColumnIndexOrThrow(StripsTable.COLUMN_ID));
    String comic = cursor.getString(cursor.getColumnIndexOrThrow(StripsTable.COLUMN_COMIC));
    String text = cursor.getString(cursor.getColumnIndexOrThrow(StripsTable.COLUMN_TEXT));
    String title = cursor.getString(cursor.getColumnIndexOrThrow(StripsTable.COLUMN_TITLE));
    String url = cursor.getString(cursor.getColumnIndexOrThrow(StripsTable.COLUMN_URL));
    int order = cursor.getInt(cursor.getColumnIndexOrThrow(StripsTable.COLUMN_URL));

    strip._id = id;
    strip.comic = comic;
    strip.text = text;
    strip.title = title;
    strip.url = url;
    strip.order = order;

    return strip;
  }
}
