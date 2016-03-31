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
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.github.ybq.android.spinkit.style.FoldingCube;
import com.github.ybq.android.spinkit.style.Pulse;
import com.github.ybq.android.spinkit.style.Wave;

import net.coscolla.comicstrip.R;
import net.coscolla.comicstrip.di.Graph;
import net.coscolla.comicstrip.entities.Strip;
import net.coscolla.comicstrip.usecases.DetailStripUseCase;

import org.parceler.Parcels;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;
import uk.co.senab.photoview.PhotoViewAttacher;

public class DetailStripPage extends Fragment {

  public static final String BITMAP = "bitmap";
  private static final String ID = "ID";
  private static final String SAVE_INSTANCE_STRIP = "STRIP";

  @Bind(R.id.image) ImageView image;

  private String id;
  private Bitmap stripBitmap;
  private PhotoViewAttacher photoViewAttacher;
  private Strip strip;

  @Inject DetailStripUseCase useCase;
  private Wave placeholder;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Graph.getInstance().getDetailStripComponent().inject(this);

    id = getArguments().getString(ID);
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.activity_detail_strip_page, container, false);
    ButterKnife.bind(this, view);

    image = (ImageView) view.findViewById(R.id.image);

    createLoadingDrawable();

    restoreStripFromSavedInstance(savedInstanceState);

    loadImageFromSavedInstanceOrNetwork(savedInstanceState);

    return view;
  }

  /**
   * Gets the strip comic object from the savedInstanceState
   *
   * if cannot get from the savedInstance like if is the first time for this fragment, we as
   * the database for the strip object
   *
   * @param savedInstanceState
   */
  private void restoreStripFromSavedInstance(@Nullable Bundle savedInstanceState) {
    if(savedInstanceState != null) {
      Parcelable stripParcel = savedInstanceState.getParcelable(SAVE_INSTANCE_STRIP);
      if(stripParcel != null) {
        this.strip = Parcels.<Strip>unwrap(stripParcel);
      }
    }

    // if could not restore from the savedInstance request to the database
    if(this.strip == null) {
      useCase.getStripById(id).subscribe(
          strip -> this.strip = strip,
          e -> Timber.e(e, "could not get from the cache the strip with id %s", id),
          () -> Timber.d("Completed the strip by id")
      );
    }
  }

  /**
   * Tries to reload the image from the savedInstance if not possible makes a request to
   * the network
   *
   * @param savedInstanceState
   */
  private void loadImageFromSavedInstanceOrNetwork(@Nullable Bundle savedInstanceState) {
    loadImageFromSavedInstance(savedInstanceState);

    if(stripBitmap == null) {
      useCase.getStripById(id)
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(
              this::loadImageFromNetwork,
              e -> Timber.e(e, "could not get from the cache the strip with id %s", id)
          );
    }
  }

  /**
   * Obtains the image from the savedInstance and load it into the imageView
   * @param savedInstanceState
   */
  private void loadImageFromSavedInstance(Bundle savedInstanceState) {
    if(savedInstanceState != null && savedInstanceState.containsKey(BITMAP)) {
      stripBitmap = savedInstanceState.getParcelable(BITMAP);
      loadImageFromBitmap();
    }
  }

  /**
   * Loads the strip bitmap inside the image view and configures the photoView library
   */
  private void loadImageFromBitmap() {
    image.setImageBitmap(stripBitmap);

    if(photoViewAttacher == null ) {
      photoViewAttacher = new PhotoViewAttacher(image);
      photoViewAttacher.setMaximumScale(20);
    } else {
      photoViewAttacher.update();
    }
  }

  /**
   * Stores in the savedInstance the bitmap already loaded and the current strip we are showing
   *
   * @param outState bundle where we store information to be pass when restoring the view
   */
  @Override
  public void onSaveInstanceState(Bundle outState) {
    if(stripBitmap != null) {
      outState.putParcelable(BITMAP, stripBitmap);
    }

    if(strip != null) {
      outState.putParcelable(SAVE_INSTANCE_STRIP, Parcels.wrap(strip));
    }

    super.onSaveInstanceState(outState);
  }

  /**
   * Makes a network call to get the bitmap and loads it into the imageview
   *
   * @param strip strip entity
   */
  private void loadImageFromNetwork(Strip strip) {
    final String imageUrl = useCase.getStripImageUrl(strip);


    Glide.with(this)
        .load(imageUrl)
        .asBitmap()
        .placeholder(placeholder)
        .error(android.R.drawable.ic_menu_rotate)
        .listener(new RequestListener<String, Bitmap>() {
          @Override
          public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
            return false;
          }

          @Override
          public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
            stripBitmap = resource;
            loadImageFromBitmap();
            return false;
          }
        })
        .into(image);
  }

  private void createLoadingDrawable() {
    placeholder = new Wave();
    placeholder.setColor(0xFFFF4081);
    placeholder.setScale(0.5f);
    placeholder.start();
  }

  public static DetailStripPage newInstance(String id) {
    DetailStripPage page = new DetailStripPage();
    Bundle args = new Bundle();
    args.putString(ID, id);
    page.setArguments(args);

    return page;
  }
}
