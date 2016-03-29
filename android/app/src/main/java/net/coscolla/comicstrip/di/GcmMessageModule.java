package net.coscolla.comicstrip.di;

import net.coscolla.comicstrip.db.ComicCache;
import net.coscolla.comicstrip.net.api.ComicApi;
import net.coscolla.comicstrip.push.PushManager;
import net.coscolla.comicstrip.push.api.PushRestService;
import net.coscolla.comicstrip.push.gcm.PushReceiveUseCaseImpl;
import net.coscolla.comicstrip.ui.list.PushSubscribeUseCaseImpl;
import net.coscolla.comicstrip.usecases.PushReceiveUseCase;
import net.coscolla.comicstrip.usecases.PushSubscribeUseCase;

import dagger.Module;
import dagger.Provides;

@Module
public class GcmMessageModule {

  @Provides
  public PushReceiveUseCase providesUseCase(ComicApi api, ComicCache db) {
    return new PushReceiveUseCaseImpl(api, db);
  }

  @Provides
  public PushSubscribeUseCase providesSubscribeUseCase(PushManager pushManager, PushRestService api) {
    return new PushSubscribeUseCaseImpl(pushManager, api);
  }
}
