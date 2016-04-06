package net.coscolla.comicstrip.di;

import com.crashlytics.android.answers.BuildConfig;

import net.coscolla.comicstrip.analytics.AnaltyticsManagerDebug;
import net.coscolla.comicstrip.analytics.AnalyticsManager;
import net.coscolla.comicstrip.analytics.IAnalytics;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AnalyticsModule {

  @Provides @Singleton
  public IAnalytics providesAnaltytics() {
    if(BuildConfig.DEBUG) {
      return new AnaltyticsManagerDebug();
    } else {
      return new AnalyticsManager();
    }
  }
}
