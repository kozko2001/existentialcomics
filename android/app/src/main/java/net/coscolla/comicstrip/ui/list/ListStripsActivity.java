/*
 * Copyright 2016 Jordi Coscolla.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.coscolla.comicstrip.ui.list;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import net.coscolla.comicstrip.R;
import net.coscolla.comicstrip.di.Graph;
import net.coscolla.comicstrip.entities.Comic;
import net.coscolla.comicstrip.entities.Strip;
import net.coscolla.comicstrip.ui.AdapterCallback;
import net.coscolla.comicstrip.ui.detail.DetailStripActivity;
import net.coscolla.comicstrip.ui.list.adapter.StripAdapter;
import net.coscolla.comicstrip.usecases.ListStripsUseCase;
import net.coscolla.comicstrip.usecases.PushSubscribeUseCase;

import org.parceler.Parcels;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

import static rx.schedulers.Schedulers.io;


public class ListStripsActivity extends AppCompatActivity {

  public static final String INTENT_ARG_COMIC = "comic";

  @Bind(R.id.list) RecyclerView list;

  @Bind(R.id.main_content) CoordinatorLayout coordinatorLayout;
  @Inject StripAdapter listAdapter;
  @Inject ListStripsUseCase useCase;
  @Inject PushSubscribeUseCase subscribeUseCase;

  private List<Strip> listData;
  private Subscription subscription;

  private Comic comic;

  /**
   * Callback to receive which item of the list was selected
   *
   * Go to the strip image activity
   */
  private AdapterCallback<Strip> adapterCallback = ((eventName, model) -> {
    if(eventName == StripAdapter.SELECTED) {
      openDetailActivity(model);
    }
  });
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_list_strips);

    Graph.getInstance().getListStripsComponent().inject(this);
    setTitle(getComic().name);

    ButterKnife.bind(this);

    configureList();

    startListeningModel();

    if(savedInstanceState == null) {
      request();
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    if(subscription != null && !subscription.isUnsubscribed()) {
      subscription.unsubscribe();
    }
  }

  /**
   * Starts observing the model and updates the list if there is new data
   */
  private void startListeningModel() {
    subscription = useCase.model(getComicId())
        .subscribeOn(io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            this::updateList,
            e -> Timber.e(e, "Error on the model subscriber"),
            () -> Timber.d("Model listener subscription finalized")
        );
  }

  /**
   * Makes a new request and updates the data of the model
   */
  private void request() {
    useCase.refresh(getComicId())
        .subscribeOn(io())
        .doOnNext(list -> Timber.d("New %d comics fetched ", list.size()))
        .flatMap(l -> useCase.model(getComicId()))
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            this::updateList,
            e -> {
              Timber.e(e, "Couldn't get the new strips from the network");
              showError("Could'nt retrieve the updated data");
            },
            () -> Timber.d("Strips requests completed")
        );
  }

  /**
   * Shows an error on the view
   * @param message error message to be shown
   */
  private void showError(String message) {
    Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG).show();
  }

  /**
   * Configures properly the recycler view
   */
  private void configureList() {
    listAdapter.setCallback(this.adapterCallback);
    list.setLayoutManager(createLayoutManager());
    list.setAdapter(listAdapter);
  }

  /**
   * Configures the layout manager for the list, we are using a grid system that auto spans to
   * get 300dp with cards
   *
   * @return a valid layout for the list
   */
  private RecyclerView.LayoutManager createLayoutManager() {
    int totalWidth = this.getResources().getDisplayMetrics().widthPixels;
    int columnWidth = (int) TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        300,
        this.getResources().getDisplayMetrics());

    int numColums = (int) Math.max(Math.floor(totalWidth / columnWidth), 1);
    GridLayoutManager grid = new GridLayoutManager(this, numColums);

    return grid;
  }

  /**
   * Starts the new activity with all the ids of this comics, but showing the page
   * of the strip we pass as parameter
   *
   * @param strip comic strip that must be shown first on the detail activity
   */
  private void openDetailActivity(Strip strip) {
    List<String> ids = Stream.of(listData)
        .map(_strip -> _strip._id)
        .collect(Collectors.toList());

    String[] ids_array = ids.toArray(new String[ids.size()]);


    Intent intent = new Intent(ListStripsActivity.this, DetailStripActivity.class);
    intent.putExtra(DetailStripActivity.STRIP, Parcels.wrap(strip));
    intent.putExtra(DetailStripActivity.IDS, ids_array);

    startActivity(intent);
  }

  /**
   * Updates the list adapter with the new data available
   */
  private void updateList(List<Strip> newData) {
    this.listData = newData;
    listAdapter.setData(listData);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.list_strips_menu, menu);
    return true;
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    Boolean isSubscribed = subscribeUseCase.isSubscribed(getComicId());

    MenuItem togglePushNotification = menu.findItem(R.id.menu_push_notification);

    int description = isSubscribed ? R.string.unsubscribe : R.string.subscribe;
    int icon = isSubscribed ? R.mipmap.ic_favorite_white_36dp : R.mipmap.ic_favorite_border_white_36dp;

    togglePushNotification.setTitle(description);
    togglePushNotification.setIcon(icon);

    return super.onPrepareOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if(item.getItemId() == R.id.menu_push_notification) {
      subscribeUseCase.toogleSubscribe(getComicId())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(
              b -> Timber.d("toggle subscription on %s", getComicId()) ,
              e -> Timber.e(e, "Error during the request to the push notification"),
              this::invalidateOptionsMenu);
    }

    if(item.getItemId() == R.id.open_browser) {
      Uri url = Uri.parse(getComic().url);
      if(url != null) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, url);
        startActivity(browserIntent);
      }
    }

    return super.onOptionsItemSelected(item);
  }

  private Comic getComic() {
    if(comic == null) {
      comic = Parcels.unwrap(getIntent().getParcelableExtra("comic"));
    }
    return comic;
  }

  private String getComicId() {
    return getComic().comic_id;
  }

  public static Intent createIntent(Context context, Comic comic) {
    Intent intent = new Intent(context, ListStripsActivity.class);
    intent.putExtra(INTENT_ARG_COMIC, Parcels.wrap(comic));
    return intent;
  }

}
