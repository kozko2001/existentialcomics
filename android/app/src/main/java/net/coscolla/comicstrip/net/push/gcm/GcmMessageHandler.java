package net.coscolla.comicstrip.net.push.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;

import net.coscolla.comicstrip.R;
import net.coscolla.comicstrip.di.Graph;
import net.coscolla.comicstrip.net.comic.repository.ComicRepository;
import net.coscolla.comicstrip.ui.list.ListStripsActivity;

import javax.inject.Inject;

import timber.log.Timber;

import static rx.schedulers.Schedulers.io;

public class GcmMessageHandler extends GcmListenerService {
  public static final int MESSAGE_NOTIFICATION_ID = 435345;

  private static final String LOGTAG = "GcmMessageService";

  @Inject ComicRepository repository;

  @Override
  public void onMessageReceived(String from, Bundle data) {
    String message = data.getString("message");
    String comic = data.getString("comic");

    Graph.getInstance().getAppComponent().inject(this);

    repository.getStrips(comic)
        .subscribeOn(io())
        .subscribe(list -> {
          createNotification(comic, comic, list.get(0).title);
        }, (e) -> {
          Timber.e(e, "GcmMessageService error fetching the data for comic " + comic);
        }, () -> {

        });
  }

  // Creates notification based on title and body received
  private void createNotification(String comic, String title, String body) {
    Context context = getBaseContext();

    createIntent(comic);

    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
        .setSmallIcon(R.mipmap.ic_launcher).setContentTitle(title)
        .setContentText(body)
        .setContentIntent(createIntent(comic))
        .setAutoCancel(true);

    NotificationManager mNotificationManager = (NotificationManager) context
        .getSystemService(Context.NOTIFICATION_SERVICE);

    mNotificationManager.notify(MESSAGE_NOTIFICATION_ID, mBuilder.build());
  }

  private PendingIntent createIntent(String comic) {
    Intent intent = new Intent(this, ListStripsActivity.class);
    intent.putExtra(ListStripsActivity.INTENT_ARG_COMIC_NAME, comic);

    return PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        );
  }
}
