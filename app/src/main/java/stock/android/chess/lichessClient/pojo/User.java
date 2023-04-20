package stock.android.chess.lichessClient.pojo;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("name")
    public String name;
    @SerializedName("id")
    public String id;
    public User(String name, String id) {
        this.name = name;
        this.id = id;
    }
}
