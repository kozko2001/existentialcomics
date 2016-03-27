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
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.parceler.Parcels;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

import net.coscolla.comicstrip.ui.detail.DetailStripActivity;
import net.coscolla.comicstrip.R;
import net.coscolla.comicstrip.di.Graph;
import net.coscolla.comicstrip.net.comic.api.entities.Strip;
import net.coscolla.comicstrip.net.comic.repository.ComicRepository;
import net.coscolla.comicstrip.net.push.PushManager;
import net.coscolla.comicstrip.ui.AdapterCallback;
import net.coscolla.comicstrip.ui.list.adapter.StripAdapter;

import javax.inject.Inject;

import static rx.schedulers.Schedulers.io;


public class ListStripsActivity extends AppCompatActivity {

  private static final String LOGTAG = "ListStripsActivity";
  public static final String STRIPS = "strips";
  public static final String INTENT_ARG_COMIC_NAME = "comicName";

  @Bind(R.id.list) RecyclerView list;

  private List<Strip> listData;

  @Inject PushManager pushManager;
  @Inject StripAdapter listAdapter;
  @Inject ComicRepository repository;

  private Subscription subscription; // Repository observable subscription

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_list_strips);

    Graph.getInstance().getListStripsComponent().inject(this);
    setTitle(getComicName());

    ButterKnife.bind(this);

    configureList();
  }

  @Override
  protected void onStart() {
    super.onStart();

    subscription = requestStrips();
  }

  @Override
  protected void onStop() {
    super.onStop();

    if(subscription != null && !subscription.isUnsubscribed()) {
      subscription.unsubscribe();
    }
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
  private Subscription requestStrips() {
    return repository.getStrips(getComicName())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(strips -> {
          listData = strips;
          updateList();
        }, (error) -> {
          Timber.e(LOGTAG, "onFailure");
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
  public boolean onPrepareOptionsMenu(Menu menu) {
    Boolean isSubscribed = isComicPushNotificationSubscribed();

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
      Boolean isSubscribed = isComicPushNotificationSubscribed();
      if(!isSubscribed) {
        repository.subscribe(getComicName())
            .subscribeOn(io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                b -> {} ,
                e -> {
                  Timber.e(LOGTAG, "Error during the request to the push notification");
                },
                this::invalidateOptionsMenu);
      } else {
        // TODO: NOT  YET IMPLEMENTED ON THE BACKEND ....
      }
    }

    return super.onOptionsItemSelected(item);
  }

  private Boolean isComicPushNotificationSubscribed() {
    Boolean isSubscribed = repository.isSubscribed(getComicName()).toBlocking().first();
    return isSubscribed;
  }

  private String getComicName() {
    return getIntent().getStringExtra(INTENT_ARG_COMIC_NAME);
  }

  public static Intent createIntent(Context context, String comicName) {
    Intent intent = new Intent(context, ListStripsActivity.class);
    intent.putExtra(INTENT_ARG_COMIC_NAME, comicName);
    return intent;
  }
}
