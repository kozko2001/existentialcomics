package net.coscolla.comicstrip.ui.list.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import net.coscolla.comicstrip.R;
import net.coscolla.comicstrip.net.api.UrlBuilder;
import net.coscolla.comicstrip.entities.Strip;
import net.coscolla.comicstrip.ui.AdapterCallback;

public class StripAdapter extends RecyclerView.Adapter<StripViewHolder>{

  public static final String SELECTED = "selected";
  public final UrlBuilder urlBuilder;

  private List<Strip> data;
  private AdapterCallback<Strip> callback;

  public StripAdapter(UrlBuilder urlBuilder) {
    this.urlBuilder = urlBuilder;
  }

  /**
   * Sets the callback so we can send which of the strips is the one the user has selected
   * @param callback
   */
  public void setCallback(AdapterCallback<Strip> callback) {
    this.callback = callback;
  }

  /**
   * Updates the current data of the recycle adapter, making all the recycle view to be updated
   *
   * @param data new data
   */
  public void setData(List<Strip> data) {
    this.data = data;
    this.notifyDataSetChanged();
  }

  @Override
  public StripViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(
        R.layout.list_item_strip, parent, false);
    return new StripViewHolder(view, callback, urlBuilder);
  }

  @Override
  public void onBindViewHolder(StripViewHolder holder, int position) {
    Strip strip = data.get(position);
    holder.bind(strip);
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

class StripViewHolder extends RecyclerView.ViewHolder {

  private final ImageView imageView;
  private final UrlBuilder urlBuilder;
  private TextView title;
  private Strip data;
  private AdapterCallback<Strip> callback;

  public StripViewHolder (View itemView, AdapterCallback<Strip> callback, UrlBuilder urlBuilder) {
    super(itemView);

    this.title = (TextView) itemView.findViewById(R.id.title);
    this.imageView = (ImageView) itemView.findViewById(R.id.preview);
    this.callback = callback;
    this.urlBuilder = urlBuilder;

    itemView.setOnClickListener(onRowSelected);
  }

  public void bind(Strip strip) {
    this.data = strip;
    title.setText(strip.title);

    String imageUrl = urlBuilder.urlThumbnail(strip);

    Glide.with(itemView.getContext())
        .load(imageUrl)
        .centerCrop()
        .into(imageView);
  }

  private final View.OnClickListener onRowSelected = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      if(callback != null) {
        callback.onEvent(StripAdapter.SELECTED, data);
      }
    }
  };
}

