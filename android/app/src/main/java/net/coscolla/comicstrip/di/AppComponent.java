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

import net.coscolla.comicstrip.App;
import net.coscolla.comicstrip.net.api.ComicApi;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
    modules = {
        AppModule.class,
        ComicApiModule.class,
        PushModule.class,
        DatabaseModule.class,
        HttpClientModule.class,
        FsCacheModule.class,
        AnalyticsModule.class
    }
)
public interface AppComponent {

  void inject(App app);

  ComicApi getComicApi();

  ListStripsComponent plus(ListStripsModule listStripsModule);

  DetailStripsComponent plus(DetailStripsModule detailStripsModule);

  ComicsActivityComponent plus(ComicsModule comicsModule);

  GcmMessageComponent plus(GcmMessageModule module);
}

