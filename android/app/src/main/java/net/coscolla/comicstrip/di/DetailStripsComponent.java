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

package net.coscolla.comicstrip.di;

import net.coscolla.comicstrip.ui.detail.DetailStripActivity;
import net.coscolla.comicstrip.ui.detail.DetailStripPage;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = {
    DetailStripsModule.class
})
public interface DetailStripsComponent {
  void inject(DetailStripActivity activity);
  void inject(DetailStripPage page);
}
