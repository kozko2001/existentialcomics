package net.coscolla.comicstrip.ui.list;

import net.coscolla.comicstrip.push.PushManager;
import net.coscolla.comicstrip.push.api.PushRegisterRequestData;
import net.coscolla.comicstrip.push.api.PushRegisterResponse;
import net.coscolla.comicstrip.push.api.PushRestService;
import net.coscolla.comicstrip.usecases.PushSubscribeUseCase;

import rx.Observable;

import static rx.schedulers.Schedulers.io;

public class PushSubscribeUseCaseImpl implements PushSubscribeUseCase {

  private final PushManager pushManager;
  private final PushRestService api;

  public PushSubscribeUseCaseImpl(PushManager pushManager, PushRestService api) {
    this.pushManager = pushManager;
    this.api = api;
  }

  @Override
  public Observable<Boolean> subscribe(String topic) {
    return pushManager.subscribe(topic)
        .subscribeOn(io())
        .map(r -> true);
  }

  @Override
  public Observable<Boolean> unsubscribe(String topic) {
    return pushManager.unsubscribe(topic)
        .subscribeOn(io())
        .map(r -> true);
  }

  @Override
  public Observable<Boolean> toogleSubscribe(String topic) {
    if(isSubscribed(topic)) {
      return unsubscribe(topic);
    } else {
      return subscribe(topic);
    }
  }

  @Override
  public boolean isSubscribed(String topic) {
    return pushManager.isSubscribed(topic);
  }

  @Override
  public Observable<PushRegisterResponse> register(String token) {
    PushRegisterRequestData data = new PushRegisterRequestData(token);

    return api.register(data)
        .doOnNext(response -> {
          String userId = response.id;
          pushManager.setUserId(userId);
        });
  }
}
