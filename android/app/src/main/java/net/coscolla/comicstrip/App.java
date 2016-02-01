package net.coscolla.comicstrip;

import android.app.Application;

import net.coscolla.comicstrip.di.Graph;
import net.coscolla.comicstrip.net.push.PushManager;

import javax.inject.Inject;

public class App extends Application{

  @Inject  PushManager pushManager;

  @Override
  public void onCreate() {
    super.onCreate();

    Graph.getInstance().createAppComponent(this);

    Graph.getInstance().getAppComponent().inject(this);

    setupPush();
  }

  /***
   * Each time the app start up we register to our push notification service
   */
  private void setupPush() {
    pushManager.registerOrPing();
  }

}
