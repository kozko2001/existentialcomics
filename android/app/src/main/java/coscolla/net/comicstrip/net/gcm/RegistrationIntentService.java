package coscolla.net.comicstrip.net.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

import coscolla.net.comicstrip.R;

public class RegistrationIntentService extends IntentService {

  private static final String LOGTAG = "RegIntentService";
  public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
  public static final String GCM_TOKEN = "gcmToken";

  public RegistrationIntentService() {
    super(LOGTAG);
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

    try {
      String token = getTokenId();
      Log.d(LOGTAG, "GCM Registration Token: " + token);
      sharedPreferences.edit().putString(GCM_TOKEN, token).apply();

      // pass along this data
      sendRegistrationToServer(token);
    } catch (IOException e) {
      Log.d(LOGTAG, "Failed to complete token refresh", e);
      sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, false).apply();
    }

  }

  private String getTokenId() throws IOException {
    InstanceID instanceID = InstanceID.getInstance(this);
    String senderId = getResources().getString(R.string.gcm_defaultSenderId);
    return instanceID.getToken(senderId, GoogleCloudMessaging.INSTANCE_ID_SCOPE);
  }


  private void sendRegistrationToServer(String token) {
    // TODO: Make request to server

    // TODO: When request is successful
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, true).apply();
  }
}
