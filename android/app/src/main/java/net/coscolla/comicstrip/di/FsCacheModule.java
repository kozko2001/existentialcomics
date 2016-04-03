package net.coscolla.comicstrip.di;

import android.content.Context;

import net.coscolla.comicstrip.utils.RxFileUtils;

import dagger.Module;
import dagger.Provides;

@Module
public class FsCacheModule {
  @Provides
  public RxFileUtils providesFileCache(Context appContext) {
    return new RxFileUtils(appContext.getCacheDir());
  }
}
