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

package net.coscolla.comicstrip.net.comic.db.tables;

public class StripsTable {

  public static final String TABLE = "strips";

  public static final String COLUMN_ID = "_id";
  public static final String COLUMN_COMIC = "comic";
  public static final String COLUMN_ORDER = "_order";
  public static final String COLUMN_TEXT = "text";
  public static final String COLUMN_TITLE = "title";
  public static final String COLUMN_URL = "url";

  public static String getCreateTableQuery() {
    return "CREATE TABLE " + TABLE + " ( "
        + COLUMN_COMIC + " TEXT NOT NULL, "
        + COLUMN_ID + " TEXT NOT NULL, "
        + COLUMN_ORDER + " INT NOT NULL, "
        + COLUMN_TEXT + " TEXT, "
        + COLUMN_TITLE + " TEXT, "
        + COLUMN_URL + " TEXT "
        + ");";
  }
}
