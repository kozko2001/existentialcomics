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

package net.coscolla.comicstrip.net.comic;

import net.coscolla.comicstrip.net.comic.api.entities.Strip;

public class UrlBuilder {

  private final String endpoint;

  public UrlBuilder(String endpoint) {
    this.endpoint = endpoint;
  }

  public String urlThumbnail(Strip strip) {
    return endpoint +  "comics/thumbnail/" + strip._id;
  }

  public String urlImage(String stripId) {
    return endpoint + "comics/image/" + stripId;
  }
}
