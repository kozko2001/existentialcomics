package net.coscolla.comicstrip.net.push.gcm;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

public class GcmInstanceIDRenewService extends InstanceIDListenerService {

  /***
   * When token is refreshed we reinitiate the registration
   */
  @Override
  public void onTokenRefresh() {
    Intent intent = new Intent(this, RegistrationIntentService.class);
    startService(intent);
  }
}
