package coscolla.net.comicstrip.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import coscolla.net.comicstrip.R;
import coscolla.net.comicstrip.net.StripResults;

public class StripAdapter extends RecyclerView.Adapter<StripViewHolder>{

  private List<StripResults.StripData> data;

  public void setData(List<StripResults.StripData> data) {
    Collections.reverse(data);
    this.data = data;
    this.notifyDataSetChanged();
  }

  @Override
  public StripViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(
        R.layout.list_item_strip, parent, false);
    return new StripViewHolder(view);
  }

  @Override
  public void onBindViewHolder(StripViewHolder holder, int position) {
    StripResults.StripData strip = data.get(position);
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

  public StripViewHolder (View itemView) {
    super(itemView);

    this.title = (TextView) itemView.findViewById(R.id.title);
  }

  public void bind(StripResults.StripData strip) {
    title.setText(strip.title);
  }
}
