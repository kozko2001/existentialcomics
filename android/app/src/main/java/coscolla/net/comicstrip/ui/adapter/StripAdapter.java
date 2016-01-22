package coscolla.net.comicstrip.ui.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.parceler.Parcels;

import java.util.Collections;
import java.util.List;

import coscolla.net.comicstrip.DetailStripActivity;
import coscolla.net.comicstrip.R;
import coscolla.net.comicstrip.net.StripResultItem;

public class StripAdapter extends RecyclerView.Adapter<StripViewHolder>{

  private List<StripResultItem> data;

  public void setData(List<StripResultItem> data) {
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
    StripResultItem strip = data.get(position);
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
  private StripResultItem data;

  public StripViewHolder (View itemView) {
    super(itemView);

    this.title = (TextView) itemView.findViewById(R.id.title);
    itemView.setOnClickListener(ppp);
  }

  public void bind(StripResultItem strip) {
    this.data = strip;
    title.setText(strip.title);
  }

  private final View.OnClickListener ppp = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      Intent i = new Intent(v.getContext(), DetailStripActivity.class);
      i.putExtra("strip", Parcels.wrap(data));

      v.getContext().startActivity(i);
    }
  };
}
