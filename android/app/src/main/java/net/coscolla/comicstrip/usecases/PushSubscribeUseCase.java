package net.coscolla.comicstrip.usecases;

import net.coscolla.comicstrip.push.api.PushRegisterResponse;

import rx.Observable;

public interface PushSubscribeUseCase {

  Observable<Boolean> subscribe(String topic);

  Observable<Boolean> unsubscribe(String topic);

  Observable<Boolean> toogleSubscribe(String topic);

  boolean isSubscribed(String topic);

  /**
   * Register the user in the push notification system
   * @param token string that represents the device in the gcm
   *
   * @return Observable with the response
   */
  Observable<PushRegisterResponse> register(String token);
}
