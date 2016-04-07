package net.coscolla.comicstrip.marketing.analytics;

import net.coscolla.comicstrip.entities.Strip;

/**
 * Does nothing is an empty implementation for debug :)
 */

public class AnaltyticsManagerDebug implements IAnalytics {
  @Override
  public void eventComicSubscribed(String comicId) {

  }

  @Override
  public void eventStripViewed(Strip strip) {

  }

  @Override
  public void eventNotificationReceived(String comicId, Strip strip) {

  }

  @Override
  public void eventNotificationOpened(String comicId, Strip strip) {

  }

  @Override
  public void eventStripListViewed(String comic) {

  }
}
