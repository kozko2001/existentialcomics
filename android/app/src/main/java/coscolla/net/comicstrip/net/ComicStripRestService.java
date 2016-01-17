package coscolla.net.comicstrip.net;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ComicStripRestService {
  @GET("/comics")
  Call<ComicsResults> listComics();

  @GET("/comics/{comicname}")
  Call<StripResults> listStrips(@Path("comicname") String comicName);
}
