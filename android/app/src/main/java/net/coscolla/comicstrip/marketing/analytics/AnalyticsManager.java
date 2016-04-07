package net.coscolla.comicstrip.marketing.analytics;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;

import net.coscolla.comicstrip.entities.Strip;

public class AnalyticsManager implements IAnalytics {

  @Override
  public void eventComicSubscribed(String comicId) {
    Answers.getInstance().logContentView(new ContentViewEvent()
        .putContentName(comicId)
        .putContentType("Subscribe")
        .putContentId(comicId));
  }

  @Override
  public void eventStripViewed(Strip strip) {
    Answers.getInstance().logContentView(new ContentViewEvent()
        .putContentName(strip.title)
        .putContentType("Strip")
        .putContentId(strip.url));
  }

  @Override
  public void eventNotificationReceived(String comicId, Strip strip) {
    Answers.getInstance().logContentView(new ContentViewEvent()
        .putContentName("NotificationsReceived - " + strip.title)
        .putContentType("NotificationReceived")
        .putContentId(strip.url));

  }

  @Override
  public void eventNotificationOpened(String comicId, Strip strip) {
    Answers.getInstance().logContentView(new ContentViewEvent()
        .putContentName("NotificationsOpened - " + strip.title)
        .putContentType("NotificationsOpened")
        .putContentId(strip.url));
  }

  @Override
  public void eventStripListViewed(String comic) {
    Answers.getInstance().logContentView(new ContentViewEvent()
        .putContentName("Strip list " + comic)
        .putContentType("Subscribe")
        .putContentId(comic));
  }
}
