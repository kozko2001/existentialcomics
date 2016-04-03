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

package net.coscolla.comicstrip.net.api;


import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

public interface ComicApi {
  @GET("/comics")
  Observable<ComicResults> listComics();

  @GET("/comics/{comicname}/{last_id}")
  Observable<StripResults> listStrips(@Path("comicname") String comicName, @Path("last_id") String lastId);

  @GET("/comics/image/{strip_id}")
  Observable<ResponseBody> stripImage(@Path("strip_id") String stripId);
}
