package coscolla.net.comicstrip.net;

import java.util.List;

public class StripResults {

  public List<StripData> result;

  public class StripData {
    public String _id;
    public String title;
    public String text;
    public String url;
    public String comic;
  }
}

