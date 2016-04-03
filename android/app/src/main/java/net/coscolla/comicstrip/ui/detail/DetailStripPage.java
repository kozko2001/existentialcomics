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

package net.coscolla.comicstrip.ui.detail;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.ybq.android.spinkit.style.Wave;

import net.coscolla.comicstrip.R;
import net.coscolla.comicstrip.di.Graph;
import net.coscolla.comicstrip.entities.Strip;
import net.coscolla.comicstrip.usecases.DetailStripUseCase;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;
import uk.co.senab.photoview.PhotoViewAttacher;

public class DetailStripPage extends Fragment {

  public static final String BITMAP = "bitmap";
  private static final String ID = "ID";

  @Bind(R.id.image) ImageView image;
  @Bind(R.id.error_container) View errorContainer;
  @Inject DetailStripUseCase useCase;
  CompositeSubscription subscription;
  private String id;
  private Bitmap stripBitmap;
  private PhotoViewAttacher photoViewAttacher;
  private Strip strip;
  private Wave loadingDrawable;

  public static DetailStripPage newInstance(String id) {
    DetailStripPage page = new DetailStripPage();
    Bundle args = new Bundle();
    args.putString(ID, id);
    page.setArguments(args);

    return page;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Graph.getInstance().getDetailStripComponent().inject(this);

  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.activity_detail_strip_page, container, false);
    ButterKnife.bind(this, view);
    createLoadingDrawable();

    subscription = new CompositeSubscription();
    id = getArguments().getString(ID);
    strip = useCase.getStripById(id).toBlocking().first();

    loadImageFromSavedInstance(savedInstanceState);

    return view;
  }

  @Override
  public void onResume() {
    super.onResume();


    Timber.i("onResume of %s", strip.title);
    if(stripBitmap != null) {
      loadImageFromBitmap();
    } else {
      loadImageFromNetwork(strip);
    }

  }

  @Override
  public void onPause() {
    super.onStop();

    subscription.clear();

    Timber.i("onPause of %s", strip.title);
  }

  /**
   * Stores in the savedInstance the bitmap already loaded
   *
   * @param outState bundle where we store information to be pass when restoring the view
   */
  @Override
  public void onSaveInstanceState(Bundle outState) {
    Timber.i("onSaveInstance of %s", strip.title);

    if(stripBitmap != null) {
      outState.putParcelable(BITMAP, stripBitmap);
    }

    super.onSaveInstanceState(outState);
  }

  /**
   * Obtains the image from the savedInstance and load it into the imageView
   * @param savedInstanceState
   */
  private void loadImageFromSavedInstance(Bundle savedInstanceState) {
    if(savedInstanceState != null && savedInstanceState.containsKey(BITMAP)) {
      stripBitmap = savedInstanceState.getParcelable(BITMAP);
    }
  }

  /**
   * Loads the strip bitmap inside the image view and configures the photoView library
   */
  private void loadImageFromBitmap() {
    image.setVisibility(View.VISIBLE);
    errorContainer.setVisibility((View.GONE));
    image.setImageBitmap(stripBitmap);

    if(photoViewAttacher == null ) {
      photoViewAttacher = new PhotoViewAttacher(image);
      photoViewAttacher.setMaximumScale(20);
    } else {
      photoViewAttacher.update();
    }
  }

  /**
   * Makes a network call to get the bitmap and loads it into the imageview
   *
   * @param strip strip entity
   */
  private void loadImageFromNetwork(Strip strip) {
    showLoading();
    subscription.add(useCase.getImage(strip)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            this::loadImageFromBytes,
            e -> {
              Timber.e(e, "Error on getting the data for the strip image");
              showRetry();
            }
        ));
  }

  /**
   * Shows the retry button for recover when socket timeout occurs
   */
  private void showRetry() {
    errorContainer.setVisibility(View.VISIBLE);
    image.setVisibility(View.GONE);
  }

  /**
   * Shows the loading drawable in the image view of the comic
   */
  private void showLoading() {
    errorContainer.setVisibility(View.GONE);
    image.setVisibility(View.VISIBLE);
    image.setImageDrawable(loadingDrawable);
  }

  /**
   * Given the bytes from the network convert them to a bitmap using glide so we don't have
   * to worry about cache and load into the image
   *
   * @param bytes data downloaded from the backedn
   */
  private void loadImageFromBytes(byte[] bytes) {
    Glide.with(this)
        .load(bytes)
        .asBitmap()
        .listener(new RequestListener<byte[], Bitmap>() {
          @Override
          public boolean onException(Exception e, byte[] model, Target<Bitmap> target, boolean isFirstResource) {
            return false;
          }

          @Override
          public boolean onResourceReady(Bitmap resource, byte[] model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
            stripBitmap = resource;
            loadImageFromBitmap();
            return false;
          }
        })
        .into(image);
  }

  /**
   * Retry to download the image on the click of the retry button
   */
  @OnClick(R.id.btn_retry) void retry() {
    if(strip != null) {
      loadImageFromNetwork(strip);
    }
  }

  private void createLoadingDrawable() {
    loadingDrawable = new Wave();
    loadingDrawable.setColor(0xFFFF4081);
    loadingDrawable.setScale(0.5f);
    loadingDrawable.start();
  }
}
