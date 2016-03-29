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

import net.coscolla.comicstrip.ui.comics.ComicsActivity;

/**
 * Class to store all the different graphs of dependency injection
 */
public class Graph {

  private static Graph instance;
  private AppComponent appComponent;

  public static Graph getInstance() {
    if(instance == null) {
      instance = new Graph();
    }

    return instance;
  }

  public AppComponent getAppComponent() {
    return appComponent;
  }

  public ListStripsComponent getListStripsComponent() {
    return getAppComponent().plus(new ListStripsModule());
  }

  public DetailStripsComponent getDetailStripComponent() {
    return getAppComponent().plus(new DetailStripsModule());
  }

  public ComicsActivityComponent getComicsComponent(ComicsActivity activity) {
    return getAppComponent().plus(new ComicsModule(activity));
  }

  public GcmMessageComponent getGcmComponent() {
    return getAppComponent().plus(new GcmMessageModule());
  }

  public void createAppComponent(Context appContext) {
    appComponent = DaggerAppComponent.builder()
        .appModule(new AppModule(appContext))
        .build();
  }

}
