package coscolla.net.comicstrip;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import coscolla.net.comicstrip.net.ComicStripRestService;
import coscolla.net.comicstrip.net.ComicsResults;
import coscolla.net.comicstrip.net.StripResults;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ListStripsActivity extends AppCompatActivity {

  private static final String LOGTAG = "ListStripsActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_list_strips);

    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl("http://46.101.199.221/")
        .addConverterFactory(GsonConverterFactory.create())
        .build();

    ComicStripRestService service = retrofit.create(ComicStripRestService.class);
    service.listStrips("existentialcomics").enqueue(new Callback<StripResults>() {
      @Override
      public void onResponse(Response<StripResults> response) {
        Log.d(LOGTAG, "");
      }

      @Override
      public void onFailure(Throwable t) {
        Log.d(LOGTAG, "onFailure");
      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.list_strips_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if(item.getItemId() == R.id.menu_open_browser) {
      Log.d(LOGTAG, "open on browser 22");
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
