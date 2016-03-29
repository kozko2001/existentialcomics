package net.coscolla.comicstrip.di;

import net.coscolla.comicstrip.push.gcm.GcmMessageHandler;
import net.coscolla.comicstrip.push.gcm.RegistrationIntentService;

import dagger.Subcomponent;

@Subcomponent(modules = GcmMessageModule.class)
public interface GcmMessageComponent {
  void inject(GcmMessageHandler gcm);

  void inject(RegistrationIntentService intentService);
}
