package net.coscolla.comicstrip.marketing.analytics;

import net.coscolla.comicstrip.entities.Strip;

public interface IAnalytics {

  void eventComicSubscribed(String comicId);

  void eventStripViewed(Strip strip);

  void eventNotificationReceived(String comicId, Strip strip);

  void eventNotificationOpened(String comicId, Strip strip);

  void eventStripListViewed(String comic);
}
