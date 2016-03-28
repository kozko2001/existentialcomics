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
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import net.coscolla.comicstrip.db.tables.ComicsTable;
import net.coscolla.comicstrip.db.tables.StripsTable;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

  public DatabaseOpenHelper(@NonNull Context context) {
    super(context, "db", null, 1);
  }

  @Override
  public void onCreate(@NonNull SQLiteDatabase db) {
    db.execSQL(ComicsTable.getCreateTableQuery());
    db.execSQL(StripsTable.getCreateTableQuery());
  }

  @Override
  public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
  }

}