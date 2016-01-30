package net.coscolla.comicstrip.net;

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
}
