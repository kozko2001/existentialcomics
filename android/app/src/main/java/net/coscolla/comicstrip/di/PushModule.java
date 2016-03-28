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

import net.coscolla.comicstrip.push.PushManager;
import net.coscolla.comicstrip.push.api.PushRestService;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class PushModule {

  @Provides
  public PushRestService providesComicApi(@Named("endpoint") String endpoint, OkHttpClient client) {
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(endpoint)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .client(client)
        .build();

    return retrofit.create(PushRestService.class);
  }

  @Provides
  public PushManager providesPushManager(Context appContext, PushRestService restService) {
    return new PushManager(appContext, restService);
  }
}
