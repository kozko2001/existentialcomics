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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;

import net.coscolla.comicstrip.R;
import net.coscolla.comicstrip.di.Graph;
import net.coscolla.comicstrip.entities.Strip;
import net.coscolla.comicstrip.ui.detail.adapter.DetailStripPageAdapter;


public class DetailStripActivity extends AppCompatActivity {

  public static final String STRIP = "strip";
  public static final String IDS = "IDS";

  private String[] strip_ids;

  @Bind(R.id.pager) ViewPager viewPager;


  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_detail_strip);
    Graph.getInstance().getDetailStripComponent().inject(this);

    strip_ids = getIntent().getStringArrayExtra(IDS);

    ButterKnife.bind(this);

    setupPageAdapter();
  }

  private void setupPageAdapter() {
    DetailStripPageAdapter adapter = new DetailStripPageAdapter(getSupportFragmentManager(), strip_ids);
    viewPager.setAdapter(adapter);
    int index = findIndexForStripId(getCurrentStrip()._id);
    viewPager.setCurrentItem(index);
  }

  private int findIndexForStripId(String id) {
    for (int i = 0; i < strip_ids.length; i++) {
      if(id.equalsIgnoreCase(strip_ids[i])) {
        return i;
      }
    }
    return 0;
  }

  /**
   * Gets the first strip to be shown
   */
  private Strip getCurrentStrip() {
    return Parcels.unwrap(getIntent().getParcelableExtra(STRIP));
  }

}
