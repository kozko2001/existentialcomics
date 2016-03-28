package net.coscolla.comicstrip.push.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

import net.coscolla.comicstrip.R;
import net.coscolla.comicstrip.di.Graph;
import net.coscolla.comicstrip.push.PushManager;
import net.coscolla.comicstrip.push.api.PushRegisterRequestData;
import net.coscolla.comicstrip.push.api.PushRegisterResponse;
import net.coscolla.comicstrip.push.api.PushRestService;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

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
      public void onResponse(Call<PushRegisterResponse> call, Response<PushRegisterResponse> response) {
        if(response.body() != null) {
          String userId = response.body().id;
          pushManager.setUserId(userId);
        }
      }

      @Override
      public void onFailure(Call<PushRegisterResponse> call, Throwable t) {
        Timber.e(t, "Could not send the registration token to the server");
      }

    });
  }
}
