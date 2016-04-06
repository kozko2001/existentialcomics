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
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Predicate;

import net.coscolla.comicstrip.R;
import net.coscolla.comicstrip.di.Graph;
import net.coscolla.comicstrip.entities.Comic;
import net.coscolla.comicstrip.ui.list.ListStripsActivity;
import net.coscolla.comicstrip.usecases.ListComicsUseCase;

import org.parceler.Parcels;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static rx.schedulers.Schedulers.io;

public class ComicsActivity extends AppCompatActivity {

  private static final String SAVED_INSTANCE_ARG_COMICS = "DATA_SAVED_LIST";

  @Bind(R.id.list) RecyclerView list;

  @Bind(R.id.main_content) CoordinatorLayout coordinatorLayout;

  @Inject ComicAdapter listAdapter;

  @Inject ListComicsUseCase useCase;

  /**
   * data that the adapter is currently using
   */
  private List<ComicAdapterModel> data;

  private CompositeSubscription subscription;
  private List<Comic> comics;

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

    subscription = new CompositeSubscription();

  }

  @Override
  protected void onStart() {
    super.onStart();

    startListeningModel();

    if(data == null) {
      requestData();
    } else {
      updateAdapter();
    }
  }

  @Override
  protected void onStop() {
    super.onStop();

    subscription.clear();
  }

  /**
   * Stores the current data in the recycler view
   *
   * @param outState bundle to store the current data
   */
  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    if(data != null && data.size() > 0) {
      List<Parcelable> dataToStore = Stream.of(data)
          .map(Parcels::wrap)
          .collect(Collectors.toList());

      Parcelable[] array = dataToStore.toArray(new Parcelable[dataToStore.size()]);


      outState.putParcelableArray(SAVED_INSTANCE_ARG_COMICS, array);
    }
  }

  /**
   * Restores the data to fill the recycler view adapter
   *
   * @param savedInstanceState bundle with the information of the last state
   */
  private void restoreFromPreviousInstance(@NonNull  Bundle savedInstanceState) {
    Parcelable[] storageData = savedInstanceState.getParcelableArray(SAVED_INSTANCE_ARG_COMICS);
    if(storageData != null && storageData.length > 0) {

      this.data = Stream.of(storageData)
          .map(Parcels::<ComicAdapterModel>unwrap)
          .collect(Collectors.toList());
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
  }

  /**
   * Launches a request to the repository and fills the adapter with the data
   */
  private void startListeningModel() {
    subscription.add(useCase.model()
        .subscribeOn(io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            this::onDataReceived,
            (e) -> Timber.e(e, "Error downloading comics from the api")));
  }

  /**
   * Request the data from the backend and stores them into the cache, so the cache observable
   * will trigger and reload the data
   */
  private void requestData() {
        subscription.add(useCase.refresh()
            .subscribeOn(io())
            .doOnNext(new_results -> Timber.d("New %d comics fetched ", new_results.size()))
            .flatMap(new_results -> useCase.model())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                this::onDataReceived,
                (e) -> {
                  Timber.e(e, "Couldn't retrieve updated data");
                  showError("Couldn't retrieve updated data");
                },
                ()  -> Timber.d("request for comics completed")
            ));
  }

  /**
   * Shows an error on the view
   * @param message error message to be shown
   */
  private void showError(String message) {
    Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG).show();
  }


  /**
   * When the data is received either from network or from cache update the list
   *
   * @param list data with the comics name from the api
   */
  private void onDataReceived(@NonNull  List<Comic> list) {
    comics = list;
    this.data = Stream.of(list)
        .map(this::convertToModel)
        .collect(Collectors.toList());

    updateAdapter();
  }

  /**s
   * Converts the raw comics api to the model for the adapter
   * @param comic list of strings with the comics name
   * @return list of ComicAdapterModel with the comics name
   */
  private ComicAdapterModel convertToModel(Comic comic) {
    return new ComicAdapterModel(comic);
  }

  /**
   * Updates the recycler view adapter
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
    });
    list.setLayoutManager(new LinearLayoutManager(this));
    list.setAdapter(listAdapter);
  }

  /**
   * Opens the list strips activity
   *
   * @param comic name of the comics to be opened
   */
  private void openStrips(@NonNull  ComicAdapterModel comic) {
    Comic _comic = Stream.of(comics)
        .filter(value -> value.comic_id.equals(comic.id))
        .findFirst()
        .get();

    Intent intent = ListStripsActivity.createIntent(this, _comic);
    this.startActivity(intent);
  }


}
