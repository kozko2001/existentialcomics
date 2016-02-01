package net.coscolla.comicstrip;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;
import net.coscolla.comicstrip.net.comic.StripResultItem;
import uk.co.senab.photoview.PhotoViewAttacher;

public class DetailStripActivity extends AppCompatActivity {

  private static final String LOGTAG = "DetailsStripActivity";
  public static final String BITMAP = "bitmap";
  @Bind(R.id.image) ImageView image;

  private StripResultItem strip;
  private Bitmap stripBitmap;
  private PhotoViewAttacher photoViewAttacher;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_detail_strip);

    strip = Parcels.unwrap(getIntent().getParcelableExtra("strip"));
    ButterKnife.bind(this);

    this.setTitle(strip.title);

    loadImageFromSavedInstance(savedInstanceState);
    if(stripBitmap == null) {
      loadImageFromNetwork();
    }

  }

  private void loadImageFromSavedInstance(Bundle savedInstanceState) {
    if(savedInstanceState != null && savedInstanceState.containsKey(BITMAP)) {
      stripBitmap = savedInstanceState.getParcelable(BITMAP);
    }
    loadImageFromBitmap();
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
    super.onSaveInstanceState(outState);
  }

  private void loadImageFromNetwork() {
    final String imageUrl = "http://46.101.199.221/comics/image/" + strip._id;

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
