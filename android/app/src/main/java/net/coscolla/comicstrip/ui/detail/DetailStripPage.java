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
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import net.coscolla.comicstrip.R;
import net.coscolla.comicstrip.di.Graph;
import net.coscolla.comicstrip.net.api.UrlBuilder;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import uk.co.senab.photoview.PhotoViewAttacher;

public class DetailStripPage extends Fragment {

  public static final String BITMAP = "bitmap";
  private static final String ID = "ID";

  @Bind(R.id.image) ImageView image;

  private String id;
  private Bitmap stripBitmap;
  private PhotoViewAttacher photoViewAttacher;


  @Inject UrlBuilder urlBuilder;

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
    loadImageFromSavedInstanceOrNetwork(savedInstanceState);

    return view;
  }

  private void loadImageFromSavedInstanceOrNetwork(@Nullable Bundle savedInstanceState) {
    loadImageFromSavedInstance(savedInstanceState);
    if(stripBitmap == null) {
      loadImageFromNetwork();
    }
  }

  private void loadImageFromSavedInstance(Bundle savedInstanceState) {
    if(savedInstanceState != null && savedInstanceState.containsKey(BITMAP)) {
      stripBitmap = savedInstanceState.getParcelable(BITMAP);
      loadImageFromBitmap();
    }
  }

  private void loadImageFromBitmap() {
    image.setImageBitmap(stripBitmap);

    if(photoViewAttacher == null ) {
      photoViewAttacher = new PhotoViewAttacher(image);
      photoViewAttacher.setMaximumScale(20);
    } else {
      photoViewAttacher.update();
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    if(stripBitmap != null) {
      outState.putParcelable(BITMAP, stripBitmap);
    }

    super.onSaveInstanceState(outState);
  }

  private void loadImageFromNetwork() {
    final String imageUrl = urlBuilder.urlImage(id);

    Glide.with(this)
        .load(imageUrl)
        .asBitmap()
        .into(new SimpleTarget<Bitmap>() {
          @Override
          public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
            stripBitmap = resource;
            loadImageFromBitmap();
          }
        });
  }

  public static DetailStripPage newInstance(String id) {
    DetailStripPage page = new DetailStripPage();
    Bundle args = new Bundle();
    args.putString(ID, id);
    page.setArguments(args);

    return page;
  }
}
