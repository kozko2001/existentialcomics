package net.coscolla.comicstrip.net.push.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

import net.coscolla.comicstrip.R;
import net.coscolla.comicstrip.di.Graph;
import net.coscolla.comicstrip.net.push.PushManager;
import net.coscolla.comicstrip.net.push.PushRegisterRequestData;
import net.coscolla.comicstrip.net.push.PushRegisterResponse;
import net.coscolla.comicstrip.net.push.PushRestService;

import javax.inject.Inject;

import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationIntentService extends IntentService {

  private static final String LOGTAG = "RegistrationIntentServi";

  @Inject PushManager pushManager;
  @Inject PushRestService pushService;

  public RegistrationIntentService() {
    super(LOGTAG);
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    Graph.getInstance().getAppComponent().inject(this);
    pushManager.setUserId(null);

    try {
      String token = getTokenId();
      sendRegistrationToServer(token);
    } catch (IOException e) {
      Log.e(LOGTAG, "Registration service exception when registering the gcm token", e);
    }

  }

  /**
   * Gets the token id
   * @return a string with the device gcm token
   *
   * @throws IOException
   */
  private String getTokenId() throws IOException {
    InstanceID instanceID = InstanceID.getInstance(this);
    String senderId = getResources().getString(R.string.gcm_defaultSenderId);
    return instanceID.getToken(senderId, GoogleCloudMessaging.INSTANCE_ID_SCOPE);
  }


  /**
   * Use the push api to send the token and gets the user id
   *
   * @param token
   */
  private void sendRegistrationToServer(String token) {
    PushRegisterRequestData data = new PushRegisterRequestData(token);
    pushService.register(data).enqueue(new Callback<PushRegisterResponse>() {
      @Override
      public void onResponse(Response<PushRegisterResponse> response) {
        String userId = response.body().id;
        pushManager.setUserId(userId);
      }

      @Override
      public void onFailure(Throwable t) {
      }
    });
  }
}
