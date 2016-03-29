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
import net.coscolla.comicstrip.net.api.UrlBuilder;
import net.coscolla.comicstrip.push.PushManager;
import net.coscolla.comicstrip.ui.list.ListStripsUseCaseImpl;
import net.coscolla.comicstrip.ui.list.PushSubscribeUseCaseImpl;
import net.coscolla.comicstrip.ui.list.adapter.StripAdapter;
import net.coscolla.comicstrip.usecases.ListStripsUseCase;
import net.coscolla.comicstrip.usecases.PushSubscribeUseCase;

import dagger.Module;
import dagger.Provides;

@Module
public class ListStripsModule {
  @Provides
  public StripAdapter providesAdapter(UrlBuilder urlBuilder) {
    return new StripAdapter(urlBuilder);
  }

  @Provides
  public ListStripsUseCase providesUseCase(ComicCache comicCache, ComicApi comicApi) {
    return new ListStripsUseCaseImpl(comicCache, comicApi);
  }

  @Provides
  public PushSubscribeUseCase providesPushUseCase(PushManager pushManager) {
    return new PushSubscribeUseCaseImpl(pushManager);
  }
}
