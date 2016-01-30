package net.coscolla.comicstrip.net.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

import net.coscolla.comicstrip.R;
import net.coscolla.comicstrip.net.PushRegisterRequestData;
import net.coscolla.comicstrip.net.PushRegisterResponse;
import net.coscolla.comicstrip.net.PushRestService;
import net.coscolla.comicstrip.net.SubcribeResult;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

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

    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl("http://192.168.11.9/")
        .addConverterFactory(GsonConverterFactory.create())
        .build();

    final PushRestService service = retrofit.create(PushRestService.class);
    PushRegisterRequestData data = new PushRegisterRequestData(token);
    service.register(data).enqueue(new Callback<PushRegisterResponse>() {
      @Override
      public void onResponse(Response<PushRegisterResponse> response) {

        Log.d(LOGTAG, "OK!!!");
        String userId = response.body().id;
        subscribeTo(userId, "existentialcomics", service);
        // TODO: When request is successful
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(RegistrationIntentService.this);
        sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, true).apply();
      }

      @Override
      public void onFailure(Throwable t) {
        Log.d(LOGTAG, "FAIL !!!"  + t);
      }
    });

  }

  private void subscribeTo(String userId, String topic, PushRestService service) {
      service.subscribe(userId, topic).enqueue(new Callback<SubcribeResult>() {
        @Override
        public void onResponse(Response<SubcribeResult> response) {
          Log.d(LOGTAG, "OK!!!");
        }

        @Override
        public void onFailure(Throwable t) {
          Log.d(LOGTAG, "FAILED!!! !!!");
        }
      });
  }
}
