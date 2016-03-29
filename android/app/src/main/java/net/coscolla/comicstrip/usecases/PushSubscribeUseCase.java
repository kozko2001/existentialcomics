package net.coscolla.comicstrip.usecases;

import rx.Observable;

public interface PushSubscribeUseCase {

  Observable<Boolean> subscribe(String topic);

  Observable<Boolean> unsubscribe(String topic);

  Observable<Boolean> toogleSubscribe(String topic);

  boolean isSubscribed(String topic);
}
