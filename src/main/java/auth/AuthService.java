package auth;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface AuthService {

    @Headers("Content-Type: text/plain")
    @POST("validate-token")
    Call<Void> validateToken(@Body String authToken);
}

