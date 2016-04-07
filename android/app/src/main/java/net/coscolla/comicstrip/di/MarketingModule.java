package net.coscolla.comicstrip.di;

import com.crashlytics.android.answers.BuildConfig;

import net.coscolla.comicstrip.marketing.analytics.AnaltyticsManagerDebug;
import net.coscolla.comicstrip.marketing.analytics.AnalyticsManager;
import net.coscolla.comicstrip.marketing.analytics.IAnalytics;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class MarketingModule {

  @Provides @Singleton
  public IAnalytics providesAnaltytics() {
    if(BuildConfig.DEBUG) {
      return new AnaltyticsManagerDebug();
    } else {
      return new AnalyticsManager();
    }
  }
}
