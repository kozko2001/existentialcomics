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

import net.coscolla.comicstrip.net.comic.ComicApi;

import dagger.Module;
import dagger.Provides;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

@Module
public class ComicApiModule {

  private static final String COMICSTRIP_END_POINT = "http://192.168.11.9/";

  @Provides
  public ComicApi providesComicApi() {
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(COMICSTRIP_END_POINT)
        .addConverterFactory(GsonConverterFactory.create())
        .build();

    return retrofit.create(ComicApi.class);
  }
}
