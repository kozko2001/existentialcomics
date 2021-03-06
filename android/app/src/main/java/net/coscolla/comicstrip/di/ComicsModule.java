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

import net.coscolla.comicstrip.db.ComicCache;
import net.coscolla.comicstrip.net.api.ComicApi;
import net.coscolla.comicstrip.push.PushManager;
import net.coscolla.comicstrip.ui.comics.ComicAdapter;
import net.coscolla.comicstrip.ui.comics.ComicsActivity;
import net.coscolla.comicstrip.ui.comics.ListComicsUseCaseImpl;
import net.coscolla.comicstrip.usecases.ListComicsUseCase;

import dagger.Module;
import dagger.Provides;

@Module
public class ComicsModule {

  private final ComicsActivity activity;

  public ComicsModule(ComicsActivity activity) {
    this.activity = activity;
  }

  @Provides
  public ComicsActivity providesActivity() {
    return activity;
  }

  @Provides
  public ComicAdapter providesComicAdapter() {
    return new ComicAdapter();
  }

  @Provides
  public ListComicsUseCase providesUseCase(ComicCache cache, ComicApi api, PushManager pushManager) {
    return new ListComicsUseCaseImpl(cache, api, pushManager);
  }
}
