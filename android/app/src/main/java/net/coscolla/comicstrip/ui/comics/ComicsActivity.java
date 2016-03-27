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

package net.coscolla.comicstrip.ui.comics;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import net.coscolla.comicstrip.R;
import net.coscolla.comicstrip.di.Graph;
import net.coscolla.comicstrip.net.comic.api.entities.Comic;
import net.coscolla.comicstrip.net.comic.repository.ComicRepository;
import net.coscolla.comicstrip.ui.list.ListStripsActivity;

import org.parceler.Parcels;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

import static rx.Observable.from;
import static rx.Observable.zip;

public class ComicsActivity extends AppCompatActivity {

  private static final String SAVED_INSTANCE_ARG_COMICS = "DATA_SAVED_LIST";

  @Bind(R.id.list) RecyclerView list;
  @Inject ComicAdapter listAdapter;
  @Inject ComicRepository repository;
  private List<ComicAdapterModel> data;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_comics);

    ButterKnife.bind(this);
    Graph.getInstance().getComicsComponent(this).inject(this);

    if(savedInstanceState != null) {
      restoreFromPreviousInstance(savedInstanceState);
    }

    configureList();

    if(data == null) {
      requestComics();
    } else {
      updateAdapter();
    }
  }

  /**
   * Launches a request to the repository and fills the adapter with the data
   */
  private void requestComics() {
    repository.getComics()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(this::onDataReceived,
            (e) -> {
              Timber.e(e, "ComicsActivity: Error downloading comics from the api");
            });
  }

  /**
   * Restores the data to fill the recycler view adapter
   *
   * @param savedInstanceState
   */
  private void restoreFromPreviousInstance(@NonNull  Bundle savedInstanceState) {
    Parcelable[] storageData = savedInstanceState.getParcelableArray(SAVED_INSTANCE_ARG_COMICS);
    if(storageData != null && storageData.length > 0) {
      this.data = from(storageData)
          .map(Parcels::unwrap)
          .cast(ComicAdapterModel.class)
          .toList()
          .toBlocking()
          .first();
    }
  }

  /**
   * Stores the current data in the recycler view
   * @param outState
   */
  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    if(data != null && data.size() > 0){
      List<Parcelable> storageData = from(data)
          .map(Parcels::wrap)
          .toList()
          .toBlocking()
          .first();

      outState.putParcelableArray(SAVED_INSTANCE_ARG_COMICS, storageData.toArray(new Parcelable[storageData.size()]));
    }
  }

  /**
   * When the data is received either from network or from cache update the list
   *
   * @param list data with the comic name from the api
   */
  private void onDataReceived(@NonNull  List<Comic> list) {
    from(list)
        .map(comic -> comic.name)
        .toList()
        .first()
        .subscribe(_list -> {
          this.data = convertToModel(_list);
          updateAdapter();
        });
  }

  /**
   * Converts the raw comic api to the model for the adapter
   * @param comics list of strings with the comics name
   * @return list of ComicAdapterModel with the comic name and if has notification activated
   */
  private List<ComicAdapterModel> convertToModel(List<String> comics) {

    Observable<Boolean> subscribed = from(comics).flatMap(repository::isSubscribed);

    return zip(from(comics), subscribed, ComicAdapterModel::new)
        .toList()
        .toBlocking()
        .first();
  }

  /**
   * Updates the recyclerview adapter
   */
  private void updateAdapter() {
    this.listAdapter.setData(data);
    this.listAdapter.notifyDataSetChanged();
  }

  /**
   * Configures properly the recycler view
   */
  private void configureList() {
    listAdapter.setCallback((eventName, comic) -> {
      if(eventName.equalsIgnoreCase(ComicAdapter.SELECTED)) {
        openStrips(comic);
      }

      if(eventName.equalsIgnoreCase(ComicAdapter.LONG_SELECTED)) {
        // TODO: show the options
      }
    });
    list.setLayoutManager(new LinearLayoutManager(this));
    list.setAdapter(listAdapter);
  }

  /**
   * Opens the list strips activity
   *
   * @param comic name of the comic to be opened
   */
  private void openStrips(@NonNull  ComicAdapterModel comic) {
    Intent intent = ListStripsActivity.createIntent(this, comic.comicName);
    this.startActivity(intent);
  }


}
