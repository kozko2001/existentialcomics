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

package net.coscolla.comicstrip.ui.detail.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import net.coscolla.comicstrip.ui.detail.DetailStripPage;

public class DetailStripPageAdapter extends FragmentStatePagerAdapter {

  private final String[] data;

  public DetailStripPageAdapter(FragmentManager fragmentManager, String[] ids) {
    super(fragmentManager);
    data = ids;
  }

  @Override
  public Fragment getItem(int position) {
    String id = data[position];
    return DetailStripPage.newInstance(id);
  }

  @Override
  public int getCount() {
    return data.length;
  }
}
