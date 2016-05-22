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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;

import net.coscolla.comicstrip.R;
import net.coscolla.comicstrip.di.Graph;
import net.coscolla.comicstrip.entities.Strip;
import net.coscolla.comicstrip.marketing.analytics.IAnalytics;
import net.coscolla.comicstrip.ui.detail.adapter.DetailStripPageAdapter;
import net.coscolla.comicstrip.usecases.DetailStripUseCase;

import org.parceler.Parcels;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;


public class DetailStripActivity extends AppCompatActivity {

  public static final String STRIP = "strip";
  public static final String IDS = "IDS";
  @Bind(R.id.pager) ViewPager viewPager;

  @Inject DetailStripUseCase useCase;
  @Inject IAnalytics analytics;

  private String[] strip_ids;
  private Strip currentStrip;
  private ShareActionProvider shareProvider;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_detail_strip);
    ButterKnife.bind(this);
    Graph.getInstance().getDetailStripComponent().inject(this);

    onRestoreSavedState(savedInstanceState);
    strip_ids = getIntent().getStringArrayExtra(IDS);

    setupPageAdapter();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    if(currentStrip != null) {
      outState.putParcelable(STRIP, Parcels.wrap(currentStrip));
    }
  }

  protected void onRestoreSavedState(Bundle savedInstanceState) {
    if(savedInstanceState != null && savedInstanceState.containsKey(STRIP)) {
      setCurrentStrip(Parcels.unwrap(savedInstanceState.getParcelable(STRIP)));
    }
    else if(savedInstanceState == null) {
      Strip strip = Parcels.unwrap(getIntent().getParcelableExtra(STRIP));
      setCurrentStrip(strip);
    }
  }

  private void setupPageAdapter() {
    DetailStripPageAdapter adapter = new DetailStripPageAdapter(getSupportFragmentManager(), strip_ids);
    viewPager.setAdapter(adapter);
    int index = findIndexForStripId(currentStrip._id);
    viewPager.setCurrentItem(index);
    viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
      @Override
      public void onPageSelected(int position) {
        String id = strip_ids[position];
        useCase.getStripById(id)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                strip -> setCurrentStrip(strip),
                e -> Timber.e(e, "Something went wrong when getting the %s strip", id));
      }
    });
  }

  private int findIndexForStripId(String id) {
    for (int i = 0; i < strip_ids.length; i++) {
      if(id.equalsIgnoreCase(strip_ids[i])) {
        return i;
      }
    }
    return 0;
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.detail_strip_menu, menu);
    MenuItem item = menu.findItem(R.id.menu_item_share);

    // Configures the share button
    Intent shareIntent = getShareIntent();

    shareProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
    shareProvider.setShareIntent(shareIntent);
    return true;
  }

  @NonNull
  private Intent getShareIntent() {
    Intent shareIntent = new Intent();
    shareIntent.setAction(Intent.ACTION_SEND);

    shareIntent.putExtra(Intent.EXTRA_TEXT, "See this strip: " +
        currentStrip.title + " from " + currentStrip.url + " via ComicStrip App");
    shareIntent.setType("text/plain");

    /*
    Not working properly, removed for release
    String imagePath = useCase.getShareImagePath(currentStrip);
    if(imagePath != null) {
      Uri imageUri = Uri.parse(imagePath);
      shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
      shareIntent.setType("image/png");
    }
    */
    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    return shareIntent;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if(item.getItemId() == R.id.open_browser) {
      Uri url = Uri.parse(currentStrip.url);

      if(url != null) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, url);
        startActivity(browserIntent);
      }
    }

    return super.onOptionsItemSelected(item);
  }

  public void setCurrentStrip(@NonNull Strip currentStrip) {
    this.currentStrip = currentStrip;
    setTitle(currentStrip.title);
    analytics.eventStripViewed(currentStrip);
    if(shareProvider != null) {
      shareProvider.setShareIntent(getShareIntent());
    }
  }
}
