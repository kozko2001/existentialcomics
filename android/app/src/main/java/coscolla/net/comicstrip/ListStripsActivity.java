package coscolla.net.comicstrip;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.Bind;
import butterknife.ButterKnife;
import coscolla.net.comicstrip.net.ComicStripRestService;
import coscolla.net.comicstrip.net.ComicsResults;
import coscolla.net.comicstrip.net.StripResults;
import coscolla.net.comicstrip.ui.adapter.StripAdapter;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ListStripsActivity extends AppCompatActivity {

  private static final String LOGTAG = "ListStripsActivity";

  @Bind(R.id.list) RecyclerView list;
  private StripAdapter listAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_list_strips);

    ButterKnife.bind(this);

    if(savedInstanceState == null) {
      configureList();
      requestStrips();
    }
  }

  private void configureList() {
    listAdapter = new StripAdapter();
    list.setLayoutManager(new LinearLayoutManager(this));
    list.setAdapter(listAdapter);
  }

  private void requestStrips() {
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl("http://46.101.199.221/")
        .addConverterFactory(GsonConverterFactory.create())
        .build();

    ComicStripRestService service = retrofit.create(ComicStripRestService.class);
    service.listStrips("existentialcomics").enqueue(new Callback<StripResults>() {
      @Override
      public void onResponse(Response<StripResults> response) {
        listAdapter.setData(response.body().result);
      }

      @Override
      public void onFailure(Throwable t) {
        Log.d(LOGTAG, "onFailure");
        // TODO SHOW ERROR!
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
      String url = "http://existentialcomics.com/";
      Intent i = new Intent(Intent.ACTION_VIEW);
      i.setData(Uri.parse(url));
      startActivity(i);
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
