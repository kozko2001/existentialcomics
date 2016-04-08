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

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.ImageVideoBitmapDecoder;

import net.coscolla.comicstrip.R;
import net.coscolla.comicstrip.ui.AdapterCallback;

import java.util.List;

public class ComicAdapter extends RecyclerView.Adapter<ComicViewHolder>{

  public static final String SELECTED = "SELECTED";
  public static final String LONG_SELECTED = "LONG_SELECTED";

  private List<ComicAdapterModel> data;
  private AdapterCallback<ComicAdapterModel> callback;

  public void setCallback(AdapterCallback<ComicAdapterModel> callback) {
    this.callback = callback;
  }

  public void setData(List<ComicAdapterModel> data) {
    this.data = data;
    this.notifyDataSetChanged();
  }

  @Override
  public ComicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(
        R.layout.activity_comics_item, parent, false);
    return new ComicViewHolder(view, callback);
  }

  @Override
  public void onBindViewHolder(ComicViewHolder holder, int position) {
    ComicAdapterModel comic = data.get(position);
    holder.bind(comic);
  }

  @Override
  public int getItemCount() {
    if(data == null) {
      return 0;
    } else {
      return data.size();
    }
  }
}

class ComicViewHolder extends RecyclerView.ViewHolder {

  private final TextView title;
  private final TextView url;
  private final ImageView image;

  private ComicAdapterModel data;
  private AdapterCallback<ComicAdapterModel> callback;

  public ComicViewHolder(View itemView, AdapterCallback<ComicAdapterModel> callback) {
    super(itemView);

    this.title = (TextView) itemView.findViewById(R.id.title);
    this.url = (TextView) itemView.findViewById(R.id.url);
    this.image = (ImageView) itemView.findViewById(R.id.image);

    this.callback = callback;

    itemView.setOnClickListener(onRowSelected);
  }

  public void bind(ComicAdapterModel comic) {
    this.data = comic;
    title.setText(comic.name);
    url.setText(comic.url);
    Glide.with(itemView.getContext())
        .load(comic.image)
        .into(image);
  }

  private final View.OnClickListener onRowSelected = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      if(callback != null) {
        callback.onEvent(ComicAdapter.SELECTED, data);
      }
    }
  };
}

