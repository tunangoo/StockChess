package stock.android.chess.lichessClient.pojo;

import com.google.gson.annotations.SerializedName;

public class UserEmail {
    @SerializedName("email")
    public String email;

    public UserEmail(String email) {
        this.email = email;
    }
}
