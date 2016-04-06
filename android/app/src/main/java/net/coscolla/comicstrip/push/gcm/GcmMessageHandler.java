package net.coscolla.comicstrip.push.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;

import net.coscolla.comicstrip.R;
import net.coscolla.comicstrip.analytics.IAnalytics;
import net.coscolla.comicstrip.di.Graph;
import net.coscolla.comicstrip.entities.Comic;
import net.coscolla.comicstrip.entities.Strip;
import net.coscolla.comicstrip.ui.list.ListStripsActivity;
import net.coscolla.comicstrip.usecases.PushReceiveUseCase;

import org.parceler.Parcels;

import javax.inject.Inject;

import timber.log.Timber;

public class GcmMessageHandler extends GcmListenerService {
  public static final int MESSAGE_NOTIFICATION_ID = 435345;

  @Inject PushReceiveUseCase useCase;

  @Inject IAnalytics analytics;

  @Override
  public void onMessageReceived(String from, Bundle data) {
    String comicId = data.getString("comic");

    Graph.getInstance().getGcmComponent().inject(this);

    Comic comic = useCase.getComic(comicId).toBlocking().single();
    useCase.getLastStrip(comicId)
        .subscribe(
            strip -> createNotification(comic, strip),
            e -> Timber.e(e, "Could not get the last strip from network"));
  }

  /**
   * Create a new notification announcing to the user a new strip is available
   * @param comic Comic entity that the notification has said there is new info
   * @param strip Last strip that we show to the notification
   */
  private void createNotification(Comic comic, Strip strip) {
    Context context = getBaseContext();

    PendingIntent intent = createIntent(comic, strip);

    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle(comic.name)
        .setContentText(strip.title)
        .setContentIntent(intent)
        .setAutoCancel(true);

    NotificationManager mNotificationManager = (NotificationManager) context
        .getSystemService(Context.NOTIFICATION_SERVICE);

    mNotificationManager.notify(getNotificationId(comic.comic_id), mBuilder.build());

    analytics.eventNotificationReceived(comic.comic_id, strip);
  }

  /**
   * The notification id is a different for each comic, so we can have multiple notification
   * one for each comic
   *
   * @param comic_id String that represent one comic
   * @return a number that is the same for the same comic but different
   */
  private int getNotificationId(String comic_id) {
    return MESSAGE_NOTIFICATION_ID + comic_id.hashCode();
  }

  private PendingIntent createIntent(Comic comic, Strip strip) {
    Intent intent = new Intent(this, ListStripsActivity.class);
    intent.putExtra(ListStripsActivity.INTENT_ARG_COMIC, Parcels.wrap(comic));
    intent.putExtra(ListStripsActivity.INTENT_ARG_FROM_NOTIFICATION, true);
    intent.putExtra(ListStripsActivity.INTENT_ARG_FROM_NOTIFICATION_STRIP, Parcels.wrap(strip));

    return PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        );
  }
}
