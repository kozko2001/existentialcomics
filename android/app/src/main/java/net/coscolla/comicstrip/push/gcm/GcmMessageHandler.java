package net.coscolla.comicstrip.push.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;

import net.coscolla.comicstrip.R;
import net.coscolla.comicstrip.di.Graph;
import net.coscolla.comicstrip.ui.list.ListStripsActivity;
import net.coscolla.comicstrip.usecases.PushReceiveUseCase;

import javax.inject.Inject;

import timber.log.Timber;

public class GcmMessageHandler extends GcmListenerService {
  public static final int MESSAGE_NOTIFICATION_ID = 435345;

  @Inject PushReceiveUseCase useCase;

  @Override
  public void onMessageReceived(String from, Bundle data) {
    String comic = data.getString("comic");

    Graph.getInstance().getGcmComponent().inject(this);

    useCase.getLastStrip(comic)
        .subscribe(
            strip -> createNotification(comic, comic, strip.title),
            e -> Timber.e(e, "Could not get the last strip from network"));
  }

  /**
   * Create a new notification announcing to the user a new strip is available
   *
   * @param comic String of the id of the comic
   * @param title title of the notification
   * @param body text of the notification
   */
  private void createNotification(String comic, String title, String body) {
    Context context = getBaseContext();

    createIntent(comic);

    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle(title)
        .setContentText(body)
        .setContentIntent(createIntent(comic))
        .setAutoCancel(true);

    NotificationManager mNotificationManager = (NotificationManager) context
        .getSystemService(Context.NOTIFICATION_SERVICE);

    mNotificationManager.notify(getNotificationId(comic), mBuilder.build());
  }

  /**
   * The notification id is a different for each comic, so we can have multiple notification
   * one for each comic
   *
   * @param comic String that represent one comic
   * @return a number that is the same for the same comic but different
   */
  private int getNotificationId(String comic) {
    return MESSAGE_NOTIFICATION_ID + comic.hashCode();
  }

  private PendingIntent createIntent(String comic) {
    Intent intent = new Intent(this, ListStripsActivity.class);
    intent.putExtra(ListStripsActivity.INTENT_ARG_COMIC, comic);

    return PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        );
  }
}
