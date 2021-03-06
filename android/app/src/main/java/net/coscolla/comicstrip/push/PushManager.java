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

package net.coscolla.comicstrip.push;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import net.coscolla.comicstrip.push.api.PushRestService;
import net.coscolla.comicstrip.push.api.SubscribeResult;
import net.coscolla.comicstrip.push.gcm.RegistrationIntentService;

import java.util.HashSet;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import timber.log.Timber;

import static rx.schedulers.Schedulers.io;

public class PushManager {

  private static final String LOGTAG = "PushManager";
  private final Context context;
  private static final String PUSH_USER_ID = "PUSH_USER_ID";
  private static final String TOPICS_SUBSCRIBED = "TOPICS_SUBSCRIBED";

  private final PushRestService pushApi;

  public PushManager(Context appContext, PushRestService restService) {
    context = appContext;
    this.pushApi = restService;
  }

  /**
   * This methods registers the user if is not already registered, if the user is
   * registered to the push system, just ping to let the system know that the user still
   * valid
   */
  public void registerOrPing() {
    if(getUserId() == null) {
      register();
    } else {
      ping();
    }
  }

  /**
   * Subscribe the current user to a topic
   *
   * if the user is not currently set, we add the topic to pending topics and when the user id
   * is filled execute the subscribes.
   *
   * TODO: Handle when no connectivity with the push service and not really subscribed
   * @param topic
   */
  public Observable<SubscribeResult> subscribe(@NonNull final String topic) {
      return pushApi.subscribe(getUserId(), topic)
          .doOnNext(result -> setSubscribedTo(topic));
    }

  /**
   * Unsubscribes from the backend from a previously subscribed topic
   *
   * @param topic
   * @return Observable with the results
   */
  public Observable<SubscribeResult> unsubscribe(@NonNull final String topic) {
    return pushApi.unsubscribe(getUserId(), topic)
        .doOnNext(result -> removeSubscribedTo(topic));
  }



  /**
   * When we have been subscribed to some topic store it in the local
   *
   * @param topic on to be subscribed
   */
  private void setSubscribedTo(String topic) {
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    Set<String> subscribed = new HashSet<>(sharedPreferences.getStringSet(TOPICS_SUBSCRIBED, new HashSet<>()));

    subscribed.add(topic);

    sharedPreferences.edit().putStringSet(TOPICS_SUBSCRIBED, subscribed).commit();
  }

  /**
   * Remove from the local setting that we are no longer subscribed to this topic
   *
   * @param topic name of the comic
   */
  private void removeSubscribedTo(String topic) {
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    Set<String> subscribed = new HashSet<>(sharedPreferences.getStringSet(TOPICS_SUBSCRIBED, new HashSet<>()));

    subscribed.remove(topic);

    sharedPreferences.edit().putStringSet(TOPICS_SUBSCRIBED, subscribed).commit();
  }

  public boolean isSubscribed(String topic) {
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    Set<String> subscribed = sharedPreferences.getStringSet(TOPICS_SUBSCRIBED, new HashSet<>());
    return subscribed.contains(topic);
  }

  /**
   * Pings the push server to maintain alive the notifications
   */
  private void ping() {
    pushApi.ping(getUserId())
        .subscribeOn(io())
        .subscribe(
            result -> Timber.d("on ping request success"),
            e -> Timber.e(e, "on ping request failed"));
  }

  /**
   * Sends an intent to the registration intent service
   */
  public void register() {
    Intent intent = new Intent(context, RegistrationIntentService.class);
    context.startService(intent);
  }

  /**
   * Returns the user id for push notification if already registered
   */
  public String getUserId() {
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    return sharedPreferences.getString(PUSH_USER_ID, null);
  }

  /**
   * When registered successfully we set the userId and store it so we have not to register again
   * just make the ping request
   */
  public void setUserId(String userId) {
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    sharedPreferences.edit().putString(PUSH_USER_ID, userId).apply();
  }

}
