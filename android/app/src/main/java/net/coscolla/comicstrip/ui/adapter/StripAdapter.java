package net.coscolla.comicstrip.ui.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.parceler.Parcels;

import java.util.Collections;
import java.util.List;

import net.coscolla.comicstrip.DetailStripActivity;
import net.coscolla.comicstrip.R;
import net.coscolla.comicstrip.net.comic.api.entities.Strip;

public class StripAdapter extends RecyclerView.Adapter<StripViewHolder>{

  public static final String SELECTED = "selected";

  private List<Strip> data;
  private AdapterCallback<Strip> callback;

  public void setCallback(AdapterCallback<Strip> callback) {
    this.callback = callback;
  }

  public void setData(List<Strip> data) {
    Collections.reverse(data);
    this.data = data;
    this.notifyDataSetChanged();
  }

  @Override
  public StripViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(
        R.layout.list_item_strip, parent, false);
    return new StripViewHolder(view, callback);
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

  private TextView title;
  private Strip data;
  private AdapterCallback<Strip> callback;

  public StripViewHolder (View itemView, AdapterCallback<Strip> callback) {
    super(itemView);

    this.title = (TextView) itemView.findViewById(R.id.title);
    this.callback = callback;
    itemView.setOnClickListener(ppp);
  }

  public void bind(Strip strip) {
    this.data = strip;
    title.setText(strip.title);
  }

  private final View.OnClickListener ppp = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      if(callback != null) {
        callback.onEvent(StripAdapter.SELECTED, data);
      }
    }
  };
}

