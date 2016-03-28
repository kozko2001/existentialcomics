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

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.operations.delete.DefaultDeleteResolver;
import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;

import net.coscolla.comicstrip.entities.Comic;
import net.coscolla.comicstrip.db.tables.ComicsTable;

public class ComicDeleteResolver extends DefaultDeleteResolver<Comic> {

  @NonNull
  @Override
  protected DeleteQuery mapToDeleteQuery(@NonNull Comic comic) {
    return DeleteQuery.builder()
        .table(ComicsTable.TABLE)
        .where(ComicsTable.COLUMN_NAME + " = ?")
        .whereArgs(comic.name)
        .build();
  }
}
