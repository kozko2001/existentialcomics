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

package net.coscolla.comicstrip.ui.comics;

import net.coscolla.comicstrip.entities.Comic;

import org.parceler.Parcel;

/**
 * Model for the select comics list
 *
 * Now is a bit unnecessary but is expected to have more data like if the
 * comic is favorite and be sorted by the time you see strips of this comic etc...
 */
@Parcel
public class ComicAdapterModel {
  public String name;
  public String id;
  public String url;
  public String image;

  public ComicAdapterModel() {

  }

  public ComicAdapterModel(Comic comic) {
    name = comic.name;
    id = comic.comic_id;
    url = comic.url;
    image = comic.image;
  }

}
