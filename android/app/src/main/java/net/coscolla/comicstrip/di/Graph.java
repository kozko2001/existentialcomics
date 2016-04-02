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
  private ListStripsComponent testListStripsComponent;
  private DetailStripsComponent testDetailStripsComponent;
  private ComicsActivityComponent testComicsComponent;
  private GcmMessageComponent testGcmMessageComponent;

  public static Graph getInstance() {
    if(instance == null) {
      instance = new Graph();
    }

    return instance;
  }

  public AppComponent getAppComponent() {
    return appComponent;
  }

  public void setAppComponent(AppComponent appComponent) {
    this.appComponent = appComponent;
  }

  public ListStripsComponent getListStripsComponent() {
    if(testListStripsComponent != null) {
      return testListStripsComponent;
    }

    return getAppComponent().plus(new ListStripsModule());
  }

  public DetailStripsComponent getDetailStripComponent() {
    if(testDetailStripsComponent != null) {
      return testDetailStripsComponent;
    }

    return getAppComponent().plus(new DetailStripsModule());
  }

  public ComicsActivityComponent getComicsComponent(ComicsActivity activity) {
    if(testComicsComponent != null) {
      return testComicsComponent;
    }

    return getAppComponent().plus(new ComicsModule(activity));
  }

  public GcmMessageComponent getGcmComponent() {
    if(testGcmMessageComponent != null) {
      return testGcmMessageComponent;
    }

    return getAppComponent().plus(new GcmMessageModule());
  }

  public DaggerAppComponent.Builder getAppComponentBuilder(Context appContext) {
    return DaggerAppComponent.builder()
        .appModule(new AppModule(appContext));
  }

  public void createAppComponent(Context appContext) {
    appComponent = getAppComponentBuilder(appContext).build();
  }

  public void setTestListStripsComponent(ListStripsComponent component) {
    this.testListStripsComponent = component;
  }

  public void setTestDetailStripsComponent(DetailStripsComponent testDetailStripsComponent) {
    this.testDetailStripsComponent = testDetailStripsComponent;
  }

  public void setTestComicsComponent(ComicsActivityComponent testComicsComponent) {
    this.testComicsComponent = testComicsComponent;
  }

  public void setTestGcmMessageComponent(GcmMessageComponent testGcmMessageComponent) {
    this.testGcmMessageComponent = testGcmMessageComponent;
  }
}
