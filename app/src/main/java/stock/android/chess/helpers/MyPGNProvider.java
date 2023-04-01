package stock.android.chess.helpers;

import android.content.UriMatcher;
import android.net.Uri;

import stock.chess.PGNProvider;

public class MyPGNProvider extends PGNProvider {

    static {
        AUTHORITY = "stock.android.chess.helpers.MyPGNProvider";
        CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/games");

        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, "games", GAMES);
        sUriMatcher.addURI(AUTHORITY, "games/#", GAMES_ID);
    }
}
