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

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import net.coscolla.comicstrip.di.Graph;
import net.coscolla.comicstrip.net.comic.ComicApi;
import net.coscolla.comicstrip.net.comic.StripResultItem;
import net.coscolla.comicstrip.net.comic.StripResults;
import net.coscolla.comicstrip.net.push.PushManager;
import net.coscolla.comicstrip.ui.adapter.StripAdapter;

import javax.inject.Inject;

import retrofit2.Callback;
import retrofit2.Response;

public class ListStripsActivity extends AppCompatActivity {

  private static final String LOGTAG = "ListStripsActivity";
  public static final String STRIPS = "strips";

  @Bind(R.id.list) RecyclerView list;

  private List<StripResultItem> listData;

  @Inject ComicApi api;
  @Inject PushManager pushManager;
  @Inject StripAdapter listAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_list_strips);

    Graph.getInstance().getListStripsComponent().inject(this);

    ButterKnife.bind(this);
    configureList();

    loadFromSavedInstanceOrNetwork(savedInstanceState);
  }

  /**
   * Fills the adapter with the data of:
   *
   * 1) savedInstance if there is available data
   * 2) if not, we just do the request and fill the information when all the data is available
   *
   * @param savedInstanceState
   */
  private void loadFromSavedInstanceOrNetwork(Bundle savedInstanceState) {
    if(savedInstanceState == null) {
      requestStrips();
    } else {
      loadStripsFromSavedInstance(savedInstanceState);
      updateList();
    }
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    if(listData != null) {
      Parcelable[] parcelable = dataToParcelable();
      outState.putParcelableArray("strips", parcelable);
    }
    super.onSaveInstanceState(outState);
  }

  /***
   * Converts the current data of the list to parcelable so we can store it on the saved instance
   *
   * @return a parcelable with the information
   */
  @NonNull
  private Parcelable[] dataToParcelable() {
    Parcelable[] parcelable = new Parcelable[listData.size()];
    for (int i = 0; i < parcelable.length; i++) {
      parcelable[i] = Parcels.wrap(listData.get(i));
    }
    return parcelable;
  }

  /**
   * Configures properly the recycler view
   */
  private void configureList() {
    listAdapter = new StripAdapter();
    list.setLayoutManager(new LinearLayoutManager(this));
    list.setAdapter(listAdapter);
  }

  /**
   * Use the api to request the data to the comic service
   */
  private void requestStrips() {
    api.listStrips("existentialcomics").enqueue(new Callback<StripResults>() {

      @Override
      public void onResponse(Response<StripResults> response) {
        listData = response.body().result;
        updateList();
      }

      @Override
      public void onFailure(Throwable t) {
        Log.d(LOGTAG, "onFailure");
      }
    });
  }

  /**
   * Updates the list adapter with the new data available
   */
  private void updateList() {
    listAdapter.setData(listData);
  }

  /**
   *
   * @param savedInstanceState
   */
  private void loadStripsFromSavedInstance(Bundle savedInstanceState) {
    Parcelable[] data = savedInstanceState.getParcelableArray(STRIPS);
    listData = new ArrayList<>();
    for(int i =0; i < data.length; i++) {
      listData.add((StripResultItem) Parcels.unwrap(data[i]));
    }
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
