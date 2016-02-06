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

package net.coscolla.comicstrip.di;

import android.content.Context;

import net.coscolla.comicstrip.net.comic.api.ComicApi;
import net.coscolla.comicstrip.net.comic.db.ComicCache;
import net.coscolla.comicstrip.net.comic.repository.ComicRepositoryImpl;
import net.coscolla.comicstrip.net.comic.repository.ComicRepository;

import dagger.Module;
import dagger.Provides;

@Module
public class RepositoryModule {

  @Provides
  public ComicCache providesCache(Context appContext) {
    return new ComicCache(appContext);
  }

  @Provides
  public ComicRepository providesRepository(ComicApi api, ComicCache cache) {
    return new ComicRepositoryImpl(api, cache);
  }
}
