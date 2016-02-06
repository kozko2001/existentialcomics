package net.coscolla.comicstrip;

import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.parceler.Parcel;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import net.coscolla.comicstrip.di.Graph;
import net.coscolla.comicstrip.net.comic.api.entities.Strip;
import net.coscolla.comicstrip.net.comic.repository.ComicRepository;
import net.coscolla.comicstrip.net.push.PushManager;
import net.coscolla.comicstrip.ui.adapter.AdapterCallback;
import net.coscolla.comicstrip.ui.adapter.StripAdapter;

import javax.inject.Inject;


public class ListStripsActivity extends AppCompatActivity {

  private static final String LOGTAG = "ListStripsActivity";
  public static final String STRIPS = "strips";

  @Bind(R.id.list) RecyclerView list;

  private List<Strip> listData;

  @Inject PushManager pushManager;
  @Inject StripAdapter listAdapter;
  @Inject ComicRepository repository;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_list_strips);

    Graph.getInstance().getListStripsComponent().inject(this);

    ButterKnife.bind(this);
    configureList();

    requestStrips();
  }

  /**
   * Configures properly the recycler view
   */
  private void configureList() {
    listAdapter.setCallback(this.adapterCallback);
    list.setLayoutManager(new LinearLayoutManager(this));
    list.setAdapter(listAdapter);
  }

  /**
   * Use the api to request the data to the comic service
   */
  private void requestStrips() {
    repository.getStrips("existentialcomics")
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(strips -> {
          listData = strips;
          updateList();
        }, (error) -> {
          Log.d(LOGTAG, "onFailure");
        });
  }

  private AdapterCallback<Strip> adapterCallback = ((eventName, model) -> {
    if(eventName == StripAdapter.SELECTED) {
      List<String> listIds = Observable.from(listData)
          .map(s -> s._id)
          .toList().toBlocking().first();

      Intent intent = new Intent(ListStripsActivity.this, DetailStripActivity.class);
      intent.putExtra(DetailStripActivity.STRIP, Parcels.wrap(model));
      intent.putExtra(DetailStripActivity.IDS, listIds.toArray(new String[listIds.size()]));

      startActivity(intent);
    }
  });

  /**
   * Updates the list adapter with the new data available
   */
  private void updateList() {
    listAdapter.setData(listData);
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
    } else if( item.getItemId() == R.id.menu_debug_register) {
      pushManager.subscribe("existential");
    }
    return super.onOptionsItemSelected(item);
  }

}
