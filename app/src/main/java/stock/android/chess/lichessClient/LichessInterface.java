package stock.android.chess.lichessClient;

import stock.android.chess.lichessClient.pojo.UserEmail;
import retrofit2.Call;
import retrofit2.http.GET;
public interface LichessInterface {
    @GET("/api/account/email")
    Call<UserEmail> doGetUserEmail();
}
