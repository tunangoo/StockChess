package stock.android.chess.puzzle;

import android.content.UriMatcher;
import android.net.Uri;

import stock.chess.ChessPuzzleProvider;

public class MyPuzzleProvider extends ChessPuzzleProvider {

    static {
        AUTHORITY = "stock.android.chess.puzzle.MyPuzzleProvider";
        CONTENT_URI_PUZZLES = Uri.parse("content://" + AUTHORITY + "/puzzles");
        CONTENT_URI_PRACTICES = Uri.parse("content://" + AUTHORITY + "/practices");

        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, "puzzles", PUZZLES);
        sUriMatcher.addURI(AUTHORITY, "puzzles/#", PUZZLES_ID);

        sUriMatcher.addURI(AUTHORITY, "practices", PRACTICES);
        sUriMatcher.addURI(AUTHORITY, "practices/#", PRACTICES_ID);
    }
}
