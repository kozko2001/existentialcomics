package net.coscolla.comicstrip.push.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

import net.coscolla.comicstrip.R;
import net.coscolla.comicstrip.di.Graph;
import net.coscolla.comicstrip.usecases.PushSubscribeUseCase;

import javax.inject.Inject;
import timber.log.Timber;

import static rx.schedulers.Schedulers.io;

public class RegistrationIntentService extends IntentService {

  @Inject PushSubscribeUseCase useCase;

  public RegistrationIntentService() {
    super("RegistrationIntentServ");
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    Graph.getInstance().getGcmComponent().inject(this);

    try {
      String token = getTokenId();
      sendRegistrationToServer(token);
    } catch (IOException e) {
      Timber.e(e, "Registration service exception when registering the gcm token");
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
   * @param token token from the {@link #getTokenId()}
   */
  private void sendRegistrationToServer(String token) {
    useCase.register(token)
        .subscribeOn(io())
        .subscribe(
            response -> Timber.d("register response received: id %s", response.id),
            e -> Timber.e(e, "register push notification request failed")
        );
  }
}
