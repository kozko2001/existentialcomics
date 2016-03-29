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
import net.coscolla.comicstrip.net.api.UrlBuilder;
import net.coscolla.comicstrip.ui.detail.DetailStripUseCaseImpl;
import net.coscolla.comicstrip.usecases.DetailStripUseCase;

import dagger.Module;
import dagger.Provides;

@Module
public class DetailStripsModule {

  @Provides
  DetailStripUseCase providesUseCase(UrlBuilder urlBuilder, ComicCache cache) {
    return new DetailStripUseCaseImpl(urlBuilder, cache);
  }
}
