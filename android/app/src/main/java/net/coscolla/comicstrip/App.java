package net.coscolla.comicstrip;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.kobakei.ratethisapp.RateThisApp;
import com.squareup.leakcanary.LeakCanary;

import net.coscolla.comicstrip.di.Graph;
import net.coscolla.comicstrip.push.PushManager;
import net.ypresto.timbertreeutils.CrashlyticsLogExceptionTree;

import javax.inject.Inject;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class App extends Application{

  @Inject  PushManager pushManager;

  @Override
  public void onCreate() {
    super.onCreate();
    Fabric.with(this, new Crashlytics());

    Graph.getInstance().createAppComponent(this);

    Graph.getInstance().getAppComponent().inject(this);

    LeakCanary.install(this);

    Stetho.initializeWithDefaults(this);

    setupLogging();

    setupPush();

    setupRate();
  }

  private void setupRate() {
    // Custom criteria: 3 days and 10 launches
    RateThisApp.Config config = new RateThisApp.Config(3, 10);
    RateThisApp.init(config);
  }

  private void setupLogging() {
    if (BuildConfig.DEBUG) {
      Timber.plant(new Timber.DebugTree());
    }
    Timber.plant(new CrashlyticsLogExceptionTree());
  }

  /***
   * Each time the app start up we register to our push notification service
   */
  private void setupPush() {
    pushManager.registerOrPing();
  }

}
