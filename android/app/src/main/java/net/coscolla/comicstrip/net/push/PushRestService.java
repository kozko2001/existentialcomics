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

package net.coscolla.comicstrip.net.push;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PushRestService {

  @POST("/register")
  Call<PushRegisterResponse> register(@Body PushRegisterRequestData request);

  @GET("/subscribe/{user_id}/{topic}")
  Call<SubcribeResult> subscribe(@Path("user_id") String userId,
                                @Path("topic") String topic);

  @GET("/ping/{user_id}")
  Call<SubcribeResult> ping(@Path("user_id") String userId);


}
