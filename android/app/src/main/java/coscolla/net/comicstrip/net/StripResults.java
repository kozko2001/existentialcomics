package coscolla.net.comicstrip.net;

import java.util.List;

public class StripResults {

  public List<StripData> result;

  class StripData {
    String _id;
    String title;
    String text;
    String url;
    String comic;
  }
}

