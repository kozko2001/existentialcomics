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

package net.coscolla.comicstrip.net.push;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import net.coscolla.comicstrip.net.push.gcm.RegistrationIntentService;

import java.util.ArrayList;

import javax.inject.Inject;

import retrofit2.Callback;
import retrofit2.Response;

public class PushManager {

  private static final String LOGTAG = "PushManager";
  private final Context context;
  private static final String PUSH_USER_ID = "PUSH_USER_ID";

  /**
   * Array of topics that are not yet subscribed
   */
  private final ArrayList<String> pendingTopics;

  @Inject PushRestService pushApi;

  public PushManager(Context appContext) {
    context = appContext;
    pendingTopics = new ArrayList<String>();
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
  public void subscribe(@NonNull final String topic) {
    if(getUserId() == null) {
      pendingTopics.add(topic);
    } else {
      pushApi.subscribe(getUserId(), topic).enqueue(new Callback<SubcribeResult>() {
        @Override
        public void onResponse(Response<SubcribeResult> response) {
          Log.d(LOGTAG, "subscribed successfully to topic " + topic);
        }

        @Override
        public void onFailure(Throwable t) {
          Log.d(LOGTAG, "failed on subscribing to topic " + topic);
        }
      });
    }
  }

  /**
   * Pings the push server to maintain alive the notifications
   */
  private void ping() {
    pushApi.ping(getUserId()).enqueue(new Callback<SubcribeResult>() {
      @Override
      public void onResponse(Response<SubcribeResult> response) {
        Log.d(LOGTAG, "on ping request success");
      }

      @Override
      public void onFailure(Throwable t) {
        Log.d(LOGTAG, "on ping request failed", t);
      }
    });
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
    sharedPreferences.edit().putString(PUSH_USER_ID, userId);
  }

}
