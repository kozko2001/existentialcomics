package net.coscolla.comicstrip;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import org.parceler.Parcel;
import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;

import net.coscolla.comicstrip.di.Graph;
import net.coscolla.comicstrip.net.comic.api.entities.Strip;

import javax.inject.Inject;
import javax.inject.Named;

import uk.co.senab.photoview.PhotoViewAttacher;

public class DetailStripActivity extends AppCompatActivity {

  public static final String BITMAP = "bitmap";
  public static final String STRIP = "strip";
  public static final String IDS = "IDS";

  @Bind(R.id.image) ImageView image;

  private Strip strip;
  private Bitmap stripBitmap;
  private PhotoViewAttacher photoViewAttacher;
  private String[] strip_ids;

  @Inject @Named("endpoint") String apiEndpoint;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_detail_strip);
    Graph.getInstance().getDetailStripComponent().inject(this);

    getCurrentStrip(savedInstanceState);

    ButterKnife.bind(this);

    this.setTitle(strip.title);

    loadImageFromSavedInstanceOrNetwork(savedInstanceState);

  }

  /**
   * Gets the strip
   *
   * If the activity is recreated get it from the savedInstance if not,
   * get it from the intent bundle
   *
   * @param savedInstanceState
   */
  private void getCurrentStrip(@Nullable Bundle savedInstanceState) {
    if(savedInstanceState != null) {
      strip = Parcels.unwrap(savedInstanceState.getParcelable(STRIP));
    } else {
      strip = Parcels.unwrap(getIntent().getParcelableExtra(STRIP));
    }

    strip_ids = getIntent().getStringArrayExtra(IDS);
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
  protected void onSaveInstanceState(Bundle outState) {
    if(stripBitmap != null) {
      outState.putParcelable(BITMAP, stripBitmap);
    }
    if(strip != null) {
      outState.putParcelable(STRIP, Parcels.wrap(strip));
    }

    super.onSaveInstanceState(outState);
  }

  private void loadImageFromNetwork() {
    final String imageUrl = apiEndpoint + "comics/image/" + strip._id;

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
}
